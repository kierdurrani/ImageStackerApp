import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class ImageFromDisk {


    public static ImageFromDisk readFromDisk(String path) throws IOException {
        BufferedImage img = javax.imageio.ImageIO.read(new File(path));
        return (makeFromBufferedImage(img));
    }

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

    protected abstract BufferedImage makeBufferedImage();

    protected abstract ImageFromDisk makeFromBufferedImage(BufferedImage image);

}

