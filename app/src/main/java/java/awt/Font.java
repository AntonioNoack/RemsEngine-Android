package java.awt;

@SuppressWarnings("unused")
public class Font {

    public String name = "Verdana";
    public int flags = 0;// bold = 1, italic = 2
    public float size = 12f;

    public Font() {
    }

    public Font(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Font decode(String font) {
        return new Font(font);
    }

    public Font deriveFont(int flags) {
        Font clone = new Font(name);
        clone.flags = flags;
        clone.size = size;
        return clone;
    }

    public Font deriveFont(int flags, float size) {
        Font clone = new Font(name);
        clone.flags = flags;
        clone.size = size;
        return clone;
    }

    public int getSize() {
        return (int) size;
    }

    public boolean canDisplay(int charCode) {
        return true;
    }

}
