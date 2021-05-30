package stacker.alignment;

import stacker.ImageStackerMain;
import stacker.images.RGBImage;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AMethodStarDetection extends AbstractAlignmentMethod{

    @Override
    public OffsetParameters calculateOffsetParameters(RGBImage rgbImg1, RGBImage rgbImg2) {

        ArrayList<StarCoordinates> starCoordinates1 = findStarCoordinates(rgbImg1);
        ArrayList<StarCoordinates> starCoordinates2 = findStarCoordinates(rgbImg2);
        starCoordinates1.sort(StarCoordinates::compareTo);
        int mostAlignedStars = 0;

        ImageStackerMain.MainLogger.info("Starting Alignment of pair.");

        int xBest = 0;
        int yBest = 0;

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
                    xBest = xOffset;
                    yBest = yOffset;
                    System.out.println("Aligned: " + countAlignedStars(starCoordinates1, starCoordinates2));
                    mostAlignedStars = allignedStars;
                }
            }
        }


        ImageStackerMain.MainLogger.info("Precise Alignment of pair.");

        int[][] img1GreyArray = rgbImg1.makeGreyImage().getGreyArray();
        int[][] img2GreyArray = rgbImg2.makeGreyImage().getGreyArray();

        double bestValue = 0.0;
        for (int yOffset = yBest - 4; yOffset < yBest + 4; yOffset++) {
            for (int xOffset = xBest - 4; xOffset < xBest + 4; xOffset++) {

                int correlation = crossCorrelation( img1GreyArray, img2GreyArray, xOffset, yOffset);
                if (correlation > bestValue) {
                    xBest = xOffset;
                    yBest = yOffset;
                    bestValue = correlation;
                }
            }
        }
        ImageStackerMain.MainLogger.info("Pair Alignment complete");
        ImageStackerMain.MainLogger.debug("Allignment Parameters found were (x,y,theta)= (" + xBest + "," + yBest + "," + "N/A");
        return new OffsetParameters(xBest, yBest, 0);
    }

    public static ArrayList<StarCoordinates> findStarCoordinates(RGBImage rgbImage) {
        // TODO - optimisation?  Implement this as a filter?
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

    // Helper Class:
    public static class StarCoordinates implements Comparable<StarCoordinates> {
        // The ordering makes it faster to perform certain operations on lists.
        private final int xOriginal;
        private final int yOriginal;

        // The below values are used to represent coordinates after transformations have been applied to the star.
        private int x;
        private int y;

        public StarCoordinates(int x, int y) {
            this.xOriginal = x;
            this.yOriginal = y;
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void transform(int xOffset, int yOffset) {
            this.x = xOriginal + xOffset;
            this.y = yOriginal + yOffset;
        }

        public static double distance(StarCoordinates coordinates1, StarCoordinates coordinates2) {
            int xOff = coordinates1.getX() - coordinates2.getX();
            int yOff = coordinates1.getY() - coordinates2.getY();
            return Math.sqrt(xOff * xOff + yOff * yOff);
        }

        @Override
        public int compareTo(StarCoordinates o) {
            return this.x - o.getX();
            // Sort on x Value and: "Return -ve if this is smaller that the other object".
        }

    }

}
