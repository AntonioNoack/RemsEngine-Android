package java.awt.geom;

import java.awt.Shape;

public class PathIteratorImpl implements PathIterator {

    Shape shape;
    AffineTransform transform;

    public PathIteratorImpl(Shape shape, AffineTransform transform) {
        this.shape = shape;
        this.transform = transform;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public int currentSegment(float[] dst) {
        return 0;
    }

    @Override
    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

}
