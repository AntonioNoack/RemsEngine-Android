package javax.imageio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;

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

    public static boolean write(RenderedImage renderedImage, String formatName, OutputStream stream) {
        BufferedImage bufferedImage = (BufferedImage) renderedImage;
        DataBufferInt bufferInt = (DataBufferInt) bufferedImage.getData().getDataBuffer();
        int[] intData = bufferInt.getData();
        Bitmap bitmap = Bitmap.createBitmap(intData, bufferedImage.width, bufferedImage.height, Bitmap.Config.ARGB_8888);
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        if ("webp".equalsIgnoreCase(formatName)) {
            format = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ?
                    Bitmap.CompressFormat.WEBP_LOSSY :
                    Bitmap.CompressFormat.WEBP;
        } else if ("jpg".equalsIgnoreCase(formatName) || "jpeg".equalsIgnoreCase(formatName)) {
            format = Bitmap.CompressFormat.JPEG;
        } else if (!"png".equalsIgnoreCase(formatName)) {
            System.out.println(formatName + " cannot be properly exported, using PNG");
        }
        bitmap.compress(format, 90, stream);
        bitmap.recycle();
        return true;
    }

    public static Iterator<ImageReader> getImageReadersBySuffix(String suffix) {
        return emptyIterator;
    }

    public static ImageInputStream createImageInputStream(Object inputStream) {
        ImageInputStream result = new ImageInputStream();
        result.data = (InputStream) inputStream;
        return result;
    }

    public static Iterator<ImageReader> getImageReaders(Object imageInputStream) {
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
