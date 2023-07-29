package java.awt.image;

@SuppressWarnings("unused")
public class ColorModel {
    public boolean hasAlpha() {
        return true;
    }
    public static ColorModel instance = new ColorModel();
}
