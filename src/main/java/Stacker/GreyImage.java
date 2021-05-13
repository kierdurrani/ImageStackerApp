package stacker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GreyImage extends ImageWriter {

    private int[][] greyArray;

    public GreyImage(String path) throws IOException {
        BufferedImage img = javax.imageio.ImageIO.read(new File(path));
        this.greyArray = makeFromBufferedImage(img).getGreyArray();
    }

    // Constructor
    public GreyImage(int[][] greyArray) {
        this.greyArray = greyArray;
    }

    // Getter
    public int[][] getGreyArray() {
        return greyArray;
    }

    // Implemented Methods
    @Override
    public BufferedImage makeBufferedImage() {
        BufferedImage image = new BufferedImage(greyArray[0].length, greyArray.length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = (greyArray[y][x] << 16) + (greyArray[y][x] << 8) + greyArray[y][x];
                image.setRGB(x, y, color);
            }
        }
        return image;
    }

    @Override
    public GreyImage makeFromBufferedImage(BufferedImage image) {
        int[][] greyArray = new int[image.getHeight()][image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int RGB = image.getRGB(x, y);
                greyArray[y][x] = (((RGB & 16711680) >> 16) + ((RGB & 65280) >> 8) + (RGB & 255))/3;
            }
        }
        return(new GreyImage(greyArray));
    }

    // Mathematical Filters for the ImageWriter:
    public GreyImage gaussian() {
        int[][] output = new int[greyArray.length][greyArray[0].length];
        for (int y = 2; y < greyArray.length - 2; y++) {
            for (int x = 2; x < greyArray[0].length - 2; x++) {
                int cross1 = greyArray[y + 1][x] + greyArray[y - 1][x] + greyArray[y][x + 1] + greyArray[y][x - 1];
                int cross2 = greyArray[y + 2][x] + greyArray[y - 2][x] + greyArray[y][x + 2] + greyArray[y][x - 2];

                int xshape1 = greyArray[y - 1][x - 1] + greyArray[y - 1][x + 1] + greyArray[y + 1][x - 1] + greyArray[y + 1][x + 1];
                int xshape2 = greyArray[y + 2][x + 2] + greyArray[y + 2][x - 2] + greyArray[y - 2][x + 2] + greyArray[y - 2][x - 2];

                int awks = greyArray[y + 2][x + 1] + greyArray[y + 2][x - 1] + greyArray[y + 1][x + 2] + greyArray[y + 1][x - 2]
                        + greyArray[y - 1][x + 2] + greyArray[y - 1][x - 2] + greyArray[y - 2][x + 1] + greyArray[y - 2][x + 1];

                output[y][x] = 26 * cross1 + 7 * cross2 + 16 * xshape1 + xshape2 + 4 * awks + 41 * greyArray[y][x];
                output[y][x] = output[y][x] / 273;
            }
        }
        return (new GreyImage(output));
    }

    public GreyImage laplacianMag()     {
        int[][] output = new int[greyArray.length][greyArray[0].length];
        for (int y = 2; y < greyArray.length - 2; y++) {
            for (int x = 2; x < greyArray[0].length - 2; x++) {

                int cross1 = greyArray[y + 1][x] + greyArray[y - 1][x] + greyArray[y][x + 1] + greyArray[y][x - 1];
                int xshape1 = greyArray[y - 1][x - 1] + greyArray[y - 1][x + 1] + greyArray[y + 1][x - 1] + greyArray[y + 1][x + 1];
                output[y][x] = cross1 + xshape1 - 8 * greyArray[y][x];
                output[y][x] = -output[y][x];
                if(output[y][x]<0){
                    output[y][x] = 0;
                }
            }
        }
        return (new GreyImage(output));
    }

    public GreyImage dim() {
        int[][] output = new int[greyArray.length][greyArray[0].length];
        for (int y = 2; y < greyArray.length - 2; y++) {
            for (int x = 2; x < greyArray[0].length - 2; x++) {
                if (greyArray[y][x] < 10) {
                    output[y][x] =  0;
                } else {
                    output[y][x] = greyArray[y][x] - 10;
                }
            }
        }
        return (new GreyImage(output));
    }

    public GreyImage bin(){
        int[][] output = new int[greyArray.length][greyArray[0].length];
        for (int y = 2; y < greyArray.length - 2; y++) {
            for (int x = 2; x < greyArray[0].length - 2; x++) {
                if (greyArray[y][x] < 28) {
                    output[y][x] =  0;
                } else {
                    output[y][x] = greyArray[y][x] +100;
                    if (greyArray[y][x] > 255) {
                        output[y][x] = 255;
                    }
                }
            }
        }
        return (new GreyImage(output));
    }

}
