import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// REPRESENTS A PICTURE CLASS WITH NO DATA, TO ENCAPSULATE I/O
public abstract class ImageWriter {

    public void writeToDisk(String path) {
        System.out.println();
        ImageStackerApp.MainLogger.info("Writing Picture to Disk with Path: " + path);
        try {
            BufferedImage image = this.makeBufferedImage();
            File imageFile = new File(path);
            javax.imageio.ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            ImageStackerApp.MainLogger.error("Error: " + e);
            ImageStackerApp.MainLogger.error("IO OUT ERROR WHILE WRITING TO DISK");
            return;
        }
        ImageStackerApp.MainLogger.info("Write to disk successful.");
    }

    public abstract BufferedImage makeBufferedImage();

    public abstract ImageWriter makeFromBufferedImage(BufferedImage image);

}