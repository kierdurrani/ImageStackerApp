import com.sun.scenario.effect.Offset;
import jdk.nashorn.internal.ir.CatchNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StackerFactory2 {

    public static RGBImage stackImage(StackableImages stackableImages) throws IOException {
        long time = System.currentTimeMillis();
        ImageStackerMain.MainLogger.info("Starting the Stacking Process.");

        OffsetParameters[][]  offsetParameterTable = stackableImages.getOffsetParameterTable();
        String[] imagePaths = stackableImages.getImagePaths();

        File file = new File(imagePaths[0]);
        BufferedImage img = ImageIO.read(file);

        // Find extremal offsetParams
        int xMaxOffset = 0;
        int yMaxOffset = 0;
        int xMinOffset = 0;
        int yMinOffset = 0;

        // Since everything is done relative to 1st img, we dont need a double loop?
        for(OffsetParameters offset : offsetParameterTable[0] ) {
            if(offset.getX() > xMaxOffset){
                xMaxOffset = offset.getX();
            }
            if(offset.getY() > yMaxOffset){
                yMaxOffset = offset.getY();
            }
            if(offset.getX() < xMinOffset){
                xMinOffset = offset.getX();
            }
            if(offset.getY() < yMinOffset){
                yMinOffset = offset.getY();
            }
        }

        // Table of final values and Temporary calculation table
        int[][][] finalValues = new int[img.getHeight() + yMaxOffset -yMinOffset][img.getWidth()+ xMaxOffset - xMinOffset][3];
        int[][][] calcTable = new int[100][img.getWidth()+ xMaxOffset - xMinOffset][imagePaths.length]; // Size  ~ 4*4000 * 4*100 * 4*n = n*25MB , also requires ~ n*width/100 IOs = 20*n

        // Load in only 100 y-values at a time to prevent memory overflow.
        for(int yRange = 0; yRange < img.getWidth() + xMaxOffset - xMinOffset; yRange += 100 ) {
            for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {
                try {
                    file = new File(imagePaths[imgNumber]);
                    img = ImageIO.read(file);
                } catch (IOException e) {
                    ImageStackerMain.MainLogger.fatal("IO Error During Calculation");
                    System.exit(112);
                }

                int xOffset = offsetParameterTable[0][imgNumber].getX();
                int yOffset = offsetParameterTable[0][imgNumber].getY();

                // Collatge aligned values into the calcTable
                // TODO: remove if statement and stick the logic in the bounds.
                for (int y = 0; y < 100; y++) {
                    System.out.println(y + yRange);  // Note: MinOffsets are -ve, so the bounds are +ve
                    if (y - yOffset < 1) {
                        continue;
                    }
                    if(y - yOffset + yRange > img.getHeight() - 1 ){
                        break;
                    }
                    for (int x = 0; x < img.getWidth() + xMaxOffset - xMinOffset; x++) {
                        if (x - xOffset < 1) {
                            continue;
                        }
                        if (x > img.getWidth() - 1) {
                            break;
                        }
                        // System.out.println("Coords:" + x + ", " + y);
                        calcTable[y][x][imgNumber] = img.getRGB(x - xOffset, y - yOffset + yRange);
                    }
                }
            }
            // TODO: fix tripling in bottom half of image
            System.out.println("finalValues= " + finalValues.length);
            System.out.println("calcTable= " + calcTable.length);
            // Numerical stacking operation
            for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {
                for (int y = 0; y < 100; y++) {
                    if(y + yRange >= finalValues.length){break;}
                    for (int x = 0; x < finalValues[y].length; x++) {
                        finalValues[y + yRange][x][0] += ((calcTable[y][x][imgNumber] & 16711680) >> 16)/imagePaths.length;
                        finalValues[y + yRange][x][1] += ((calcTable[y][x][imgNumber] & 65280) >> 8)/imagePaths.length;
                        finalValues[y + yRange][x][2] += ((calcTable[y][x][imgNumber] & 255))/imagePaths.length;
                    }
                }
            }
        }
        return  new RGBImage(finalValues);
    }

}
