package java.awt.font;

import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;

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
import java.util.List;

import me.anno.maths.geometry.MarchingSquares;

@SuppressWarnings("unused")
public class TextLayout {

    private final Rectangle2D bounds = new Rectangle2D();

    private final float ascent, descent;

    private final String text;
    private final Paint paint = new Paint();
    private final Rect rect = new Rect();

    public TextLayout(String text, Font font, FontRenderContext context) {
        this.text = text;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(font.name, font.flags));
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

    // todo why is the shape mirrored?? z space maybe...

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

        // clearly superior compared to the old method
        // todo we could simplify the shapes, and identify quadratic and cubic splines...
        float[] pixelFloats = new float[w * h];
        for (int i = 0, l = pixelInts.length; i < l; i++) {
            pixelFloats[i] = pixelInts[i] >>> 24;
        }
        return createMarchingSquaresShape(w, h, pixelFloats);
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

}
