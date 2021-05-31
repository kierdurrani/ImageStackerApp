package stacker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stacker.alignment.ImportException;
import stacker.alignment.StackableImages;
import stacker.images.RGBImage;
import stacker.stacking.StackingMethod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageStackerMain {

    // Config & Globals
    public static Logger MainLogger = LogManager.getRootLogger();
    public static int write = 0;


    // TestingRoot contains: InFiles, OutFiles, WorkingFiles, OffsetParams
    private static String rootDir = "C:\\Users\\Kier\\Developing\\Space Image Stack Project\\TestingRoot\\";

    private static String instanceName = "Orion2";

    private static String inptDir = rootDir + "InFiles\\" + instanceName + "\\";
    private static String workDir = rootDir + "WorkingFiles\\";
    private static String outpDir = rootDir + "outFiles\\";
    private static String logFile = rootDir + "LOG:" + System.currentTimeMillis() + ".log";

    // TODO: Create a generic static methods to easily build workflows in main()
    // TODO: Abstract away the stacking procedure from Stackable images into a separate customisable class. - Done, just need to make customisable!
    // TODO: Log4j tidying up and make the logging level CUSTOMISABLE
    public static void main(String[] args) throws IOException, ImportException {

        // Initialization
        System.setProperty("filename", logFile);
        MainLogger.info("Program has started.");

        // align(inptDir, outpDir);


        // AlignedImages stackableImages = new AlignedImages(rootDir + instanceName + ".txt");
        StackableImages stackableImages = StackableImages.importAlignmentParameters(rootDir + instanceName + ".txt");

        System.out.println(stackableImages.isConsistent());

        try{
            RGBImage finalStack = StackingMethod.stackImage(stackableImages);
            System.out.println(outpDir + instanceName + "orionMega.jpg");
            finalStack.writeToDisk(outpDir + instanceName + "orionMega.jpg");

        } catch (IOException e) {
            ImageStackerMain.MainLogger.fatal("IO Error During Calculation");
            System.exit(112);
        }

    }


    // Static I/O Utilities:
    public static void writeStringArrayToFile(String filename, String[] data) throws IOException {
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < data.length; i++) {
            outputWriter.write(data[i]);
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

    private static String[] listFiles(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File: " + folderPath + listOfFiles[i].getName());
                fileNames[i] = folderPath + listOfFiles[i].getName();
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return fileNames;
    }

    private static String[] copyFilesToWorkingDir(String[] inFiles) throws IOException {
        String[] workingFiles = new String[inFiles.length];
        for (int x = 0; x < inFiles.length; x++) {
            Files.copy(Paths.get(inFiles[x]), Paths.get(workDir + "IMG" + x + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
            workingFiles[x] = workDir + "IMG" + x + ".jpg";
        }
        return workingFiles;
    }

    private static void copyToWorkingAndAlign(String iDir, String oDir) throws IOException{
        // Copy images in folder to "Imports" folder
        String[] iFiles = listFiles(iDir);
        String[] wFiles = copyFilesToWorkingDir(iFiles);

        // Create StackableImages DataStructure
        StackableImages imagesToStack = StackableImages.calculateAlignmentParameters(wFiles, null, null);
        writeStringArrayToFile(rootDir + instanceName + ".txt", imagesToStack.getStringRepresentation());

        System.out.println(imagesToStack.isConsistent());

    }

}
