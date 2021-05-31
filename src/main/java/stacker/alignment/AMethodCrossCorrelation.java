package stacker.alignment;

import stacker.ImageStackerMain;
import stacker.images.GreyImage;
import stacker.images.RGBImage;
import stacker.ProgressBar;

public class AMethodCrossCorrelation extends AbstractAlignmentMethod{

    // Inherits calculateAllAlignments(String[] filePaths) - which generates table using below method

    @Override
    public OffsetParameters calculateOffsetParameters(RGBImage RGBimg1, RGBImage RGBimg2, ProgressBar progressBar) {

        ImageStackerMain.MainLogger.info("Starting Alignment of pair.");

        GreyImage img1 = RGBimg1.makeGreyImage();
        GreyImage img2 = RGBimg2.makeGreyImage();

        // Create smaller smoothed arrays to do initial alignment
        int[][] smallImg1 = makeSmaller(img1.dim().gaussian().laplacianMag().getGreyArray());
        int[][] smallImg2 = makeSmaller(img2.dim().gaussian().laplacianMag().getGreyArray());

        // Try all alignments to find highest Cross Correlation.
        double bestValue = 0.0;
        int yMax = (int) (0.75 * Math.min(smallImg1.length, smallImg2.length));
        int xMax = (int) (0.75 * Math.min(smallImg1[0].length, smallImg2[0].length));

        int xBest = 0;
        int yBest = 0;

        for (int yOffset = -yMax; yOffset < yMax; yOffset++) {
            for (int xOffset = -xMax; xOffset < xMax; xOffset++) {
                // TODO add ROTATION
                int correlation = crossCorrelation(smallImg1, smallImg2, xOffset, yOffset);
                if (correlation > bestValue) {
                    xBest = 3 * xOffset;
                    yBest = 3 * yOffset;
                    bestValue = correlation;
                }
            }
        }

        // Precise Alignment:
        for (int yOffset = yBest - 3; yOffset < yBest + 3; yOffset++) {
            for (int xOffset = xBest - 3; xOffset < xBest + 3; xOffset++) {
                int correlation = crossCorrelation(img1.getGreyArray(), img2.getGreyArray(), xOffset, yOffset);
                if (correlation > bestValue) {
                    xBest = xOffset;
                    yBest = yOffset;
                    bestValue = correlation;
                }
            }
        }

        return new OffsetParameters(xBest, yBest, 0);

    }

    // Methods used in creation of Offset Parameters
    private static int[][] makeSmaller(int[][] input) {
        int[][] output = new int[input.length / 3][input[0].length / 3];
        for (int y = 0; y < input.length / 3; y++) {
            for (int x = 0; x < input[0].length / 3; x++) {
                output[y][x] = input[3 * y][3 * x];
            }
        }
        return output;
    }



    // TODO - make bounds non arbitrary. Then move to child classes
    protected int crossCorrelation(int[][] img1, int[][] img2, int xOffset, int yOffset) {
        // Assert offsets are bounded between 0-75% of dimension.

        int correlation = 0;

        // Having a -ve offset is the same as offsetting the OTHER picture by a positive amount.
        // Do this by inverting the bounds of the for loop

        // TODO: FIX THE BOUNDS ON THE FOR LOOPS. - 0.75 factor is arbitrary
        int yLimit = (int) (0.75 * Math.min(img2.length, img1.length));
        int xLimit = (int) (0.75 * Math.min(img2[0].length, img1[0].length));

        if (xOffset >= 0) {
            if (yOffset >= 0) {
                //  +x +y
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y + yOffset][x + xOffset] * img2[y][x];
                    }
                }
            } else {
                //  +x -y
                yOffset = -yOffset;
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y][x + xOffset] * img2[y + yOffset][x] ;
                    }
                }
            }
        } else {
            // then -ve x
            xOffset = -xOffset;
            if (yOffset >= 0) {
                //  -x +y
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y + yOffset][x] * img2[y][x + xOffset];
                    }
                }
            } else {
                // -x -y
                yOffset = -yOffset;
                for (int y = yOffset; y < (yLimit - yOffset); y++) {
                    for (int x = xOffset; x < (xLimit - xOffset); x++) {
                        correlation += img1[y][x] * img2[y + yOffset][x + xOffset];
                    }
                }
            }
        }
        return (correlation);
    }



}
