package java.awt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import org.joml.Matrix4fArrayList;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.Map;

public class Graphics {

    private final BufferedImage image;
    private Font font;
    private FontMetrics metrics;
    private final Matrix4fArrayList transforms = new Matrix4fArrayList();
    final Paint paint = new Paint();
    private Bitmap bitmap;
    private Canvas canvas;

    private void ensureFields() {
        bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    public Graphics(BufferedImage image) {
        this.image = image;
    }

    public void setFont(Font font) {
        this.font = font;
        paint.setTypeface(Typeface.create(font.name, font.flags));
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(font.size);
    }

    public Font getFont() {
        if (font == null) font = new Font("Verdana");
        return font;
    }

    public void setRenderingHints(Map<RenderingHints.Key, Object> map) {
    }

    public FontMetrics getFontMetrics() {
        return new FontMetrics(this, font);
    }

    public void fillRect(int x, int y, int w, int h) {
        ensureFields();
        canvas.drawRect(x, y, x + w, y + h, paint);
    }

    public void setColor(Color color) {
        paint.setColor(color.argb);
    }

    public void setBackground(Color color) {
        paint.setStyle(Paint.Style.FILL);
        setColor(color);
        fillRect(0, 0, image.width, image.height);
    }

    public void translate(int dx, int dy) {
        transforms.translate(dx, dy, 0f);
    }

    public void drawString(String text, int x, int y) {
        drawString(text, (float) x, (float) y);
    }

    public void drawString(String text, float x, float y) {
        ensureFields();
        setFont(font);// just for safety
        canvas.save();
        Vector3f tmp = new Vector3f();
        transforms.getTranslation(tmp);
        canvas.translate(tmp.x, tmp.y);
        canvas.drawText(text, x, y, paint);
        canvas.restore();
    }

    public void dispose() {
        // finish all remaining operations (if there is any)
        if (canvas != null) {
            // draw bitmap to bufferedImage
            bitmap.getPixels(image.getData().getData(),
                    0, image.width,
                    0, 0, image.width, image.height);
            bitmap.recycle();
            bitmap = null;
            canvas = null;
        }
    }

}
