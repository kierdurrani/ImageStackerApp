package stacker.alignment;

public class StarCoordinates implements Comparable<StarCoordinates> {
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
