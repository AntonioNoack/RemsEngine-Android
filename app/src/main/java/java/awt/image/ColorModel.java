package java.awt.image;

public class ColorModel {
    public boolean hasAlpha() {
        return true;
    }
    public static ColorModel instance = new ColorModel();
}
