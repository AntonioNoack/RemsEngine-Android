package java.awt;

import android.graphics.Paint;

@SuppressWarnings("unused")
public class FontMetrics {

    final int height;
    final float ascent, descent;
    final float leading, top, bottom;
    final protected Font font;

    public FontMetrics(Graphics g, Font font) {
        this.font = font;
        height = (int) Math.ceil(font.size);
        Paint p = g.paint;
        Paint.FontMetrics m = p.getFontMetrics();
        ascent = -m.ascent; // mirrored, then it's similar to the Java AWT values
        descent = m.descent;
        leading = m.leading;
        top = m.top;
        bottom = m.bottom;
        System.out.println("[FontMetrics] height: " + height + ", ascent: " + ascent + ", descent: " + descent + ", top: " + top + ", bottom: " + bottom);
    }

    public int getHeight() {
        return (int) (bottom - top);
    }

    public int getAscent() {
        return (int) -top;
    }

    public int getDescent() {
        return (int) bottom;
    }

}
