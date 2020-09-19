import com.sun.scenario.effect.Offset;
import jdk.nashorn.internal.ir.CatchNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StackerFactory3 {

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
        int[][][] bufferTable = new int[100][img.getWidth()+ xMaxOffset - xMinOffset][imagePaths.length]; // Size  ~ 4*4000 * 4*100 * 4*n = n*25MB , also requires ~ n*width/100 IOs = 20*n

        // Load in only 100 y-values at a time to prevent memory overflow.
        for(int yRange = 0; yRange < img.getHeight() + yMaxOffset - yMinOffset; yRange += 100 ) {
            for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {
                file = new File(imagePaths[imgNumber]);
                img = ImageIO.read(file);

                int xOffset = offsetParameterTable[0][imgNumber].getX();
                int yOffset = offsetParameterTable[0][imgNumber].getY();

                // TODO: remove if statement and put the logic in the bounds.
                // Collate aligned values into the calcTable
                for (int y = 0; y < 100; y++) {
                    System.out.println(y + yRange);  // Note: MinOffsets are -ve, so the bounds are +ve
                    if (y - yOffset + yRange < 1) {
                        continue;
                    }
                    if(y - yOffset + yRange > img.getHeight() - 1 ){
                        System.out.println("yBreak");
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
                        bufferTable[y][x][imgNumber] = img.getRGB(x - xOffset, y - yOffset + yRange);
                    }
                }
            }
            // TODO: fix tripling in bottom half of image
            System.out.println("finalValues= " + finalValues.length);
            System.out.println("calcTable= " + bufferTable.length);
            // Numerical stacking operation
            for (int imgNumber = 0; imgNumber < imagePaths.length; imgNumber++) {
                for (int y = 0; y < 100; y++) {
                    if(y + yRange >= finalValues.length){
                        break;
                    }
                    for (int x = 0; x < finalValues[y].length; x++) {
                        finalValues[y + yRange][x][0] += ((bufferTable[y][x][imgNumber] & 16711680) >> 16)/imagePaths.length;
                        finalValues[y + yRange][x][1] += ((bufferTable[y][x][imgNumber] & 65280) >> 8)/imagePaths.length;
                        finalValues[y + yRange][x][2] += ((bufferTable[y][x][imgNumber] & 255))/imagePaths.length;
                    }
                }
            }
            for (int x = 0; x < finalValues[0].length; x++) {
 //               finalValues[yRange][x][0] += 255;
           //     finalValues[yRange][x][1] = 255;
//                finalValues[yRange][x][2] += 255;
            }
        }
        return  new RGBImage(finalValues);
    }

}
