package stacker.alignment;

import stacker.ImageStackerMain;
import stacker.images.*;
import stacker.ProgressBar;

import java.io.IOException;

public abstract class AbstractAlignmentMethod {

    // Class representing a general framework for calculating alignment param
    // Derived classes just need to implement calculateOffsetParameters - to work out how a given pair of images are offset

    // States whether alignment should go through all pairs of images to explicitly work out, or just do one row and work out from that.

    public boolean allPairs = false;

    // Methods
    public abstract OffsetParameters calculateOffsetParameters(RGBImage img1, RGBImage img2, ProgressBar subtaskProgress);

    // Wrapper function
    public OffsetParameters[][] calculateOffsetParameterTable(String[] filePaths) throws IOException{
        // fills in progress bar with Dummy value if none provided.
        return calculateOffsetParameterTable(filePaths, new ProgressBar("Calculating OffsetParameterTable"));
    }

    public OffsetParameters[][] calculateOffsetParameterTable(String[] filePaths, ProgressBar progressBar) throws IOException{
        try
        {
            if (allPairs)
            {
                return allPairsMethod(filePaths, progressBar);
            } else
            {
                return oneRowMethod(filePaths, progressBar);
            }
        }catch (IOException e)
        {
            ImageStackerMain.MainLogger.error("I/O ERROR ACCESSING THE WORKING FOLDER DURING ALIGNMENT");
            System.out.println("[FATAL]: I/O ERROR ACCESSING WORKING FOLDER DURING ALIGNMENT ");
            e.printStackTrace();
            throw e;
        }
    }

    // Worker methods:
    private OffsetParameters[][] oneRowMethod(String[] filePaths, ProgressBar progressBar) throws IOException{

        // Stack all images relative to the first one
        ImageStackerMain.MainLogger.info("Calculating Offset Params - implicit method");
        System.out.println("Running main alignment function");

        OffsetParameters[][] offsetParameterTable = new OffsetParameters[filePaths.length][filePaths.length];

        // Calculate one row in the offset parameter table explicitly:
        RGBImage jImage = new RGBImage(filePaths[0]);
        progressBar.populateSubTaskList(offsetParameterTable[0].length, "Calculating pairwise offsets" ); // Fills in subtasks for each lengthy subtask

        for (int i = 0; i < offsetParameterTable[0].length; i++) {

            ImageStackerMain.MainLogger.debug("Initial fill: " + i);
            RGBImage iImage = new RGBImage(filePaths[i]);
            offsetParameterTable[0][i] = calculateOffsetParameters(jImage, iImage, progressBar.subTaskList.get(i));

            progressBar.subTaskList.get(i).setProgressPercent(100);
            progressBar.printPercent();

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

    private OffsetParameters[][] allPairsMethod(String[] filePaths, ProgressBar progressBar) throws IOException{
        // Stack all images relative to the first one
        ImageStackerMain.MainLogger.info("Calculating Offset Params - All pairs method");

        OffsetParameters[][] offsetParameterTable = new OffsetParameters[filePaths.length][filePaths.length];

        progressBar.populateSubTaskList(offsetParameterTable.length * offsetParameterTable[0].length, "Calculating all pairs of offsets"); // FIXME
        int current = 0;

        for (int j = 0; j < offsetParameterTable.length; j++) {
            RGBImage jImage = new RGBImage(filePaths[j]);
            for (int i = 0; i < offsetParameterTable[0].length; i++) {
                RGBImage iImage = new RGBImage(filePaths[i]);
                offsetParameterTable[j][i] = calculateOffsetParameters(jImage, iImage, progressBar.subTaskList.get(current));
                progressBar.subTaskList.get(current).setProgressPercent(100);
                current++;
            }
        }

        return offsetParameterTable;
    }

}
