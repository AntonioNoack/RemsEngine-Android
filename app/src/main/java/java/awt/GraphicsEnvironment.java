package java.awt;

import java.io.File;
import java.util.Locale;

@SuppressWarnings("unused")
public class GraphicsEnvironment {

    private static GraphicsEnvironment instance;

    public static GraphicsEnvironment getLocalGraphicsEnvironment() {
        if (instance == null) instance = new GraphicsEnvironment();
        return instance;
    }

    public String[] getAvailableFontFamilyNames() {
        return getAvailableFontFamilyNames(Locale.ROOT);
    }

    public String[] getAvailableFontFamilyNames(Locale locale) {
        File[] fontFiles = new File("/system/fonts").listFiles();
        if (fontFiles != null) {
            String[] fontNames = new String[fontFiles.length];
            for (int i = 0, l = fontNames.length; i < l; i++) {
                String name = fontFiles[i].getName();
                int j = name.lastIndexOf('.');
                if (j > 0) name = name.substring(0, j);
                fontNames[i] = name;
            }
            return fontNames;
        } else return new String[]{"Droid Sans", "Droid Serif", "Droid Sans Mono", "Roboto"};
    }

}
