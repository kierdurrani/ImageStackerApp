package stacker.alignment;

import stacker.ImageStackerMain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StackableImages {

    // Class representing the paths of images, and a table of relative alignment parameters
    private String[] imagePaths;
    private OffsetParameters[][] offsetParameterTable;

    public static AbstractAlignmentMethod defaultAlignmentMethod = new AlignmentMethodDefault();

    // Constructors - take array of imagePaths method of calculating offset params
    public StackableImages(String[] imagePaths, OffsetParameters[][] alignmentParameters){
        this.imagePaths = imagePaths;
        this.offsetParameterTable = alignmentParameters;
    }

    // Generate new instances of the object through a lengthy alignment algorithm.
    public static StackableImages calculateAlignmentParameters(String[] imagePaths) throws IOException {
        return calculateAlignmentParameters(imagePaths, defaultAlignmentMethod);
    }

    public static StackableImages calculateAlignmentParameters(String[] imagePaths, AbstractAlignmentMethod alignmentMethod) throws IOException{
        return new StackableImages(imagePaths, alignmentMethod.calculateAllAlignments(imagePaths));
    }

    public static StackableImages importAlignmentParameters(String filePath) throws ImportException {
        ImageStackerMain.MainLogger.info("Creating StackableImage object from file import: " + filePath);

        StackableImages importedStackableImages;

        try
        {
            // Read data from file
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Parse image paths in 1st line -  ";" delimited
            String line = reader.readLine();
            String[] imagePaths = line.split(";");

            // Parse all subsequent lines, which are offset co-ords separated by "," and ";"
            OffsetParameters[][] offsetParameterTable = new OffsetParameters[imagePaths.length][imagePaths.length];
            for (int i = 0; i < imagePaths.length; i++) {
                line = reader.readLine();
                String[] lineOfCoords = line.split(";");
                for (int j = 0; j < imagePaths.length; j++) {
                    String[] coords = lineOfCoords[j].split(",");
                    offsetParameterTable[i][j] = new OffsetParameters(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                }
            }

            importedStackableImages = new StackableImages(imagePaths, offsetParameterTable);

            // Validation - for consistency / file existence - this also verifies everything is non-null
            if(offsetParameterTable.length != imagePaths.length){
                throw(new ImportException("File syntax errors - not enough lines of coordinates."));
            }
            if (!importedStackableImages.isConsistent()) {
                ImageStackerMain.MainLogger.warn("Import of stackableImage succeeded but was not found to be consistent");
            }
            for (String imagePath : imagePaths) {
                File f = new File(imagePath);
                if (!f.exists()) {
                    ImageStackerMain.MainLogger.warn("Image file not found in expected location. Please correct before continuing: " + filePath);
                }
            }

            ImageStackerMain.MainLogger.info("Loaded StackableImages from file successfully.");
            return importedStackableImages;

        } catch (IOException e) {
            ImageStackerMain.MainLogger.error("Import of StackableImage File Failed: " + filePath);
            throw (new ImportException("Import Failure - file not found / could not be opened at: " + filePath));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw (new ImportException("Import Failure - syntax error in file / invalid data. 0x01"));

        } catch (NumberFormatException e) {
            ImageStackerMain.MainLogger.error("Error Parsing Numbers during StackableImages Import of: " + filePath);
            throw (new ImportException("Import Failure - syntax error in file / invalid data. 0x02"));

        } catch (NullPointerException e) {

            throw (new ImportException("Import Failure - syntax error in file / invalid data 0x03"));
        }
    }

    // Getter methods
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
                Output[x] += offsetParams.getStringRepresentation() + ";";
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

    // Use to verify that the set of alignment params obeys triangle law and pairwise inversion
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


}
