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


    // CROSS-CORRELATION METHOD
    public OffsetParameters(RGBImage rgbImg1, RGBImage rgbImg2) {
        ImageStackerApp.MainLogger.info("Starting Alignment of pair.");

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

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // MAXIMISE NUMBER OF ALIGNED STARS
    public OffsetParameters(RGBImage rgbImg1, RGBImage rgbImg2, int method) {

        ArrayList<StarCoordinates> starCoordinates1 = getStarCords(rgbImg1);
        ArrayList<StarCoordinates> starCoordinates2 = getStarCords(rgbImg2);
        starCoordinates1.sort(StarCoordinates::compareTo);
        int mostAlignedStars = 0;

        ImageStackerApp.MainLogger.info("Starting Alignment of pair.");

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
        ImageStackerApp.MainLogger.info("Precise Alignment of pair.");
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
        ImageStackerApp.MainLogger.info("Pair Alignment complete");
        ImageStackerApp.MainLogger.debug("Allignment Parameters found were (x,y,theta)= (" + x + "," + y + "," + theta);
    }

    private ArrayList<StarCoordinates> getStarCords(RGBImage rgbImage) {

        GreyImage greyImage = rgbImage.makeGreyImage().gaussian().bin().gaussian().laplacianMag();
        greyImage.writeToDisk("C:\\Users\\Fairooz\\Desktop\\Stack Testing\\QUICK TEST\\" + ImageStackerApp.write++ + ".png");

        ArrayList<StarCoordinates> starCandidates = new ArrayList<StarCoordinates>();

        ImageStackerApp.MainLogger.info("Finding Stars in Image");
        int[][] greyArray = greyImage.getGreyArray();
        for (int y = 10; y < greyArray.length - 10; y++) {
            for (int x = 10; x < greyArray[0].length - 10; x++) {
                if (greyArray[y][x] > 50) {
                    if ((greyArray[y][x - 1] > 100 | greyArray[y - 1][x] > 100) & (greyArray[y + 1][x] > 100 | greyArray[y][x + 1] > 100)) {
                        if ((greyArray[y][x - 6] < 80 | greyArray[y][x + 6] < 80) & (greyArray[y + 6][x] < 80 | greyArray[y - 6][x] < 80)) {
//                          rgbImage.makeGreenCross(x, y);
                            starCandidates.add(new StarCoordinates(x, y));
                        }
                    }
                }
            }
        }

        ImageStackerApp.MainLogger.info(starCandidates.size() + " Stars were identified pre-culling");

        // CULLING THE STARS:
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
        ImageStackerApp.MainLogger.info("Possible stars grouped based off proximity. Averaging position to find true stars.");
        for (ArrayList<StarCoordinates> starClass : actualStars) {
            int length = starClass.size();
            int x = 0;
            int y = 0;
            for (StarCoordinates coords : starClass) {
                x += coords.getX();
                y += coords.getY();
            }
            starCandidates.add(new StarCoordinates(x / length, y / length));
            //    rgbImage.makeGreenCross(x / length, y / length);
        }
        ImageStackerApp.MainLogger.debug("STARS DETECTED: " + actualStars.size());
        // ImageStackerApp.MainLogger.info("Writing Remaining Stars to Disk");
        // THIS PRINTS WITH CROSSES
        // rgbImage.writeToDisk("C:\\Users\\Fairooz\\Desktop\\Stack Testing\\QUICK TEST\\" + ImageStackerApp.write++ + ".png");
        return starCandidates;
    }

    private boolean starBelongsToClass(StarCoordinates star, List<StarCoordinates> starCoordinatesList) {
        for (StarCoordinates refStarCoords : starCoordinatesList) {
            if (StarCoordinates.distance(refStarCoords, star) < 6) {
                return true;
            }
        }
        return false;
    }

    //

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


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

    public void print() {
        System.out.print("x= " + this.x);
        System.out.print(" y= " + this.y);
        System.out.print(" Theta= " + this.theta);
    }


    // Methods to test relationships of different Offsets
    public static boolean isNegationOf(OffsetParameters first, OffsetParameters second) {
        return ((first.x == -second.x) && (first.y == -second.y) && (first.theta == -second.theta));
    }

    public static boolean triangleEquality(OffsetParameters first, OffsetParameters second, OffsetParameters third) {
        int xDifference = first.x + second.x - third.x;
        int yDifference = first.y + second.y - third.y;
        int thetaDifference = first.theta + second.theta - third.theta;
        return ((xDifference < 2) && (yDifference < 2) && (thetaDifference < 2));
    }


}
