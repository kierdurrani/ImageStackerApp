package stacker.alignment;

import stacker.ImageStackerMain;
import stacker.images.*;

import java.io.IOException;

public abstract class AbstractAlignmentMethod {

    // Class representing a general framework for calculating alignment param
    // Derived classes just need to

    // States whether alignment should go through all pairs of images to explicitly work out, or just do one row and work out from that.
    boolean allPairs = false;

    // Methods
    public abstract OffsetParameters calculateOffsetParameters(RGBImage img1, RGBImage img2);

    public OffsetParameters[][] calculateAllAlignments(String[] filePaths) throws IOException{

        try
        {
            if (allPairs) {
                return allPairsMethod(filePaths);
            } else {
                return oneRowMethod(filePaths);
            }
        }catch (IOException e)
        {
            ImageStackerMain.MainLogger.error("I/O ERROR ACCESSING THE WORKING FOLDER DURING ALIGNMENT");
            System.out.println("[FATAL]: I/O ERROR ACCESSING WORKING FOLDER DURING ALIGNMENT ");
            e.printStackTrace();
            throw e;
        }
    }

    private OffsetParameters[][] oneRowMethod(String[] filePaths) throws IOException{

        // Stack all images relative to the first one
        ImageStackerMain.MainLogger.info("Calculating Offset Params - implicit method");
        System.out.println("Running main alignment function");

        OffsetParameters[][] offsetParameterTable = new OffsetParameters[filePaths.length][filePaths.length];

        // Calculate one row in the offset parameter table explicitly:
        RGBImage jImage = new RGBImage(filePaths[0]);
        for (int i = 0; i < offsetParameterTable[0].length; i++) {
            ImageStackerMain.MainLogger.debug("Initial fill: " + i);
            RGBImage iImage = new RGBImage(filePaths[i]);
            offsetParameterTable[0][i] = calculateOffsetParameters(jImage, iImage);  // Calculates the  change method back
        }

        // Calculate all other rows in the table by adding the params in the image:
        for (int j = 1; j < offsetParameterTable.length; j++) {
            for (int i = 0; i < offsetParameterTable[0].length; i++) {
                ImageStackerMain.MainLogger.debug("Full fill (j,i): " + j + ", " + i);
                int x = offsetParameterTable[0][i].getX() - offsetParameterTable[0][j].getX();
                int y = offsetParameterTable[0][i].getY() - offsetParameterTable[0][j].getY();
                offsetParameterTable[j][i] = new OffsetParameters(x, y, 0);
            }
        }

        return offsetParameterTable;
    }

    private OffsetParameters[][] allPairsMethod(String[] filePaths) throws IOException{
        // Stack all images relative to the first one
        ImageStackerMain.MainLogger.info("Calculating Offset Params - All pairs method");

        OffsetParameters[][] offsetParameterTable = new OffsetParameters[filePaths.length][filePaths.length];

        for (int j = 0; j < offsetParameterTable.length; j++) {
            RGBImage jImage = new RGBImage(filePaths[j]);
            for (int i = 0; i < offsetParameterTable[0].length; i++) {
                RGBImage iImage = new RGBImage(filePaths[i]);
                offsetParameterTable[j][i] = calculateOffsetParameters(jImage, iImage);  // Calculates the  change method back
            }
        }

        return offsetParameterTable;
    }

    // TODO - make bounds non arbitrary. Then move to child classes
    protected int crossCorrelation(int[][] img1, int[][] img2, int xOffset, int yOffset) {
        // Assert offsets are bounded between 0-75% of dimension.

        int correlation = 0;

        // Having a -ve offset is the same as offsetting the OTHER picture by a positive amount.
        // Do this by inverting the bounds of the for loop

        // TODO: FIX THE BOUNDS ON THE FOR LOOPS. - 0.75 factor is arbitrary
        int yLimit = (int) (0.75 * Math.min(img2.length, img1.length));
        int xLimit = (int) (0.75 * Math.min(img2[0].length, img1[0].length));

        if (xOffset >= 0) {
            if (yOffset >= 0) {
                //  +x +y
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y + yOffset][x + xOffset] * img2[y][x];
                    }
                }
            } else {
                //  +x -y
                yOffset = -yOffset;
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y][x + xOffset] * img2[y + yOffset][x] ;
                    }
                }
            }
        } else {
            // then -ve x
            xOffset = -xOffset;
            if (yOffset >= 0) {
                //  -x +y
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y + yOffset][x] * img2[y][x + xOffset];
                    }
                }
            } else {
                // -x -y
                yOffset = -yOffset;
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y][x] * img2[y + yOffset][x + xOffset];
                    }
                }
            }
        }
        return (correlation);
    }




}
