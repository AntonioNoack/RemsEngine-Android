package java.awt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import org.joml.Matrix4fArrayList;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.Map;

@SuppressWarnings("unused")
public class Graphics {

    private final BufferedImage image;
    private Font font;
    private FontMetrics metrics;
    private final Matrix4fArrayList transforms = new Matrix4fArrayList();
    final Paint paint = new Paint();
    private Bitmap bitmap;
    private Canvas canvas;

    private void ensureFields() {
        if (bitmap == null || canvas == null) {
            int[] data = image.getData().getData();
            boolean hasData = false;
            for (int color : data) {
                if (color != 0) {
                    hasData = true;
                    break;
                }
            }
            if (hasData) {
                bitmap = Bitmap.createBitmap(data, image.width, image.height, Bitmap.Config.ARGB_8888);
                if (!bitmap.isMutable()) {// :/
                    Bitmap bm1 = bitmap;
                    Bitmap bm2 = bm1.copy(Bitmap.Config.ARGB_8888, true);
                    bm1.recycle();
                    bitmap = bm2;
                }
            } else {
                bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888);
            }
            canvas = new Canvas(bitmap);
        }
    }

    public Graphics(BufferedImage image) {
        this.image = image;
    }

    public void setFont(Font font) {
        if (font != this.font) {
            this.font = font;
            paint.setTypeface(Typeface.create(font.name, font.flags));
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(font.size);
            paint.setAntiAlias(true);
        }
    }

    public Font getFont() {
        if (font == null) font = new Font("Verdana");
        return font;
    }

    public void setRenderingHints(Map<RenderingHints.Key, Object> map) {
        // could be used for subpixel rendering hints, if they exist for Android
        // todo set subpixel yes/no for rendering fonts
    }

    public FontMetrics getFontMetrics() {
        if (metrics != null && metrics.font == font) {
            return metrics;
        } else {
            FontMetrics metrics = new FontMetrics(this, font);
            this.metrics = metrics;
            return metrics;
        }
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

    private final Vector3f translation = new Vector3f();

    public void drawString(String text, float x, float y) {
        ensureFields();
        setFont(getFont());// ensure it has been initialized
        transforms.getTranslation(translation);
        System.out.println("drawing '" + text + "' at " + x + " + " + translation.x + ", " + y + " + " + translation.y);
        canvas.drawText(text, x + translation.x, y + translation.y, paint);
    }

    public void dispose() {
        // finish all remaining operations (if there is any)
        if (canvas != null) {
            // draw bitmap to bufferedImage
            bitmap.getPixels(
                    image.getData().getData(),
                    0, image.width,
                    0, 0,
                    image.width, image.height
            );
            bitmap.recycle();
            bitmap = null;
            canvas = null;
        }
    }

}
