package stacker.alignment;

import stacker.ImageStackerMain;
import stacker.images.GreyImage;
import stacker.images.RGBImage;

import java.io.IOException;

public class AlignmentMethodAllPairs extends AbstractAlignmentMethod{

    // Implements the slower way of aligning the images!

    @Override
    public OffsetParameters[][] calculateAllAlignments(String[] filePaths){

        // Stack all images relative to the first one
        ImageStackerMain.MainLogger.info("Calculating Offset Params - All pairs method");

        OffsetParameters[][] offsetParameterTable = new OffsetParameters[filePaths.length][filePaths.length];
        try
        {

            for (int j = 0; j < offsetParameterTable.length; j++) {
                RGBImage jImage = new RGBImage(filePaths[j]);
                for (int i = 0; i < offsetParameterTable[0].length; i++) {
                    RGBImage iImage = new RGBImage(filePaths[i]);
                    offsetParameterTable[j][i] = new OffsetParameters(jImage, iImage, 0);  // Calculates the  change method back
                }
            }


        } catch (IOException e) {
            ImageStackerMain.MainLogger.fatal("I/O ERROR ACCESSING THE WORKING FOLDER");
            System.out.println("[FATAL]: I/O ERROR ACCESSING WORKING FOLDER");
            e.printStackTrace();
            System.exit(404);
            // TODO - not make it exit!
        }

        return offsetParameterTable;

    }

    @Override
    public OffsetParameters calculateOffsetParameters(GreyImage img1, GreyImage img2) {
        return null;
    }


}
