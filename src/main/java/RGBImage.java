import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RGBImage extends ImageWriter {

    private int[][][] rgbArray;

    // Constructor
    public RGBImage(int[][][] rgbArray) {
        this.rgbArray = rgbArray;
    }

    // IO READ CONSTRUCTOR
    public RGBImage(String path) throws IOException {
        File file = new File(path);
        BufferedImage img;
        try {
            img = ImageIO.read(file);
            this.rgbArray = makeFromBufferedImage(img).getRgbArray();
        } catch (IOException e) {
            System.out.println("IO ERROR");
        } finally {
            img = null;
            file = null;
        }
    }

    public GreyImage makeGreyImage() {
        int[][] greyscale = new int[rgbArray.length][rgbArray[0].length];
        for (int y = 0; y < rgbArray.length; y++) {
            for (int x = 0; x < rgbArray[0].length; x++) {
                int sum = rgbArray[y][x][0] + rgbArray[y][x][1] + rgbArray[y][x][2];
                int colour = Math.floorDiv(sum, 3);
                greyscale[y][x] = colour;
            }
        }
        return new GreyImage(greyscale);
    }

    // Getter
    public int[][][] getRgbArray() {
        return rgbArray;
    }

    // Implemented Methods
    @Override
    public BufferedImage makeBufferedImage() {
        BufferedImage image = new BufferedImage(rgbArray[0].length, rgbArray.length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = (rgbArray[y][x][0] << 16) + (rgbArray[y][x][1] << 8) + rgbArray[y][x][2];
                image.setRGB(x, y, color);
            }
        }
        return image;
    }


    @Override
    public RGBImage makeFromBufferedImage(BufferedImage image) {
        int RGB;
        int[][][] rgbArray = new int[image.getHeight()][image.getWidth()][3];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                RGB = image.getRGB(x, y);
                rgbArray[y][x][0] = (RGB & 16711680) >> 16;
                rgbArray[y][x][1] = (RGB & 65280) >> 8;
                rgbArray[y][x][2] = (RGB & 255);
            }
        }
        return (new RGBImage(rgbArray));
    }


    public void makeGreenCross(int x, int y){
        rgbArray[y][x][1] = 255;
        rgbArray[y][x + 1][1] = 255;
        rgbArray[y][x + 2][1] = 255;
        rgbArray[y][x - 1][1] = 255;
        rgbArray[y][x - 2][1] = 255;
        rgbArray[y - 1][x][1] = 255;
        rgbArray[y - 2][x][1] = 255;
        rgbArray[y + 1][x][1] = 255;
        rgbArray[y + 2][x][1] = 255;
        rgbArray[y][x][0] = 0;
        rgbArray[y][x + 1][0] = 0;
        rgbArray[y][x + 2][0] = 0;
        rgbArray[y][x - 1][0] = 0;
        rgbArray[y][x - 2][0] = 0;
        rgbArray[y - 1][x][0] = 0;
        rgbArray[y - 2][x][0] = 0;
        rgbArray[y + 1][x][0] = 0;
        rgbArray[y + 2][x][0] = 0;
        rgbArray[y][x][2] = 0;
        rgbArray[y][x + 1][2] = 0;
        rgbArray[y][x + 2][2] = 0;
        rgbArray[y][x - 1][2] = 0;
        rgbArray[y][x - 2][2] = 0;
        rgbArray[y - 1][x][2] = 0;
        rgbArray[y - 2][x][2] = 0;
        rgbArray[y + 1][x][2] = 0;
        rgbArray[y + 2][x][2] = 0;

    }

}

