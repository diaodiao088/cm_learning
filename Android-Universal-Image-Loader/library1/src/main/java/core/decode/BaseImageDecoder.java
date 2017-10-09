package core.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

import core.assist.ImageScaleType;
import core.assist.ImageSize;
import core.download.ImageDownLoader;
import utils.ImageSizeUtils;
import utils.IoUtils;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by zhangdan on 2017/9/29.
 * <p>
 * comments:
 */

public class BaseImageDecoder implements ImageDecoder {

    protected final boolean loggingEnabled;

    public BaseImageDecoder(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {

        Bitmap decodeBitmap;
        ImageFileInfo imageInfo;
        InputStream imageStream = getStreamInfo(decodingInfo);

        try{
            imageInfo = defineImageSizeAndRotation(imageStream , decodingInfo);
            imageStream = resetStream(imageStream ,decodingInfo);

            BitmapFactory.Options options = prepareDecodingOptions(imageInfo.imageSize , decodingInfo);
            decodeBitmap = BitmapFactory.decodeStream(imageStream , null ,options);
        }finally {
            IoUtils.closeSiliently(imageStream);
        }

        decodeBitmap = considerExactScaleAndOrientatiton(decodeBitmap, decodingInfo, imageInfo.exifInfo.rotation,
                imageInfo.exifInfo.flipHorizontal);

        return decodeBitmap;
    }


    protected  Bitmap considerExactScaleAndOrientatiton(Bitmap subsampledBitmap , ImageDecodingInfo decodingInfo ,
                                                        int rotation , boolean flipHorizontal){
        Matrix m = new Matrix();
        // Scale to exact size if need
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        if (scaleType == ImageScaleType.EXACTLY || scaleType == ImageScaleType.EXACTLY_STRETCHED) {
            ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), rotation);
            float scale = ImageSizeUtils.computeImageScale(srcSize, decodingInfo.getTargetSize(), decodingInfo
                    .getViewScaleType(), scaleType == ImageScaleType.EXACTLY_STRETCHED);
            if (Float.compare(scale, 1f) != 0) {
                m.setScale(scale, scale);

            }
        }
        // Flip bitmap if need
        if (flipHorizontal) {
            m.postScale(-1, 1);

        }
        // Rotate bitmap if need
        if (rotation != 0) {
            m.postRotate(rotation);
        }

        Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap
                .getHeight(), m, true);
        if (finalBitmap != subsampledBitmap) {
            subsampledBitmap.recycle();
        }
        return finalBitmap;
    }

    protected  InputStream resetStream(InputStream imageStream , ImageDecodingInfo decodingInfo) throws IOException{
        if (imageStream.markSupported()){
            try{
                imageStream.reset();
                return imageStream;
            }catch(IOException e){

            }
        }
        IoUtils.closeSiliently(imageStream);
        return getStreamInfo(decodingInfo);
    }

    protected InputStream getStreamInfo(ImageDecodingInfo decodingInfo) throws IOException {
        ImageDownLoader imageDownLoader = decodingInfo.getDownloader();
        return imageDownLoader.getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
    }

    protected ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, ImageDecodingInfo decodingInfo) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);

        ExifInfo exif;
        String imageUri = decodingInfo.getImageUri();

        if (decodingInfo.shouldConsiderExifParams() && canDefineExifParams(imageUri , options.outMimeType)){
            exif = defineExifOrientation(imageUri);
        }else{
            exif = new ExifInfo();
        }

        return new ImageFileInfo(new ImageSize(options.outWidth ,options.outHeight) , exif);
    }

    private boolean canDefineExifParams(String imageUri , String mimeType){

        return "image/jpeg".equalsIgnoreCase(mimeType) && ImageDownLoader.Scheme.ofUri(imageUri) == ImageDownLoader.Scheme.FILE;

    }

    protected BitmapFactory.Options prepareDecodingOptions(ImageSize imageSize , ImageDecodingInfo decodingInfo){
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        int scale;
        if (scaleType == ImageScaleType.NONE) {
            scale = 1;
        } else if (scaleType == ImageScaleType.NONE_SAFE) {
            scale = ImageSizeUtils.computeMinImageSampleSize(imageSize);
        } else {
            ImageSize targetSize = decodingInfo.getTargetSize();
            boolean powerOf2 = scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2;
            scale = ImageSizeUtils.computeImageSampleSize(imageSize, targetSize, decodingInfo.getViewScaleType(), powerOf2);
        }
        if (scale > 1 && loggingEnabled) {

        }

        BitmapFactory.Options decodingOptions = decodingInfo.getDecodingOptions();
        decodingOptions.inSampleSize = scale;
        return decodingOptions;
    }




    protected  ExifInfo defineExifOrientation(String imageUrl){

        int rotation = 0;
        boolean flip = false;

        try {
            ExifInterface exif = new ExifInterface(ImageDownLoader.Scheme.FILE.crop(imageUrl));
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    flip = true;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        }catch (IOException E){

        }
        return new ExifInfo(rotation ,flip);
    }


    protected static class ExifInfo {

        public final int rotation;
        public final boolean flipHorizontal;

        protected ExifInfo() {
            this.rotation = 0;
            this.flipHorizontal = false;
        }

        protected ExifInfo(int rotation, boolean flipHorizontal) {
            this.rotation = rotation;
            this.flipHorizontal = flipHorizontal;
        }
    }

    protected static class ImageFileInfo {

        public final ImageSize imageSize;
        public final ExifInfo exifInfo;

        public ImageFileInfo(ImageSize imageSize, ExifInfo exifInfo) {
            this.imageSize = imageSize;
            this.exifInfo = exifInfo;
        }
    }
}
