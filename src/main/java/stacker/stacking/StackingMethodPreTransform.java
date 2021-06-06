package stacker.stacking;

import stacker.ImageStackerMain;
import stacker.ProgressBar;
import stacker.alignment.OffsetParameters;
import stacker.alignment.StackableImages;
import stacker.images.RGBImage;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class StackingMethodPreTransform extends AbstractStackingMethod {

    private String workingDirectory;
    private String stackName;
   // private ArrayList<String> transformedImagePaths = new ArrayList<>();

    // Populated by transformStackableImages(), but consumed by stackTransformedImages()
    private int canvasWidth;
    private int canvasHeight;

    // Constructor
    public StackingMethodPreTransform(String workingDirectory, String stackName){
        this.workingDirectory = workingDirectory;
        this.stackName = stackName;
    }

    @Override
    public RGBImage stackImages(StackableImages stackableImages, ProgressBar progressBar) throws IOException{

        ArrayList<String> transformedImagePaths = transformAllImages(stackableImages);
        RGBImage stackedImage = stackTransformedImages(transformedImagePaths);
        return stackedImage;
    }


    public ArrayList<String> transformAllImages(StackableImages stackableImages) throws IOException
    {

        // Use first row as the reference row
        OffsetParameters[][] offsetParameterTable = stackableImages.getOffsetParameterTable();
        String[] imagePaths = stackableImages.getImagePaths();

        //region - Find Extremal offsetParams
        int xMaxOffset = 0;
        int yMaxOffset = 0;
        int xMinOffset = 0;
        int yMinOffset = 0;

        for (OffsetParameters offset : offsetParameterTable[0]) {
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
        //endregion

        // TODO - more robust handling of images of different sizes & rotations?
        BufferedImage img = ImageIO.read(new File(imagePaths[0]));
        canvasWidth = xMaxOffset - xMinOffset + img.getWidth();
        canvasHeight = yMaxOffset - yMinOffset + img.getHeight();

        //region - Transform images and save
        ArrayList<String> transformedImagePaths = new ArrayList<>();
        for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {

            img = ImageIO.read(new File(imagePaths[imgNumber]));

            int xOffset = offsetParameterTable[0][imgNumber].getX();
            int yOffset = offsetParameterTable[0][imgNumber].getY();
            double theta = offsetParameterTable[0][imgNumber].getTheta();

            // Collate aligned values into the bufferTable
            BufferedImage transformedBufferedImage = transformBufferedImage(img, xOffset - xMinOffset, yOffset - yMinOffset, theta, canvasWidth, canvasHeight);

            String fileName = workingDirectory + "\\" + stackName + imgNumber + ".png";

            try {
                File imageFile = new File(fileName);
                javax.imageio.ImageIO.write(transformedBufferedImage, "png", imageFile);
                transformedImagePaths.add(fileName);
            } catch (IOException e) {
                ImageStackerMain.MainLogger.error("IO OUT ERROR WHILE WRITING TO DISK");
                System.out.println("[StackingMethodPreTransform]: IO Failure while writing" + fileName + "image to disk");
            }
        }
        //endregion

        return transformedImagePaths;
    }

    public RGBImage stackTransformedImages(ArrayList<String> transformedImagePaths) throws IOException
    {

        int pageSize = 400;
        int[][][] finalValues = new int[canvasHeight][canvasWidth][3];
        int[][][] bufferTable = new int[pageSize][canvasWidth][transformedImagePaths.size()]; // Size  ~ 4*4000 * 4*100 * 4*n = n*25MB , also requires ~ n*width/100 IOs = 20*n


        System.out.println("GOOOOOO!");
        // Load in only {pageSize} y-values at a time to prevent memory overflow.
        for (int yPagingOffset = 0; yPagingOffset < canvasHeight; yPagingOffset += pageSize) {

            System.out.println(yPagingOffset);

            //region - put values into bufferTable
            for (int imgNumber = 0; imgNumber < transformedImagePaths.size(); imgNumber++)
            {
                BufferedImage currentImage = ImageIO.read( new File(transformedImagePaths.get(imgNumber)) );

                // Collate aligned values into the bufferTable
                for (int y = 0 + yPagingOffset; y < Math.min(canvasHeight, pageSize + yPagingOffset); y++) // was previously Math.min(canvasHeight, 100 + yPagingOffset -1) - why?!
                {
                    if(imgNumber == 0) {
                        System.out.println(y);
                    }

                    for (int x = 0; x < canvasWidth; x++)
                    {
                        bufferTable[y - yPagingOffset][x][imgNumber] = currentImage.getRGB(x, y);
                        // TODO - move the calculation of the final color here to speed it up?
                    }
                }
            }
            //endregion

            //region - take values out of the buffer and calculate final brightness
            for (int y = 0; y < Math.min(pageSize, canvasHeight - yPagingOffset); y++)
            {
                for (int x = 0; x < canvasWidth; x++)
                {
                    finalValues[y + yPagingOffset][x] = calculateFinalRGBValues(bufferTable[y][x]);
                }
            }
            //endregion

        }

        RGBImage finalImage = new RGBImage(finalValues);
        return finalImage;
    }

    private int[] calculateFinalRGBValues(int[] RBGValueOfImages)
    {
        // Input is an array of the bitwise RGB value of all images
        Arrays.sort(RBGValueOfImages);

        int[][] colorImageArray = new int[3][RBGValueOfImages.length];

        for (int imgNumber = 0; imgNumber < RBGValueOfImages.length ; imgNumber++)
        {
            colorImageArray[0][imgNumber] = (RBGValueOfImages[imgNumber] & 16711680) >> 16;
            colorImageArray[1][imgNumber] = (RBGValueOfImages[imgNumber] & 65280) >> 8;
            colorImageArray[2][imgNumber] = (RBGValueOfImages[imgNumber] & 255);
        }

        Arrays.sort( colorImageArray[0] );
        Arrays.sort( colorImageArray[1] );
        Arrays.sort( colorImageArray[2] );

        int[] outputRGB = new int[3];

        int sample = (RBGValueOfImages.length/2) -3;
        outputRGB[0] = colorImageArray[0][sample];
        outputRGB[1] = colorImageArray[1][sample];
        outputRGB[2] = colorImageArray[2][sample];

        // float meanPower = (float) totalPower / RBGValueOfImages.length;
        // int outputBrightness = (int) (255.0 * (Math.tanh(meanPower / 50.0) + meanPower / 1000.0) / 1.26);
        return outputRGB;
    }

    // Takes a buffered image, and transforms it inside a new buffered image with specified dimension
    public static BufferedImage transformBufferedImage(BufferedImage img, int XOffset, int YOffset, double theta, int canvasWidth, int canvasHeight)
    {

        // Rotations are centred around the top left corner. To rotate about the centre, rotate about top left, translate the new centre of the image back to (0,0)
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        double postRotTranslationX = -(cos * img.getWidth() / 2 - sin * img.getHeight() / 2) + img.getWidth() / 2;
        double postRotTranslationY = -(cos * img.getHeight() / 2 + sin * img.getWidth() / 2) + img.getHeight() / 2;

        // Create the affine transformation operator. Add a bit of offset to prevent clipping.
        AffineTransform affineTransform = new AffineTransform(cos, sin, -sin, cos, postRotTranslationX + XOffset, postRotTranslationY + YOffset);
        AffineTransformOp affineTransOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC);

        // Perform the transformation on the image
        BufferedImage transformedImage = new BufferedImage(canvasWidth, canvasHeight, img.getType());
        affineTransOp.filter(img, transformedImage);

        return transformedImage;
    }

}
