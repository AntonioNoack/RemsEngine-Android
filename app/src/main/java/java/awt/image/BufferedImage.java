package java.awt.image;

import android.graphics.Bitmap;

import java.awt.Graphics;
import java.awt.Graphics2D;

@SuppressWarnings("unused")
public class BufferedImage {

    public final int width, height, type;
    private final Graphics2D graphics = new Graphics2D(this);
    private final WritableRaster raster = new WritableRaster();

    public BufferedImage(int w, int h, int type) {
        this.width = w;
        this.height = h;
        this.type = type;
        raster.data = new int[w * h];
    }

    public BufferedImage(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        type = TYPE_INT_ARGB;
        int[] pixels = raster.data = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public void setRGB(int x, int y, int color) {
        raster.data[x + y * width] = color;
    }

    public int getRGB(int x, int y) {
        return raster.data[x + y * width];
    }

    public Raster getData() {
        return raster;
    }

    public WritableRaster getRaster() {
        return raster;
    }

    public int getType() {
        return type;
    }

    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_INT_RGB = 1;
    public static final int TYPE_INT_ARGB = 2;
    public static final int TYPE_INT_ARGB_PRE = 3;
    public static final int TYPE_INT_BGR = 4;
    public static final int TYPE_3BYTE_BGR = 5;
    public static final int TYPE_4BYTE_ABGR = 6;
    public static final int TYPE_4BYTE_ABGR_PRE = 7;
    public static final int TYPE_USHORT_565_RGB = 8;
    public static final int TYPE_USHORT_555_RGB = 9;
    public static final int TYPE_BYTE_GRAY = 10;
    public static final int TYPE_USHORT_GRAY = 11;
    public static final int TYPE_BYTE_BINARY = 12;
    public static final int TYPE_BYTE_INDEXED = 13;
    private static final int DCM_RED_MASK = 16711680;
    private static final int DCM_GREEN_MASK = 65280;
    private static final int DCM_BLUE_MASK = 255;
    private static final int DCM_ALPHA_MASK = -16777216;
    private static final int DCM_565_RED_MASK = 63488;
    private static final int DCM_565_GRN_MASK = 2016;
    private static final int DCM_565_BLU_MASK = 31;
    private static final int DCM_555_RED_MASK = 31744;
    private static final int DCM_555_GRN_MASK = 992;
    private static final int DCM_555_BLU_MASK = 31;
    private static final int DCM_BGR_RED_MASK = 255;
    private static final int DCM_BGR_GRN_MASK = 65280;
    private static final int DCM_BGR_BLU_MASK = 16711680;

}
