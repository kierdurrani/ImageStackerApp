package stacker.alignment;

public class OffsetParameters {

    // Interpret the offset parameters as the affine operation needed to align to images:
    // Given two images which start off on top of each other
    // Apply the translation (x,y) to the 2nd image, then rotate by theta radians about the top left corner.

    private int x;
    private int y;
    private int theta;

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

    public int getTheta() {
        return theta;
    }

    public String getStringRepresentation() {
        return ((this.x + "," + this.y + "," + this.theta));
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

}
