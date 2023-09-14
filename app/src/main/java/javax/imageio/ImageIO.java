package javax.imageio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.imageio.stream.ImageInputStream;

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

    public static ImageInputStream createImageInputStream(Object inputStream) {
        ImageInputStream result = new ImageInputStream();
        result.data = (InputStream) inputStream;
        return result;
    }

    public static Iterator<ImageReader> getImageReaders(Object imageInputStream){
        ImageReader reader = new ImageReader();
        reader.data = ((ImageInputStream) imageInputStream).data;
        return Collections.singletonList(reader).iterator();
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
