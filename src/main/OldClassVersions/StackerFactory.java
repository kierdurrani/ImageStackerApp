import com.sun.scenario.effect.Offset;
import jdk.nashorn.internal.ir.CatchNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StackerFactory {

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
        int[][][] calcTable = new int[100][img.getWidth()+ xMaxOffset - xMinOffset][imagePaths.length]; // Size  ~ 4*4000 * 4*100 * 4*n = n*25MB , also requires ~ n*width/100 IOs = 20*n

        // Load each img from disk, and put data into calcTable
        // TODO: Change bound at the end.
        int numImgs = 3;
        for(int imgNumber = 0 ; imgNumber < numImgs ; imgNumber++) {
            try {
                file = new File(imagePaths[imgNumber]);
                img = ImageIO.read(file);
            } catch (IOException e){
                ImageStackerMain.MainLogger.fatal("IO Error During Calculation");
                System.exit(112);
            }

            int xOffset = offsetParameterTable[0][imgNumber].getX();
            int yOffset = offsetParameterTable[0][imgNumber].getY();

            // Now collate aligned values into the calcTable // TODO check bounds are safe - they are not! - hit the -ve offset
            //Note: MinOffsets are -ve, so the bounds are +ve
            // TODO: Fix bounds for y: It can end up being -ve!
            System.out.println("Offset:"+ xOffset + ", " +yOffset);
            for (int y = 0; y < 100; y++) {
                System.out.println(y);
                if(y-yOffset<1){continue;}
                for (int x = 0 ; x < img.getWidth()+ xMaxOffset - xMinOffset; x++) {
                    if(x-xOffset<1){continue;}
                    if(x>img.getWidth()-1){break;}
                    System.out.println("Coords:"+ x + ", " +y);
                    calcTable[y][x][imgNumber] = img.getRGB(x - xOffset, y - yOffset);
                }
            }

            //            for (int y = Math.max(yOffset+1,0); y < yOffset + 100; y++) {
//                for (int x = xOffset + 1; x < (img.getWidth() + xOffset); x++) {
//                    //Note: MinOffsets are -ve, so the bounds are +ve
//                    System.out.println("x="+x+", y=" + y);
//                    // TODO: Fix bounds for y: It can end up being -ve!
//                    calcTable[y][x][imgNumber] = img.getRGB(x - xOffset, y - yOffset);
//                }
//            }
        }

        // Numerical stacking operation
        for(int imgNumber = 0 ; imgNumber < imagePaths.length ; imgNumber++) {
            for (int y = 0 ; y < 100; y++) {
                for (int x = 0; x < finalValues[y].length; x++) {
                    finalValues[y][x][0] += ((calcTable[y][x][imgNumber] & 16711680) >> 16);
                    finalValues[y][x][1] += ((calcTable[y][x][imgNumber] & 65280) >> 8);
                    finalValues[y][x][2] += ((calcTable[y][x][imgNumber] & 255));
                }
            }
        }
//        RGBImage rgbImage = new RGBImage(finalValues);
//        GreyImage greyImage = rgbImage.makeGreyImage();
//        int[][] greyArray = greyImage.getGreyArray();
//        for(int imgNumber = 0 ; imgNumber < imagePaths.length ; imgNumber++) {
//            for (int y = 0 ; y < 100; y++) {
//                for (int x = 0; x < finalValues[y].length; x++) {
//                    System.out.print(greyArray[y][x] + ",");
//                }
//                System.out.println();
//            }
//        }

        return  new RGBImage(finalValues);
    }

}
