package stacker.alignment;

import stacker.ImageStackerMain;
import stacker.images.GreyImage;
import stacker.images.RGBImage;

import java.io.IOException;

public class AlignmentMethodDefault extends AbstractAlignmentMethod{

    // Implements the speedy way of aligning the images!

    @Override
    public OffsetParameters[][] calculateAllAlignments(String[] filePaths){

        // Stack all images relative to the first one
        ImageStackerMain.MainLogger.info("Calculating Offset Params - implicit method");
        System.out.println("Running main alignment function");

        OffsetParameters[][] offsetParameterTable = new OffsetParameters[filePaths.length][filePaths.length];

        try {
            RGBImage jImage = new RGBImage(filePaths[0]);
            for (int i = 0; i < offsetParameterTable[0].length; i++) {
                ImageStackerMain.MainLogger.debug("Initial fill: " + i);
                RGBImage iImage = new RGBImage(filePaths[i]);
                offsetParameterTable[0][i] = new OffsetParameters(jImage, iImage, 0);  // Calculates the  change method back
            }
            for (int j = 1; j < offsetParameterTable.length; j++) {
                for (int i = 0; i < offsetParameterTable[0].length; i++) {
                    ImageStackerMain.MainLogger.debug("Full fill (j,i): " + j + ", " + i);
                    int x = offsetParameterTable[0][i].getX() - offsetParameterTable[0][j].getX();
                    int y = offsetParameterTable[0][i].getY() - offsetParameterTable[0][j].getY();
                    offsetParameterTable[j][i] = new OffsetParameters(x, y, 0);
                }
            }
        } catch (IOException e) {
            ImageStackerMain.MainLogger.fatal("I/O ERROR ACCESSING THE WORKING FOLDER");
            System.out.println("[FATAL]: I/O ERROR ACCESSING WORKING FOLDER");
            e.printStackTrace();
            System.exit(404);
        }
        // TODO - maybe make this throw out instead?

        return offsetParameterTable;
    }

    @Override
    public OffsetParameters calculateOffsetParameters(GreyImage img1, GreyImage img2) {
        return null;
        // TODO -
    }


}
