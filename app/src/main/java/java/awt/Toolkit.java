package java.awt;

@SuppressWarnings("unused")
public class Toolkit {

    private static final Toolkit instance = new Toolkit();

    public static Toolkit getDefaultToolkit() {
        return instance;
    }

    public Object getDesktopProperty(String name) {
        return null;
    }

}
