package GUI;

import stacker.*;
import stacker.alignment.*;
import stacker.images.RGBImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public final class StackerInterface {

    // Class to manage dependencies with the stacker 'backend'
    // Also contains some static fields to centralise stacking params.

    private static StackableImages stackableImages;

    public static void writeStringArrayToFile(String absolutePath, String[] stringRepresentation) throws IOException {
        ImageStackerMain.writeStringArrayToFile(absolutePath, stringRepresentation);
    }

    // Long calculation functions:
    public static void calculateOffsetParameters(String[] imagePaths, ProgressBar progressBar) throws IOException {


        stackableImages = StackableImages.calculateAlignmentParameters(imagePaths, null, progressBar);

        System.out.println("THIS IS COMPLETE");
    }

    public static void stackImages(){

    }

    // Wrapper functions for methods in other package:
    public static void importStackableImage(String filePath ) throws ImportException {

        stackableImages = StackableImages.importAlignmentParameters(filePath);

    }

    public static StackableImages getStackableImages(){
        return stackableImages;
    }

    public static BufferedImage markStarsInImage(BufferedImage image){

        System.out.println("Making array rep");
        RGBImage rgbImage = RGBImage.makeFromBufferedImage(image);

        System.out.println("Finding Stars");
        ArrayList<AMethodStarDetection.StarCoordinates> starCords = AMethodStarDetection.findStarCoordinates(rgbImage);

        System.out.println("Marking Stars. There were: " + starCords.size());
        for (AMethodStarDetection.StarCoordinates cord : starCords) {
            rgbImage.makeGreenCross(cord.getX(), cord.getY());
        }
        System.out.println("Converting back");

        return rgbImage.makeBufferedImage();

    }






}
