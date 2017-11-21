package io.left.core.utils.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arman on 10/16/17.
 */

public class ProfileUtil {

    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{2,50}$";
    public static String ROOT_PATH = Environment.
            getExternalStorageDirectory().getAbsolutePath() + File.separator + "Flare";


    public static String saveProfileImage(Bitmap bitmap, String fileName) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/Flare");


        if (!direct.exists()) {
            File wallpaperDirectory = new File("/Flare/.avatar");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("Flare/.avatar"), fileName);
        String path = file.getAbsolutePath();
        if (file.exists()) {
            file.delete();
        }

        try {
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }


    public static String imagePathToImageString(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return BitMapToString(myBitmap);
        }
        return null;
    }


    public static String BitMapToString(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    public static boolean isUserNameValid(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static void createDirectory() {
        FileUtil.makeExternalDir(ROOT_PATH);
    }

}
