package java.awt.font;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.WindowInsetsAnimation;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class TextLayout {

    private final Rectangle2D bounds = new Rectangle2D();

    private final float ascent, descent;

    public TextLayout(String text, Font font, FontRenderContext context) {
        Paint p = new Paint();
        p.setTextAlign(Paint.Align.LEFT);
        p.setTypeface(Typeface.create(font.name, font.flags));
        p.setTextSize(font.size);
        Paint.FontMetrics fm = p.getFontMetrics();
        Rect rect = new Rect();
        p.getTextBounds(text, 0, text.length(), rect);
        ascent = fm.ascent;
        descent = fm.descent;
        bounds.maxX = rect.right;
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

    public Shape getOutline(AffineTransform transform) {
        return null;
    }

    public float getAdvance() {
        return (float) bounds.getMaxX();
    }

}
