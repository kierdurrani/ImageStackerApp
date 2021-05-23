package GUI;

import stacker.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public final class StackerInterface {
    // Class to manage dependencies with the stacker 'backend'
    // Also contains some objects to centralise stacking params.

    private static StackableImages stackableImages;

    public static void writeStringArrayToFile(String absolutePath, String[] stringRepresentation) throws IOException {
        ImageStackerMain.writeStringArrayToFile(absolutePath, stringRepresentation);
    }

    public static void calculateStackableImages(String[] imagePaths){
        stackableImages = new StackableImages(imagePaths);
        System.out.println("THIS IS COMPLETE");
    }

    public static StackableImages getStackableImages(){
        return stackableImages;
    }

    public static BufferedImage markStarsInImage(BufferedImage image){

        System.out.println("Making array rep");
        RGBImage rgbImage = RGBImage.makeFromBufferedImage(image);

        System.out.println("Finding Stars");
        ArrayList<StarCoordinates> starCords = OffsetParameters.getStarCords(rgbImage);

        System.out.println("Marking Stars. There were: " + starCords.size());
        for (StarCoordinates cord : starCords) {
            rgbImage.makeGreenCross(cord.getX(), cord.getY());
        }
        System.out.println("Converting back");

        return rgbImage.makeBufferedImage();

    }

    public static void importStackableImage(String filePath ) throws ImportException{

        stackableImages = new StackableImages(filePath);

    }



}
