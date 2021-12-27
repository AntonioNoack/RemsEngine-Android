package java.awt;

import android.graphics.Paint;

public class FontMetrics {

    int height;
    float ascent, descent;
    float leading, top, bottom;

    public FontMetrics(Graphics g, Font font) {
        height = (int) Math.ceil(font.size);
        Paint p = g.paint;
        Paint.FontMetrics m = p.getFontMetrics();
        ascent = m.ascent;
        descent = m.descent;
        leading = m.leading;
        top = m.top;
        bottom = m.bottom;
    }

    public int getHeight() {
        return height;
    }

    public int getAscent() {
        return height;
    }

    public int getDescent() {
        return 0;
    }

}
