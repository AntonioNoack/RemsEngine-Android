package java.awt.geom;

import java.awt.Shape;

public class PathIteratorImpl implements PathIterator {

    private final Shape shape;
    private final AffineTransform transform;

    public PathIteratorImpl(Shape shape, AffineTransform transform) {
        this.shape = shape;
        this.transform = transform;
    }

    private int opIndex = 0;
    private int dataIndex = 0;

    @Override
    public boolean isDone() {
        return shape == null || opIndex >= shape.operations.length;
    }

    @Override
    public void next() {
        int op = shape.operations[opIndex++];
        switch (op) {
            case SEG_LINETO:
            case SEG_MOVETO:
                dataIndex += 2;
                break;
            case SEG_QUADTO:
                dataIndex += 4;
                break;
            case SEG_CUBICTO:
                dataIndex += 6;
                break;
        }
    }

    @Override
    public int currentSegment(float[] dst) {
        int op = shape.operations[opIndex];
        float[] data = shape.points;
        int dataIndex = this.dataIndex;
        switch (op) {
            case SEG_LINETO:
            case SEG_MOVETO:
                dst[0] = data[dataIndex];
                dst[1] = data[dataIndex + 1];
                break;
            case SEG_QUADTO:
                dst[0] = data[dataIndex];
                dst[1] = data[dataIndex + 1];
                dst[2] = data[dataIndex + 2];
                dst[3] = data[dataIndex + 3];
                break;
            case SEG_CUBICTO:
                dst[0] = data[dataIndex];
                dst[1] = data[dataIndex + 1];
                dst[2] = data[dataIndex + 2];
                dst[3] = data[dataIndex + 3];
                dst[4] = data[dataIndex + 4];
                dst[5] = data[dataIndex + 5];
                break;
            case SEG_CLOSE:
                break;
        }
        return op;
    }

    private void transform(float[] data, int length) {
        // todo transform all points
    }

    @Override
    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

}
