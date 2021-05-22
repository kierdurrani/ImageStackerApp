package stacker;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class OffsetParameters {

    private int x;
    private int y;
    private int theta;

    @SuppressWarnings("unused")
    public OffsetParameters(int x, int y, int theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @SuppressWarnings("unused")
    public int getTheta() {
        return theta;
    }

    public String print() {
        return( (this.x + "," + this.y + "," + this.theta) );
    }

    // Methods to test relationships of different Offsets
    public static boolean isNegationOf(OffsetParameters first, OffsetParameters second) {
        return ((first.x == -second.x) && (first.y == -second.y) && (first.theta == -second.theta));
    }

    public static boolean triangleEquality(OffsetParameters first, OffsetParameters second, OffsetParameters third) {
        // Verifies the sum of the first two offsets is equal (very close) to the third value
        int xDifference = first.x + second.x - third.x;
        int yDifference = first.y + second.y - third.y;
        int thetaDifference = first.theta + second.theta - third.theta;
        return ((xDifference < 2) && (yDifference < 2) && (thetaDifference < 2));
    }

    // ++++++++++++++++++++++++++++++++ CREATE OFFSET PARAM FROM IMAGE BY MAXIMISING NUMBER OF ALIGNED STARS ++++++++++++++++++++++++++++++++
    public OffsetParameters(RGBImage rgbImg1, RGBImage rgbImg2, int method) {

        ArrayList<StarCoordinates> starCoordinates1 = OffsetParameters.getStarCords(rgbImg1);
        ArrayList<StarCoordinates> starCoordinates2 = OffsetParameters.getStarCords(rgbImg2);
        starCoordinates1.sort(StarCoordinates::compareTo);
        int mostAlignedStars = 0;

        ImageStackerMain.MainLogger.info("Starting Alignment of pair.");

        int yMax = rgbImg1.getRgbArray().length / 2;
        int xMax = rgbImg1.getRgbArray()[0].length / 2;
        int nextPercentProgressAlert = 10;

        for (int yOffset = -yMax; yOffset < yMax; yOffset += 5) {
            // Calculate Progress
            double percentComplete = 50.0 * (yOffset + yMax) / yMax;
            if(percentComplete>nextPercentProgressAlert){
                nextPercentProgressAlert += 10;
                System.out.println( (int) percentComplete + "%");
            }

            for (int xOffset = -xMax; xOffset < xMax; xOffset += 5) {
                // Transform the coordinates of the 2nd list
                for (StarCoordinates coords : starCoordinates2) {
                    coords.transform(xOffset, yOffset); // TODO angle
                }
                int allignedStars = countAlignedStars(starCoordinates1, starCoordinates2);
                if (allignedStars > mostAlignedStars) {
                    this.x = xOffset;
                    this.y = yOffset;
                    System.out.println("Alligned: " + countAlignedStars(starCoordinates1, starCoordinates2));
                    mostAlignedStars = allignedStars;
                }
            }

        }
        ImageStackerMain.MainLogger.info("Precise Alignment of pair.");
        double bestValue = 0.0;
        for (int yOffset = this.y - 4; yOffset < this.y + 4; yOffset++) {
            for (int xOffset = this.x - 4; xOffset < this.x + 4; xOffset++) {
                int correlation = correlation(rgbImg1.makeGreyImage().getGreyArray(), rgbImg2.makeGreyImage().getGreyArray(), xOffset, yOffset);
                if (correlation > bestValue) {
                    this.x = xOffset;
                    this.y = yOffset;
                    bestValue = correlation;
                }
            }
        }
        ImageStackerMain.MainLogger.info("Pair Alignment complete");
        ImageStackerMain.MainLogger.debug("Allignment Parameters found were (x,y,theta)= (" + x + "," + y + "," + theta);
    }

    public static ArrayList<StarCoordinates> getStarCords(RGBImage rgbImage) {
        // TODO - optimisation?  Impletement this as a filter?
        // Make a filter analogous to the laplassian of the form  [-1, 0 ,-1] , [0, 1 ,0].  [-1, 0 ,-1]
        ImageStackerMain.MainLogger.info("Finding Stars in Image");

        ArrayList<StarCoordinates> starCandidates = new ArrayList<StarCoordinates>();
        int[][] greyArray = rgbImage.makeGreyImage().getGreyArray();

        for (int y = 10; y < greyArray.length - 10; y+=3) {
            for (int x = 10; x < greyArray[0].length - 10; x+=3) {
                // This could be done quickly in python using numpy to multiply a filter array by the other

                if (greyArray[y][x] > 50) {
                    // TODO; this doens't work well for the case of a single outlier pixel in the 'dark' region happens to be super light
                    int bright = greyArray[y + 1][x] + greyArray[y-1][x] + greyArray[y][x+ 1] + greyArray[y][x-1];
                    bright += (greyArray[y + 2][x + 2] + greyArray[y+ 2][x -2 ] + greyArray[y-2][x +2] + greyArray[y-2][x+2]);

                    int dark   = greyArray[y][x - 10] + greyArray[y][x + 10] + greyArray[y + 10][x] + greyArray[y - 10][x];
                    dark   += greyArray[y + 7][x + 7] + greyArray[y + 7][x - 7] + greyArray[y - 7][x + 7] + greyArray[y - 7][x - 7];

                    // float darkRatio = ((float) bright + dark )/ (bright); threshold 1.3
                    int darkRatio = bright - dark;
                    if( darkRatio >  40 * 8){
                        starCandidates.add(new StarCoordinates(x, y));
                    }
                }
            }
        }

        ImageStackerMain.MainLogger.info(starCandidates.size() + " Stars were identified pre-culling");

        // CULLING THE STARS (prevent multiple counts of the same star):
        ArrayList<ArrayList<StarCoordinates>> actualStars = new ArrayList<>();
        while (!starCandidates.isEmpty()) {

            // Add first Star to a new Class of Stars
            ArrayList<StarCoordinates> thisStarClass = new ArrayList<>();
            thisStarClass.add(starCandidates.get(0));
            actualStars.add(thisStarClass);
            starCandidates.remove(0);

            boolean newStarsAdded = true;
            while (newStarsAdded) {
                newStarsAdded = false;
                ListIterator<StarCoordinates> iterStarClass = starCandidates.listIterator();
                // Check if starToCheck is near to any of the refCoords.
                while (iterStarClass.hasNext()) {
                    StarCoordinates starToCheck = iterStarClass.next();
                    if (starBelongsToClass(starToCheck, thisStarClass)) {
                        newStarsAdded = true;
                        thisStarClass.add(starToCheck);
                        iterStarClass.remove();
                    }
                }
            }
        }
        ImageStackerMain.MainLogger.info("Possible stars grouped based off proximity. Averaging position to find true stars.");
        for (ArrayList<StarCoordinates> starClass : actualStars) {
            int length = starClass.size();
            int x = 0;
            int y = 0;
            for (StarCoordinates coords : starClass) {
                x += coords.getX();
                y += coords.getY();
            }
            starCandidates.add(new StarCoordinates(x / length, y / length));
        }
        ImageStackerMain.MainLogger.debug("STARS DETECTED: " + actualStars.size());

        return starCandidates;
    }

    private static boolean starBelongsToClass(StarCoordinates star, List<StarCoordinates> starCoordinatesList) {
        for (StarCoordinates refStarCoords : starCoordinatesList) {
            if (StarCoordinates.distance(refStarCoords, star) < 6) {
                return true;
            }
        }
        return false;
    }

    private static int countAlignedStars(ArrayList<StarCoordinates> coordsList1, ArrayList<StarCoordinates> coordsList2) {
        int starAlignmentCount = 0;
        for (StarCoordinates coords2 : coordsList2) {

            // Since coordsList1 is sorted by xCord, we can index into coordsList1 to quickly find the smallest xCoord which is still close to coords2,
            int detectionRadius = 7;
            int lowerBound = 0;
            int upperBound = coordsList2.size();
            while ((upperBound - lowerBound) > 4) {
                // Interval Bisection: If coords2 is to the left of the half way coordinate:
                if (coords2.getX() - coordsList2.get((upperBound + lowerBound) / 2).getX() < -detectionRadius) {
                    upperBound = (upperBound + lowerBound) / 2;
                } else {
                    lowerBound = (upperBound + lowerBound) / 2;
                }
            }
            // After finding the furthest left
            for (int i = lowerBound; i < coordsList1.size(); i++) {
                StarCoordinates coords1 = coordsList1.get(i);
                int xMisalign = coords1.getX() - coords2.getX();
                int yMisalign = coords1.getY() - coords2.getY();
                if (xMisalign > detectionRadius) {
                    break; // gone too far
                }
                if (Math.abs(xMisalign) < 3 & Math.abs(yMisalign) < 3) {
                    starAlignmentCount++;
                    break;
                }

            }
        }
        return starAlignmentCount;
    }

    // ------------------------- CROSS-CORRELATION METHOD -------------------------
    public OffsetParameters(RGBImage rgbImg1, RGBImage rgbImg2) {
        ImageStackerMain.MainLogger.info("Starting Alignment of pair.");

        GreyImage img1 = rgbImg1.makeGreyImage();
        GreyImage img2 = rgbImg2.makeGreyImage();

        // GET SMALL, CLEAN ARRAYS TO ALIGN
        int[][] smallImg1 = makeSmaller(img1.dim().gaussian().gaussian().laplacianMag().getGreyArray());
        int[][] smallImg2 = makeSmaller(img2.dim().gaussian().gaussian().laplacianMag().getGreyArray());

        // ITERATE TO FIND BEST STACK PARAMS
        // TODO: remove min, since images will be cropped eventually
        double bestValue = 0.0;
        int yMax = (int) (0.75 * Math.min(smallImg1.length, smallImg2.length));
        int xMax = (int) (0.75 * Math.min(smallImg1[0].length, smallImg2[0].length));
        for (int yOffset = -yMax; yOffset < yMax; yOffset++) {
            for (int xOffset = -xMax; xOffset < xMax; xOffset++) {
                // TODO add ROTATION
                int correlation = correlation(smallImg1, smallImg2, xOffset, yOffset);
                if (correlation > bestValue) {
                    this.x = 3 * xOffset;
                    this.y = 3 * yOffset;
                    bestValue = correlation;
                }
            }
        }

        // Precise Allignment:
        for (int yOffset = this.y - 3; yOffset < this.y + 3; yOffset++) {
            for (int xOffset = this.x - 3; xOffset < this.x + 3; xOffset++) {
                int correlation = correlation(img1.getGreyArray(), img2.getGreyArray(), xOffset, yOffset);
                if (correlation > bestValue) {
                    this.x = xOffset;
                    this.y = yOffset;
                    bestValue = correlation;
                }
            }
        }
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

    private int correlation(int[][] img1, int[][] img2, int xOffset, int yOffset) {
        int correlation = 0;

        // Having a -ve offset is the same as offsetting the OTHER picture by a positive amount.
        // Do this by inverting the bounds of the for loop

        // TODO: FIX THE BOUNDS ON THE FOR LOOPS. (I think it is fine as is??)
        int yMax = (int) (0.75 * Math.min(img2.length, img1.length));
        int xMax = (int) (0.75 * Math.min(img2[0].length, img1[0].length));

        if (xOffset >= 0) {
            if (yOffset >= 0) {
                //  +x +y
                for (int y = yOffset; y < (yMax - yOffset); y++) {
                    for (int x = xOffset; x < (xMax - xOffset); x++) {
                        correlation += img2[y][x] * img1[y + yOffset][x + xOffset];
                    }
                }
            } else {
                //  +x -y
                yOffset = -yOffset;
                for (int y = yOffset; y < (yMax - yOffset); y++) {
                    for (int x = xOffset; x < (xMax - xOffset); x++) {
                        correlation += img2[y + yOffset][x] * img1[y][x + xOffset];
                    }
                }
            }
        } else {
            // then -ve x
            xOffset = -xOffset;
            if (yOffset >= 0) {
                //  -x +y
                for (int y = yOffset; y < (yMax - yOffset); y++) {
                    for (int x = xOffset; x < (xMax - xOffset); x++) {
                        correlation += img2[y][x + xOffset] * img1[y + yOffset][x];
                    }
                }
            } else {
                // -x -y
                yOffset = -yOffset;
                for (int y = yOffset; y < (yMax - yOffset); y++) {
                    for (int x = xOffset; x < (xMax - xOffset); x++) {
                        correlation += img2[y + yOffset][x + xOffset] * img1[y][x];
                    }
                }
            }
        }
        return (correlation);
    }

}
