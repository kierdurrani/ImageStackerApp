package stacker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// REPRESENTS A PICTURE CLASS WITH NO DATA, TO ENCAPSULATE I/O
public abstract class ImageWriter {

    public void writeToDisk(String path) {
        System.out.println();
        ImageStackerMain.MainLogger.info("Writing Picture to Disk with Path: " + path);
        try {
            BufferedImage image = this.makeBufferedImage();
            File imageFile = new File(path);
            javax.imageio.ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            ImageStackerMain.MainLogger.error("Error: " + e);
            ImageStackerMain.MainLogger.error("IO OUT ERROR WHILE WRITING TO DISK");
            return;
        }
        ImageStackerMain.MainLogger.info("Write to disk successful.");
    }

    public abstract BufferedImage makeBufferedImage();

   // public abstract ImageWriter makeFromBufferedImage(BufferedImage image);

}