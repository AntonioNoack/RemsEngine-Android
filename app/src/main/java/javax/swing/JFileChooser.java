package javax.swing;

@SuppressWarnings("unused")
public class JFileChooser {

    public static final int OPEN_DIALOG = 0;
    public static final int SAVE_DIALOG = 1;
    public static final int CUSTOM_DIALOG = 2;
    public static final int CANCEL_OPTION = 1;
    public static final int APPROVE_OPTION = 0;
    public static final int ERROR_OPTION = -1;
    public static final int FILES_ONLY = 0;
    public static final int DIRECTORIES_ONLY = 1;
    public static final int FILES_AND_DIRECTORIES = 2;

    private int mode = 0;

    public void setFileSelectionMode(int mode) {
        this.mode = mode;
    }

    public int showOpenDialog(){
        // todo support opening files, if this is possible at all generally...
        // we don't support it yet
        return CANCEL_OPTION;
    }

}
