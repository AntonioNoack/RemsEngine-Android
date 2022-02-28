package java.awt.geom;

import java.awt.Shape;

@SuppressWarnings("unused")
public class GeneralPath {

    private Shape path;

    public void append(Shape other, boolean whateverTrue) {
        if (path != null) throw new RuntimeException();
        path = other;
    }

    public PathIterator getPathIterator(AffineTransform transform) {
        return new PathIteratorImpl(path, transform);
    }

}
