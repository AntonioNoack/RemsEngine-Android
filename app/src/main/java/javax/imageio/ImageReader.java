package javax.imageio;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.metadata.IIOMetadata;

public class ImageReader {
    public InputStream data;

    public void setInput(Object input) {
        // a try to reset things...
    }

    public IIOMetadata getImageMetadata(int imageIndex) {
        return null;
    }

    public void dispose() throws IOException {
        data.close();
        data = null;
    }
}
