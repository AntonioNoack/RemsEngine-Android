package java.awt;

@SuppressWarnings("unused")
public class Color {

    int argb;

    public Color(int argb) {
        this.argb = argb;
    }

    public static Color WHITE = new Color(-1);
    public static Color BLACK = new Color(0xff000000);

}
