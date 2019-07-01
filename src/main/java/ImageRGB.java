import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageRGB extends  ImageFromDisk {


    @Override
    protected BufferedImage makeBufferedImage() {
        return null;
    }

    @Override
    protected ImageFromDisk makeFromBufferedImage(BufferedImage image) {
        return null;
    }

    void test() throws IOException {
        ImageFromDisk rgb = readFromDisk("test");
    }
}
