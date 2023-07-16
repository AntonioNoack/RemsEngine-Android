package java.awt.geom;

@SuppressWarnings("unused")
public class Rectangle2D {

    public double minX = 0;
    public double maxX = 10;

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getWidth() {
        return maxX - minX;
    }

}
