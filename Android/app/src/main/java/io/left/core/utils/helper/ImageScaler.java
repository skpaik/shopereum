package io.left.core.utils.helper;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.left.core.FlareApplication;

/*
*  ****************************************************************************
*  * Created by : Md. Azizul Islam on 11/9/2017 at 12:54 PM.
*  * Email : azizul@w3engineers.com
*  *
*  * Last edited by : Md. Azizul Islam on 11/9/2017.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/
public class ImageScaler {
    public enum Type {IMAGE, CAMERA, VIDEO}

    public enum ImageTier {SHARE, VIEW, THUMB}

    private static ImageScaler imageScaler;
    private static Context mContext;

    /**
     * private constructor
     *
     * @param context
     */
    private ImageScaler(Context context) {
        mContext = context;
    }

    /**
     * Instance creation
     */
    public static ImageScaler onScaler(Context context) {
        return imageScaler = imageScaler == null ? new ImageScaler(context) : imageScaler;
    }


    public Bitmap getScaledBitmap(Type type, String imageUri, int width, int height) {
        //  Bitmap scaledBitmap = BitmapFactory.decodeFile(imageUri);
        String scaledImageUri = compressImage(type, imageUri, width, height);
        Bitmap scaledBitmap = decodeBitmapFromPath(scaledImageUri, width, height);
        return scaledBitmap;
    }

    public String getScaledImageUri(String imageUri) {
        String scaledImageUri = compressImage(Type.IMAGE, imageUri, 750, 950);
        return scaledImageUri;
    }

  /*  private synchronized void getRealImageResolution() {
        if (resolutionType == 3) {
            requestedImageHeight = 950;
            requestedImageWidth = 750;
        } else {
            requestedImageWidth = 1260;
            requestedImageHeight = 1060;
        }
    }*/

    private String getFilenameExtension(String name) {
        if (name == null || name.equals("")) {
            return "";
        }
        String suffix = "";
        int index = name.lastIndexOf(".");
        if (index != -1) {
            suffix = name.substring(index + 1);
        }
        return suffix;
    }

    public String compressImage(Type type, String imageUri, int width, int height) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;

        // if image size is lower than compression size
        // then image compressor return the root path
        if (actualWidth <= width || actualHeight <= height) return imageUri;


        Log.e("actualWidth: ", "" + actualWidth);
        Log.e("actualHeight: ", "" + actualHeight);

        float maxWidth = width; /*612.0f;*/
        float maxHeight = height; /*816.0f;*/

        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(actualWidth), convertDipToPixels(actualHeight));
        options.inJustDecodeBounds = false;
        options.inDither = false;
/*			options.inPurgeable = true;
        options.inInputShareable = true;*/
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.e("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.e("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename(type, getFilenameExtension(imageUri));

        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public void compressImage(Type type, String path) {
        FileOutputStream out = null;
        String filePath = getRealPathFromURI(path);
        Uri photoUri = Uri.parse(path);
        Cursor cursor = mContext.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    String filename = getFilename(type, getFilenameExtension(filePath));
                    Bitmap original = BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                    out = new FileOutputStream(filename);
                    original.compress(Bitmap.CompressFormat.JPEG, 80, out);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null) cursor.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(String contentURI) {
        if (mContext == null) throw new NullPointerException();

        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private String getFilename(Type type, String extension, boolean isToUseTime) {

        String fileFolder = null;
        String prefix = null;


        if (type == Type.IMAGE) {

            fileFolder = DirectoryConstants.TEMP_DIRECTORY;
            prefix = "img_";

        }


        File file = new File(Environment.getExternalStorageDirectory() + fileFolder);
        if (!file.exists()) {
            file.mkdirs();

            File output = new File(file, ".nomedia");
            try {
                boolean fileCreated = output.createNewFile();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

       /* String fileName = YoCommon.EMPTY_STRING;
        if(isToUseTime)
            fileName = YoCommon.EMPTY_STRING + System.currentTimeMillis() + ".";
        String uriString = (file.getAbsolutePath() + File.separator + prefix + fileName + extension);
*/

        return null;
    }

    public String getFilename(Type type, String extension) {
        return getFilename(type, extension, true);
    }


    public Bitmap decodeBitmapFromPath(String imageUri, int width, int height) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageUri, options);

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(width), convertDipToPixels(height));
        options.inDither = false;
        options.inJustDecodeBounds = false;

        scaledBitmap = BitmapFactory.decodeFile(imageUri, options);
        return scaledBitmap;
    }

    public int convertDipToPixels(float dips) {
        if (mContext == null)
            throw new NullPointerException();
        Resources r = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dips, r.getDisplayMetrics());
    }


    private int getMaxDimen(int dimen1, int dimen2) {
        return dimen1 > dimen2 ? dimen1 : dimen2;
    }

    private int getMinDimen(int dimen1, int dimen2) {
        return dimen1 < dimen2 ? dimen1 : dimen2;
    }

    public boolean getIsPanorama(int width, int height) {
        int largest = getMaxDimen(width, height);
        int smallest = getMinDimen(width, height);
        float ratio = largest / smallest;
        return ratio > (4 / 3);
    }

    public boolean getIsCorruptedImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);
        return options.outWidth == 0 || options.outHeight == 0;
    }

    public String Bitmap2String(Bitmap bmp, int compressionFactor) {
        String img2string = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, compressionFactor, stream);
            byte[] byteArray = stream.toByteArray();
            img2string = Base64.encodeToString(byteArray, Base64.DEFAULT);
            /**
             * Roman:02-06-15; Releasing to System.gc()
             */
            //bmp.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return img2string;
    }

    //convert base64 encoded string to bitmap
    public Bitmap String2Bitmap(String str) {
        Bitmap bitmap = null;
        try {
            byte[] byt = Base64.decode(str, 0);
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeByteArray(byt, 0, byt.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public Bitmap decodeScaledBitmapFromSdCard(String filePath, int reqWidth, int reqHeight) {
        Bitmap convertedBmp = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            convertedBmp = BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            return convertedBmp;
        } catch (Error e) {
            System.gc();
            return convertedBmp;
        }
        return convertedBmp;
    }

    public Bitmap decodeBitmapFromSdCard(String filePath) {
        Bitmap convertedBmp = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
            //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            convertedBmp = BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            e.printStackTrace();
            return convertedBmp;
        } catch (Error e) {
            e.printStackTrace();
            return convertedBmp;
        }
        return convertedBmp;
    }

    public String ImgFile2String(String filepath, int compressionFactor) {
        String img2string = null;
        try {
            File f = new File(filepath);
            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, compressionFactor, stream);
            byte[] byteArray = stream.toByteArray();
            img2string = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return img2string;
    }

    public Bitmap getThumnailImage(ImageTier tier, String imagePath) {
        /**Roman: 13-04-15: Re implementation of this code to resolve issue: YOANDROID-740*/
        //String filePath = OnScaler.init(OnContext.get(null)).getRealPathFromURI(imagePath);
        //System.out.println("filePath:"+filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = null;
        bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;

        if (actualWidth == 0 || actualHeight == 0) return null;

        System.out.println("actualHeight:" + actualHeight);

        /**
         *  Roman: To reduce heap size and also for panorama image
         */
        boolean isPanorama = ImageScaler.onScaler(FlareApplication.getContext()).getIsPanorama(actualWidth, actualHeight);


        float maxWidth, maxHeight;
        maxWidth = maxHeight = tier == ImageTier.SHARE ? 130 : (tier == ImageTier.THUMB ? 300 : 612);

        if (isPanorama) {
            Log.e("__-------______Panorama", "Image");
            maxWidth = maxHeight = maxHeight * 3;
        }


        Log.e("#####Width and Height", maxWidth + " " + maxHeight);

        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = ImageScaler.onScaler(FlareApplication.getContext())
                .calculateInSampleSize(options, ImageScaler.onScaler(FlareApplication.getContext())
                        .convertDipToPixels(actualWidth), ImageScaler.onScaler(FlareApplication.getContext()).convertDipToPixels(actualHeight));
        options.inJustDecodeBounds = false;
        options.inDither = false;
/*			options.inPurgeable = true;
        options.inInputShareable = true;*/
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            //Log.e("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                //Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                //Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                //Log.e("EXIF", "Exif: " + orientation);
            }
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        /**Roman: 13-04-15: Re implementation of this code to resolve issue: YOANDROID-740*/
        return bmp;
    }


    public String prepareThumbString(@NonNull String path, int height, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(path).getAbsolutePath(), options);
        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;

        float maxWidth = width;
        float maxHeight = height;

        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        Bitmap bitmap = getThumnailImage(ImageScaler.ImageTier.SHARE, path);
        bitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true);
        String byteStream = Bitmap2String(bitmap, 100);
        return byteStream;
    }

    private int getCompressionFactor(long fileSize) {
        if (fileSize > 5120 || fileSize < 10240) {
            return 50;
        } else if (fileSize > 10240 || fileSize < 15360) {
            return 40;
        } else if (fileSize > 15360 || fileSize < 20480) {
            return 30;
        } else if (fileSize > 20480) {
            return 10;
        } else {
            return 100;
        }
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) throws IOException {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ++Tarun 12-12-2014 (To find out  image orientation)
     */
    public int FindoutExifOrientaionOfImageFromFilePath(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (Exception e) {
            return 0;
        }
    }

    public String compressScaleImageFromSDCard(String imagePath) {
        String encodedStr = null;
        try {
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(imagePath);

            int bitHeight = 0;
            if (bitmap != null) {
                bitHeight = bitmap.getHeight();
            }

            if (bitHeight <= 300) {
                encodedStr = Bitmap2String(bitmap, 80);//60);
            } else if (bitHeight > 300 && bitHeight <= 450) {
                encodedStr = Bitmap2String(bitmap, 70);//25);
            } else if (bitHeight > 450 && bitHeight <= 600) {
                encodedStr = Bitmap2String(bitmap, 50);//3);
            } else {
                encodedStr = Bitmap2String(bitmap, 40);//2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedStr;
    }


    public StreamDrawable getDrawable(String path) {
//        File sd = Environment.getExternalStorageDirectory();
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return new StreamDrawable(bitmap, 0, 0);
    }


    public Bitmap getTransformedBitmap(Bitmap sourceImage, int alphaColor, int borderRadius) {
        return getTransformedBitmap(sourceImage, alphaColor, borderRadius, -1);
    }

    public Bitmap getTransformedBitmap(Bitmap sourceImage, int alphaColor, int borderRadius, int fixRadius) {
        if (sourceImage == null)
            return null;
        int size = Math.min(sourceImage.getWidth(), sourceImage.getHeight());

        int x = (sourceImage.getWidth() - size) / 2;
        int y = (sourceImage.getHeight() - size) / 2;

        BitmapShader mBitmapShader = new BitmapShader(sourceImage,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mBitmapShader);
        mPaint.setAlpha(alphaColor);
        mPaint.setColorFilter(new ColorFilter());

        final float r = size / 2f;
        Bitmap bitmap = Bitmap.createBitmap(size, size, sourceImage.getConfig());
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(alphaColor, PorterDuff.Mode.CLEAR);
        float radius;
        if (fixRadius != -1)
            radius = fixRadius;
        else
            radius = r - borderRadius;
        canvas.drawCircle(r, r, radius, mPaint);//13
        return bitmap;

    }

    class StreamDrawable extends Drawable {
        private static final boolean USE_VIGNETTE = false;

        private final float mCornerRadius;
        private final RectF mRect = new RectF();
        private final BitmapShader mBitmapShader;
        private final Paint mPaint;
        private final int mMargin;
        private final int width, height;

        StreamDrawable(Bitmap bitmap, float cornerRadius, int margin) {
            mCornerRadius = cornerRadius;
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            mBitmapShader = new BitmapShader(bitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setShader(mBitmapShader);

            mMargin = margin;
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRect.set(mMargin, mMargin, bounds.width() - mMargin, bounds.height() - mMargin);

            if (USE_VIGNETTE) {
                RadialGradient vignette = new RadialGradient(
                        mRect.centerX(), mRect.centerY() * 1.0f / 0.7f, mRect.centerX() * 1.3f,
                        new int[]{0, 0, 0x7f000000}, new float[]{0.0f, 0.7f, 1.0f},
                        Shader.TileMode.CLAMP);

                Matrix oval = new Matrix();
                oval.setScale(1.0f, 0.7f);
                vignette.setLocalMatrix(oval);

                mPaint.setShader(
                        new ComposeShader(mBitmapShader, vignette, PorterDuff.Mode.SRC_OVER));
            }
        }

        @Override
        public void draw(Canvas canvas) {
//			canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
            final int size = Math.min(width, height);
            final float r = size / 2f;
            canvas.drawCircle(r, r, r, mPaint);//(mRect, mCornerRadius, mCornerRadius, mPaint);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }
    }


    /********************* New message *************************/

    public String getCompressedImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String compressedImagePath = getCompressedImagePath(getFilenameExtension(imageUri));
        try {
            out = new FileOutputStream(compressedImagePath);
//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return compressedImagePath;

    }

    public String getCompressedImagePath(String extension) {
        File file = new File(DirectoryConstants.TEMP_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + "." + extension);
        AppLog.v("FileSize", "Comptressed apth ="+uriSting);
        return uriSting;

    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


}
