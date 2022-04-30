package javax.imageio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@SuppressWarnings("unused")
public class ImageIO {

    public static BufferedImage read(InputStream inputStream) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap == null) throw new IOException("Image could not be decoded");
        return new BufferedImage(bitmap);
    }

    public static BufferedImage read(File file) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmap == null) throw new IOException("Image could not be decoded");
        return new BufferedImage(bitmap);
    }

    public static Iterator<ImageReader> getImageReadersBySuffix(String suffix) {
        return emptyIterator;
    }

    private static final Iterator<ImageReader> emptyIterator = new Iterator<ImageReader>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public ImageReader next() {
            return null;
        }
    };

}
