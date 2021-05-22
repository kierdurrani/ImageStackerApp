package stacker;

import java.io.*;
import java.text.ParseException;
import java.util.Stack;

public class StackableImages {

    private String[] imagePaths;
    private OffsetParameters[][] offsetParameterTable;

    // Constructor: Takes array of imagePaths & populates them.
    public StackableImages(String[] imagePaths) {
        this.imagePaths = imagePaths;
        this.populateOffsetParameters2();
    }

    // Constructor: Import Stacking Parameters from a text file.
    public StackableImages(String filePath) {
        ImageStackerMain.MainLogger.info("Creating StackableImage object from file import: " + filePath);
        try {
            // Read data from file
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Parse image paths in 1st line -  ";" delimited
            String line = reader.readLine();
            String[] Paths = line.split(";");
            this.imagePaths = Paths;

            // All subsequent lines are offset coords seperated by "," and ";"
            offsetParameterTable = new OffsetParameters[imagePaths.length][imagePaths.length];
            for (int i = 0; i < imagePaths.length; i++) {
                line = reader.readLine();
                String[] lineOfCoords = line.split(";");
                for (int j = 0; j < imagePaths.length; j++) {
                    String[] coords = lineOfCoords[j].split(",");
                    offsetParameterTable[i][j] = new OffsetParameters(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                }
            }

            // Validation - isConsistent will throw if table isnt initalized. Warn if not consistent.
            if (!this.isConsistent()) {
                ImageStackerMain.MainLogger.warn("Import of stackableImage  succeeded but was not found to be consistent");
            }
            for (String imagePath : imagePaths) {
                File f = new File(imagePath);
                if (!f.exists()) {
                    ImageStackerMain.MainLogger.warn("Image file not found in expected location. Please correct before continuing: " + filePath);
                    // TODO - catch properly?
                }
            }

            // TODO -  verify the table isnt transposed!
            // TODO -  verify error handling is graceful: Catch out of bounds, perform validate on coords, verify non null table entries, catch parse errors, verify images exist?
        } catch (IOException e) {
            ImageStackerMain.MainLogger.error("Error: " + e);
            ImageStackerMain.MainLogger.error("Import of StackableImage File Failed: " + filePath);
            System.exit(111);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Caused by bad

        } catch (NumberFormatException e) {
            ImageStackerMain.MainLogger.error("Error Parsing Numbers during StackableImages Import of: " + filePath);

        } catch (NullPointerException e) {

        }
        ImageStackerMain.MainLogger.info("Write to disk successful.");
    }

    public void populateOffsetParameters() {
        ImageStackerMain.MainLogger.info("Calculating Offset Params");
        this.offsetParameterTable = new OffsetParameters[imagePaths.length][imagePaths.length];
        try {
            for (int j = 0; j < offsetParameterTable.length; j++) {
                RGBImage jImage = new RGBImage(imagePaths[j]);
                for (int i = 0; i < offsetParameterTable[0].length; i++) {
                    RGBImage iImage = new RGBImage(imagePaths[i]);
                    offsetParameterTable[j][i] = new OffsetParameters(jImage, iImage, 0);  // Calculates the  change method back
                }
            }
        } catch (IOException e) {
            ImageStackerMain.MainLogger.fatal("I/O ERROR ACCESSING THE WORKING FOLDER");
            System.out.println("[FATAL]: I/O ERROR ACCESSING WORKING FOLDER");
            e.printStackTrace();
            System.exit(404);
        }
    }


    public void populateOffsetParameters2() {

        // Stack all images relative to the first one
        ImageStackerMain.MainLogger.info("Calculating Offset Params - implicit method");
        this.offsetParameterTable = new OffsetParameters[imagePaths.length][imagePaths.length];

        try {
            RGBImage jImage = new RGBImage(imagePaths[0]);
            for (int i = 0; i < offsetParameterTable[0].length; i++) {
                ImageStackerMain.MainLogger.debug("Initial fill: " + i);
                RGBImage iImage = new RGBImage(imagePaths[i]);
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
    }

    public String[] getStringRepresentation() {
        String[] Output = new String[imagePaths.length + 1];
        Output[0] = "";
        for (String imagePath : imagePaths) {
            Output[0] += (imagePath + ";"); // Careful of final ;
        }

        int x = 1;
        for (OffsetParameters[] row : offsetParameterTable) {
            Output[x] = "";
            for (OffsetParameters offsetParams : row) {
                Output[x] += offsetParams.print() + ";";
            }
            x++;
        }
        return Output;
    }

    public OffsetParameters[][] getOffsetParameterTable() {
        return offsetParameterTable;
    }

    public String[] getImagePaths() {
        return imagePaths;
    }

    // @JsonIgnore
    public boolean isConsistent() {
        boolean isConsistent = true;
        for (int j = 0; j < offsetParameterTable.length; j++) {
            for (int i = 0; i < offsetParameterTable.length; i++) {
                // Checks that (i,j) = (-i,-j) and that the diagonal is zero.
                isConsistent &= OffsetParameters.isNegationOf(offsetParameterTable[j][i], offsetParameterTable[i][j]);

                // Check for the triangle inequality
                for (int k = 0; k < offsetParameterTable.length; k++) {
                    isConsistent &= OffsetParameters.triangleEquality(offsetParameterTable[i][j], offsetParameterTable[j][k], offsetParameterTable[i][k]);
                }
            }
        }
        if (isConsistent) {
            ImageStackerMain.MainLogger.info("Set of Images is consistent. Ready to stack.");
        } else {
            ImageStackerMain.MainLogger.warn("Set of images cannot be aligned. Cannot stack.");
        }
        return (isConsistent);
    }

    // TODO: make this safe so that images can be different sizes?
    // TODO: Use working folder.
    // TODO: Abstract into StackerFactor Class
    public RGBImage stackAll() throws IOException {
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
