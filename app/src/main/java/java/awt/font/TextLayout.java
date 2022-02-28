package java.awt.font;

import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.SEG_QUADTO;
import static java.lang.Math.abs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import org.joml.Vector2f;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.BitSet;
import java.util.List;

import me.anno.maths.geometry.MarchingSquares;
import me.anno.utils.structures.arrays.FloatArrayList;
import me.anno.utils.structures.arrays.IntArrayList;

@SuppressWarnings("unused")
public class TextLayout {

    private final Rectangle2D bounds = new Rectangle2D();

    private final float ascent, descent;

    private final String text;
    private final Font font;
    private final Typeface typeface;
    private final Paint paint = new Paint();
    private final Rect rect = new Rect();

    public TextLayout(String text, Font font, FontRenderContext context) {
        this.text = text;
        this.font = font;
        typeface = Typeface.create(font.name, font.flags);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(typeface);
        paint.setTextSize(font.size);
        Paint.FontMetrics fm = paint.getFontMetrics();
        paint.getTextBounds(text, 0, text.length(), rect);
        ascent = fm.ascent;
        descent = fm.descent;
        bounds.minX = rect.left;
        bounds.maxX = rect.right;
        // System.out.println("[TextLayout] ascent: " + ascent + ", descent: " + descent + ", bounds: " + rect.toString());
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public float getAscent() {
        return ascent;
    }

    public float getDescent() {
        return descent;
    }

    public float getAdvance() {
        return (float) bounds.getMaxX();
    }

    public Shape getOutline(AffineTransform transform) {

        int w = rect.width();
        int h = rect.height();
        if (w <= 0 || h <= 0) return null;

        // padding
        w += 2;
        h += 2;

        // create pixels
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(-1);
        int dx = -rect.left + 1;
        int dy = -rect.top + 1;
        canvas.drawText(text, (float) dx, (float) dy, paint);
        int[] pixelInts = new int[w * h];
        bitmap.getPixels(pixelInts, 0, w, 0, 0, w, h);
        bitmap.recycle();

        System.out.println("Using size " + w + " x " + h + " for " + text);

        if (true) {
            float[] pixelFloats = new float[w * h];
            for (int i = 0, l = pixelInts.length; i < l; i++) {
                pixelFloats[i] = pixelInts[i] >>> 24;
            }
            return createMarchingSquaresShape(w, h, pixelFloats);
        } else {
            for (int i = 0, l = pixelInts.length; i < l; i++) {
                pixelInts[i] = (pixelInts[i] >>> 24) - 128;// extract alpha, and make it to [127,-128]
            }
            return createPixelatedShape(w, h, dx, dy, pixelInts);
        }
    }

    private static Shape createMarchingSquaresShape(int w, int h, float[] pixelFloats) {

        List<List<Vector2f>> polygons = MarchingSquares.INSTANCE.march(w, h, pixelFloats, 127.5f);
        Shape shape = new Shape();
        int numPoints = 0;
        for (List<Vector2f> polygon : polygons) {
            numPoints += polygon.size();
        }
        float[] coordinates = new float[numPoints * 2];
        byte[] operations = new byte[numPoints + polygons.size()];
        int coordinateIndex = 0;
        int operationIndex = 0;
        float hm1 = h - 1f;
        for (List<Vector2f> polygon : polygons) {
            for (int index = 0, length = polygon.size(); index < length; index++) {
                Vector2f v = polygon.get(index);
                operations[operationIndex++] = (byte) (index == 0 ? SEG_MOVETO : SEG_LINETO);
                coordinates[coordinateIndex++] = v.x;// + dx;
                coordinates[coordinateIndex++] = hm1 - v.y;
            }
            operations[operationIndex++] = SEG_CLOSE;
        }
        shape.points = coordinates;
        shape.operations = operations;
        return shape;
    }

    private static Shape createPixelatedShape(int w, int h, int dx, int dy, int[] pixelInts) {
        IntArrayList ops = new IntArrayList(64);
        FloatArrayList data = new FloatArrayList(256, 0f);
        IntArrayList edge = new IntArrayList(64);
        float[] point = new float[2];
        int ctr = 0;
        BitSet done = new BitSet(w * (h + 2) * 2);
        for (int y = 0, i = 0; y < h; y++) {
            for (int x = 0; x < w - 1; x++, i++) {
                if (isEdge(x, y, w, pixelInts, true)) {

                    traceEdge(x, y, w, h, pixelInts, done, edge);

                    // todo if the curve is inside, reverse its order (?)

                    if (edge.size() > 2) {
                        // a full curve was found -> turn into points & lines
                        edgeToPoint(edge.getValue(0), w, pixelInts, point);
                        moveTo(point[0] + dx, h - point[1] - 1f, ops, data);
                        for (int k = 1, l = edge.size(); k < l; k++) {
                            edgeToPoint(edge.getValue(k), w, pixelInts, point);
                            lineTo(point[0] + dx, h - point[1] - 1f, ops, data);
                        }
                        close(ops);
                    }

                }
            }
            i++;
        }

        if (ops.isEmpty()) return null;
        Shape shape = new Shape();
        shape.points = data.toFloatArray();
        byte[] ops2 = new byte[ops.size()];
        for (int i = 0, l = ops2.length; i < l; i++) {
            ops2[i] = (byte) ops.getValue(i);
        }
        shape.operations = ops2;
        return shape;
    }

    private static void edgeToPoint(int edge, int w, int[] pixels, float[] dst) {
        int i = edge >> 1;
        int x = i % w;
        int y = i / w;
        dst[0] = x + 0.5f;
        dst[1] = y + 0.5f;
        if (isVEdge(edge)) {
            dst[0] += cut(pixels[i], pixels[i + 1]);
        } else {
            dst[1] += cut(pixels[i], pixels[i + w]);
        }
    }

    private static void traceEdge(int x0, int y0, int w, int h, int[] pixels, BitSet done, IntArrayList dst) {

        dst.clear();

        int x = x0;
        int y = y0;
        // our direction
        // 0: ->
        // 1: V
        // 2: <-
        // 3: A
        int edge;
        int dir = 1;
        while (true) {

            int i3 = dir * 3, i;
            boolean vEdge = (dir & 1) == 1;
            for (i = 0; i < 3; i++) {
                int x2 = x + dx[i3];
                int y2 = y + dy[i3++];
                if (isEdge(x2, y2, w, pixels, vEdge)) {
                    // go right
                    x = x2;
                    y = y2;
                    dir = (dir + nextDir[i]) & 3;
                    break;
                }
                vEdge = (dir & 1) == 0;
            }
            if (i >= 3) return;

            edge = (x + y * w) * 2 + (dir & 1);
            dst.plusAssign(edge);

            if (done.get(edge)) return;
            done.set(edge, true);

        }
    }

    private static boolean isVEdge(int edge) {
        return (edge & 1) == 1;
    }

    private final static int[] dx = new int[]{
            +1, +0, +0,
            +0, +0, +1,
            -1, -1, -1,
            +0, +1, +0
    };
    private final static int[] dy = new int[]{
            +0, +1, +0,
            +1, +0, +0,
            +0, +0, +1,
            -1, -1, -1
    };
    private final static int[] nextDir = new int[]{0, 1, 3};

    private static boolean isEdge(int x, int y, int w, int[] pixels, boolean vEdge) {
        int i0 = x + y * w, i1;
        if (i0 < 0 || x >= w || i0 >= pixels.length) {
            System.out.println(x + "," + y + "is oob");
            return false;
        }
        if (vEdge) {
            if (x + 1 >= w) return false;
            i1 = i0 + 1;
        } else {
            i1 = i0 + w;
            if (i1 >= pixels.length) return false;
        }
        return (pixels[i0] > 0) != (pixels[i1] > 0);
    }

    private static float cut(int a, int b) {
        if ((a < 0) == (b < 0)) return 0.5f;
        if (a > b) a++;
        else b++;
        return a / (float) (a - b);
    }

    private static void close(IntArrayList ops) {
        ops.plusAssign(SEG_CLOSE);
    }

    private static void moveTo(float x, float y, IntArrayList ops, FloatArrayList data) {
        ops.plusAssign(SEG_MOVETO);
        data.plusAssign(x);
        data.plusAssign(y);
    }

    private static void lineTo(float x, float y, IntArrayList ops, FloatArrayList data) {
        ops.plusAssign(SEG_LINETO);
        data.plusAssign(x);
        data.plusAssign(y);
    }

    private static void quadTo(float x0, float y0, float x1, float y1, IntArrayList ops, FloatArrayList data) {
        ops.plusAssign(SEG_QUADTO);
        data.plusAssign(x0);
        data.plusAssign(y0);
        data.plusAssign(x1);
        data.plusAssign(y1);
    }

    private static void cubicTo(float x0, float y0, float x1, float y1, float x2, float y2, IntArrayList ops, FloatArrayList data) {
        ops.plusAssign(SEG_CUBICTO);
        data.plusAssign(x0);
        data.plusAssign(y0);
        data.plusAssign(x1);
        data.plusAssign(y1);
        data.plusAssign(x2);
        data.plusAssign(y2);
    }

}
