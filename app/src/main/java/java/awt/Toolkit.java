package java.awt;

import android.content.res.Resources;

@SuppressWarnings({"unused", "SameReturnValue"})
public class Toolkit {

    private static final Toolkit instance = new Toolkit();

    public static Toolkit getDefaultToolkit() {
        return instance;
    }

    public Object getDesktopProperty(String name) {
        return null;
    }

    private Dimension dim;

    public Dimension getScreenSize() {
        Dimension dim = this.dim;
        if (dim == null) {
            dim = this.dim = new Dimension();
            dim.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        }
        return dim;
    }

}
