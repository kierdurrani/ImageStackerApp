import java.io.*;

public class ConsistentSetOfStackableImages {

    private RGBImage[] stackableImages;
    private OffsetParameters[][] offsetParameterTable;

    // GOES THROUGH ALL PAIRS OF IMAGES AND CALCULATES OFFSET PARAMS
    public ConsistentSetOfStackableImages(RGBImage[] stackableImages) {
        ImageStackerApp.MainLogger.info("Starting CSoSI Creation");
        this.stackableImages = stackableImages;
        this.offsetParameterTable = new OffsetParameters[stackableImages.length][stackableImages.length];
        for (int j = 0; j < offsetParameterTable.length; j++) {
            for (int i = 0; i < offsetParameterTable[0].length; i++) {
                offsetParameterTable[j][i] = new OffsetParameters(stackableImages[j], stackableImages[i], 0);  // TODO change method back
            }
        }
    } // Constructor End

    // Import Stacking Parameters from a text file.
    public ConsistentSetOfStackableImages(RGBImage[] inImages, String Path) throws IOException {

        this.stackableImages = inImages;
        this.offsetParameterTable = new OffsetParameters[stackableImages.length][stackableImages.length];

        // if true, read the stack parameters from the disk and initialize based off that.
        ImageStackerApp.MainLogger.info("Trying to parse read alignment parameters");
        ImageStackerApp.MainLogger.debug("The read alignement Parameters are:");

        BufferedReader bReader = new BufferedReader(new FileReader(Path));

        String line;
        int lineNumber = 0;
        while ((line = bReader.readLine()) != null) {
            String[] lineAsString = line.split(",");
            int[] lineNumbers = new int[lineAsString.length];
            for (int i = 0; i < lineAsString.length; i++) {
                lineNumbers[i] = Integer.parseInt(lineAsString[i]);
                System.out.print(lineNumbers[i]);
            }
            for (int i = 0; i < lineAsString.length; i += 3) {
                offsetParameterTable[lineNumber][i / 3] = new OffsetParameters(lineNumbers[i], lineNumbers[i + 1], lineNumbers[i + 2]);
            }
            lineNumber++;
            System.out.println(line);
        }
        String inString = bReader.readLine();

        for (OffsetParameters[] row : offsetParameterTable) {
            for (OffsetParameters offParam : row) {
                offParam.print();
            }
        }
        // TODO, initialize the private fields properly.
    }

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
            ImageStackerApp.MainLogger.info("Set of Images is consistent. Ready to stack.");
        } else {
            ImageStackerApp.MainLogger.warn("Set of images cannot be aligned. Cannot stack.");
        }
        return (isConsistent);
    }

    public boolean wasnotConistent2(){
        return !isConsistent();
    }

    public boolean getSexy(){
        return isConsistent();
    }

    public void print() {
        System.out.println();
        for (OffsetParameters[] offParamRow : offsetParameterTable) {
            for (OffsetParameters offParam : offParamRow) {
                System.out.print("(");
                offParam.print();
                System.out.print(")");
            }
            System.out.println();
        }
    }

    public void print2(String Path) {
        System.out.println();
        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(Path, false));
            String outString = "";
            for (OffsetParameters[] offRow : offsetParameterTable) {
                for (OffsetParameters offParam : offRow) {
                    outString += offParam.getX();
                    outString += ",";
                    outString += offParam.getY();
                    outString += ",";
                    outString += offParam.getTheta();
                    outString += ",";
                }
                outString += "\n";
            }

            bWriter.write(outString);
            bWriter.close();
            System.out.println("Write Successful!");
        } catch (IOException e) {
            System.out.println("WARNING: IO ERROR WHILE WRITING OFFSET PARAMETERS");
        }
    }


    // TODO: make this safe so that images can be different sizes etc.
    public RGBImage stackAll() {
        System.out.println();
        long time = System.currentTimeMillis();
        System.out.println("Starting the Stacking Process... ");

        //
        int[][][][] imageList = new int[stackableImages.length][][][];
        for (int imageNumber = 0; imageNumber < imageList.length; imageNumber++) {
            imageList[imageNumber] = stackableImages[imageNumber].getRgbArray();
        }

        int[][][] finalBrightness = new int[imageList[0].length][imageList[0][0].length][3];
        // Use the first image in the stack as a reference, and then stack all the others using offset compared to first.
        // Iterate over all pictures:
        for (int imageNumber = 0; imageNumber < imageList.length; imageNumber++) {
            int xOffset = offsetParameterTable[0][imageNumber].getX();
            int yOffset = offsetParameterTable[0][imageNumber].getY();

            // TODO: Make this always work, rather than just arbitrary factor of 0.95
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
        double exp = 0.93
                ;
        for (int j = 0; j < finalBrightness.length; j++) {
            for (int i = 0; i < finalBrightness[0].length; i++) {
                int totBrightness = 0;
                for (int colour = 0; colour < 3; colour++) {
//                    totBrightness +=  finalBrightness[j][i][colour];
                    finalBrightness[j][i][colour] = (int) Math.pow( Math.pow(255.0, 1/exp - 1.0) *finalBrightness[j][i][colour] / (offsetParameterTable.length), exp);
                }
//                totBrightness /= 3;
//                int brightnessMultiplier =  Math.pow((totBrightness *  offsetParameterTable.length),
//                for (int colour = 0; colour < 3; colour++) {
//                    finalBrightness[j][i][colour] = (int) Math.pow(245.0 * finalBrightness[j][i][colour] / (totBrightness *  offsetParameterTable.length), 0.7);
//                }

            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Finisehd the Stacking Process in: " + time + "ms");
        return (new RGBImage(finalBrightness));
    }





}
