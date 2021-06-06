package stacker.stacking;

import stacker.ImageStackerMain;
import stacker.ProgressBar;
import stacker.alignment.OffsetParameters;
import stacker.alignment.StackableImages;
import stacker.images.RGBImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class StackingMethodOld extends AbstractStackingMethod {

    @Override
    public RGBImage stackImages(StackableImages stackableImages, ProgressBar progressBar) throws IOException {

        long time = System.currentTimeMillis();
        ImageStackerMain.MainLogger.info("Starting the Stacking Process.");

        OffsetParameters[][] offsetParameterTable = stackableImages.getOffsetParameterTable();
        String[] imagePaths = stackableImages.getImagePaths();

        File file = new File(imagePaths[0]);
        BufferedImage img = ImageIO.read(file);

        // Find extremal offsetParams
        int xMaxOffset = 0;
        int yMaxOffset = 0;
        int xMinOffset = 0;
        int yMinOffset = 0;
        for (OffsetParameters offset : offsetParameterTable[0])
        {
            if (offset.getX() > xMaxOffset) {
                xMaxOffset = offset.getX();
            }
            if (offset.getY() > yMaxOffset) {
                yMaxOffset = offset.getY();
            }
            if (offset.getX() < xMinOffset) {
                xMinOffset = offset.getX();
            }
            if (offset.getY() < yMinOffset) {
                yMinOffset = offset.getY();
            }
        }

        // Table of final values and Temporary calculation table
        int[][][] finalValues = new int[img.getHeight() + yMaxOffset - yMinOffset][img.getWidth() + xMaxOffset - xMinOffset][3];
        int[][][] bufferTable = new int[100][img.getWidth() + xMaxOffset - xMinOffset][imagePaths.length]; // Size  ~ 4*4000 * 4*100 * 4*n = n*25MB , also requires ~ n*width/100 IOs = 20*n

        // Load in only 100 y-values at a time to prevent memory overflow.
        // for (int yRange = 2000; yRange < img.getHeight() + yMaxOffset - yMinOffset; yRange += 100) {
        for (int yRange = img.getHeight() + yMaxOffset - yMinOffset ; yRange > 0; yRange -= 100) {
            for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {
                file = new File(imagePaths[imgNumber]);
                img = ImageIO.read(file);

                int xOffset = offsetParameterTable[0][imgNumber].getX();
                int yOffset = offsetParameterTable[0][imgNumber].getY();

                // Collate aligned values into the bufferTable
                for (int y = Math.max(0, 1 + yOffset - yRange); y < Math.min(100, img.getHeight() + yOffset - yRange - 1); y++) {
                    System.out.println(y + yRange);
                    for (int x = Math.max(0, 1 + xOffset); x < Math.min(img.getWidth() + xMaxOffset - xMinOffset, img.getWidth() + xOffset - 1); x++) {
                        bufferTable[y][x][imgNumber] = img.getRGB(x - xOffset, y - yOffset + yRange);
                    }
                }
                img = null;
            }

            // TODO: fix tripling in bottom half of image


            for (int y = 0; y < 100; y++) {
                if (y + yRange >= finalValues.length) {
                    break;
                }
                for (int x = 0; x < finalValues[y].length; x++) {
                    int[][] colourImageArray = new int[3][imagePaths.length];
                    for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {
                        colourImageArray[0][imgNumber] = ((bufferTable[y][x][imgNumber] & 16711680) >> 16);
                        colourImageArray[1][imgNumber] = ((bufferTable[y][x][imgNumber] & 65280) >> 8);
                        colourImageArray[2][imgNumber] = ((bufferTable[y][x][imgNumber] & 255));
                    }
                    finalValues[y + yRange][x][0] = calculateFinalBrightness(colourImageArray[0]);
                    finalValues[y + yRange][x][1] = calculateFinalBrightness(colourImageArray[1]);
                    finalValues[y + yRange][x][2] = calculateFinalBrightness(colourImageArray[2]);
                }
            }


//            for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {
//                for (int y = 0; y < 100; y++) {
//                    if (y + yRange >= finalValues.length) {
//                        break;
//                    }
//                    for (int x = 0; x < finalValues[y].length; x++) {
//                        finalValues[y + yRange][x][0] += ((bufferTable[y][x][imgNumber] & 16711680) >> 16);
//                        finalValues[y + yRange][x][1] += ((bufferTable[y][x][imgNumber] & 65280) >> 8);
//                        finalValues[y + yRange][x][2] += ((bufferTable[y][x][imgNumber] & 255));
//                    }
//                }
//            }
        }


        return new RGBImage(finalValues);
    }

    // Helper Functions
    private static int normaliseBrightness(int totalPower, int imgsUsed) {

        float fPower = (float) totalPower / imgsUsed;
        int y = (int) (255.0*(Math.tanh( fPower / 50.0) + fPower/1000.0)/1.26);

        return y;
    }

    private static int calculateFinalBrightness(int[] inputBrightness){

        Arrays.sort(inputBrightness);
        int outputBrightness = 0;
        for (int x = 0; x < inputBrightness.length - 10; x++) {
            outputBrightness += inputBrightness[x];
        }
        int imgsUsed = (inputBrightness.length - 10);
        outputBrightness = normaliseBrightness(outputBrightness, imgsUsed);
        return outputBrightness;
    }

    // TODO - Work out if this method is actually useful / distinct from the one above?
    // TODO: make this safe so that images can be different sizes? Use a working folder.
    public RGBImage stackAll(StackableImages imagesToStack) throws IOException {

        String[] imagePaths =  imagesToStack.getImagePaths();
        OffsetParameters[][] offsetParameterTable =  imagesToStack.getOffsetParameterTable();

        long time = System.currentTimeMillis();
        System.out.println("Starting the Stacking Process... ");
        ImageStackerMain.MainLogger.info("Starting the Stacking Process.");

        int[][][][] imageList = new int[imagePaths.length][][][];
        for (int imageNumber = 0; imageNumber < imageList.length; imageNumber++) {
            imageList[imageNumber] = (new RGBImage(imagePaths[imageNumber])).getRgbArray();  // WARNING: May
        }

        int[][][] finalBrightness = new int[imageList[0].length][imageList[0][0].length][3];
        // Use the first image in the stack as a reference, and then stack all the others using offset compared to first.
        // Iterate over all pictures:
        for (int imageNumber = 0; imageNumber < imageList.length; imageNumber++) {
            int xOffset = offsetParameterTable[0][imageNumber].getX();
            int yOffset = offsetParameterTable[0][imageNumber].getY();

            // TODO: Make this always work, rather than just arbitrary factor of 0.95 - Need to go upto maximum offset or something?
            for (int j = Math.abs(yOffset); j < (int) (0.95 * (imageList[imageNumber].length)); j++) {
                for (int i = Math.abs(xOffset); i < (int) (0.95 * (imageList[imageNumber][j].length)); i++) {
                    for (int colour = 0; colour < 3; colour++) {
                        finalBrightness[j + yOffset][i + xOffset][colour] += imageList[imageNumber][j][i][colour];
                    }
                }
            }
        }
        // Normalise Brightness
        // TODO: make less shit
        double exp = 0.93;
        for (int j = 0; j < finalBrightness.length; j++) {
            for (int i = 0; i < finalBrightness[0].length; i++) {
                int totBrightness = 0;
                for (int colour = 0; colour < 3; colour++) {
//                    totBrightness +=  finalBrightness[j][i][colour];
                    finalBrightness[j][i][colour] = (int) Math.pow(Math.pow(255.0, 1 / exp - 1.0) * finalBrightness[j][i][colour] / (offsetParameterTable.length), exp);
                }

//                totBrightness /= 3;
//                int brightnessMultiplier =  Math.pow((totBrightness *  offsetParameterTable.length),
//                for (int colour = 0; colour < 3; colour++) {
//                    finalBrightness[j][i][colour] = (int) Math.pow(245.0 * finalBrightness[j][i][colour] / (totBrightness *  offsetParameterTable.length), 0.7);
//                }

            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Finished the Stacking Process in: " + time + "ms");
        return (new RGBImage(finalBrightness));
    }

}
