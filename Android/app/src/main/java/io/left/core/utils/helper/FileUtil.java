package io.left.core.utils.helper;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.Locale;

import io.left.core.FlareApplication;

/**
 * Created by w3e02 on 10/16/17.
 */

public class FileUtil {
    public static final String IMAGE = "img";
    public static final String VOICE = "voice";
    public FileUtil()
    {

    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public static File makeExternalDir(String name) {
        if (FileUtil.isExternalStorageWritable()) {
            File dir = FileUtil.makeDir(Environment.getExternalStorageDirectory(), name);
            if (dir != null && dir.isDirectory()) {
                return dir;
            }
        }
        return null;
    }

    public static File makeDir(File parent, String child) {
        if (FileUtil.isExternalStorageWritable()) {
            File file = new File(parent, child);
            if (file.exists() && file.isDirectory()) return file;
            if (file.mkdirs()) {
                return file;
            }
        }
        return null;
    }

    public static String getFileType(String path) {
        try {
            String url = path.toLowerCase(Locale.US);
            if (url.toString().contains(".m4p")
                    || url.toString().contains(".3gpp")
                    || url.toString().contains(".mp3")
                    || url.toString().contains(".wma")
                    || url.toString().contains(".wav")
                    || url.toString().contains(".ogg")
                    || url.toString().contains(".m4a")
                    || url.toString().contains(".aac")
                    || url.toString().contains(".ota")
                    || url.toString().contains(".imy")
                    || url.toString().contains(".rtx")
                    || url.toString().contains(".rtttl")
                    || url.toString().contains(".xmf")
                    || url.toString().contains(".mid")
                    || url.toString().contains(".mxmf")
                    || url.toString().contains(".amr")
                    || url.toString().contains(".flac")) {
                return VOICE;
            } else if (url.toString().contains(".jpg")
                    || url.toString().contains(".jpeg")
                    || url.toString().contains(".png")
                    || url.toString().contains(".gif")
                    || url.toString().contains(".bmp")) {

                return IMAGE;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return null;
    }

    public static long getMediaLength(String mediaLink) {
        long mediaLength = 0;
        try {
            MediaPlayer mp = MediaPlayer
                    .create(FlareApplication.getContext(), Uri.parse(mediaLink));
            return mp.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaLength;
    }

    public static String getDurationInMinute(long durationInMillis) {

        final int HOUR = 60 * 60 * 1000;
        final int MINUTE = 60 * 1000;
        final int SECOND = 1000;

        long durationHour = durationInMillis / HOUR;
        long durationMint = (durationInMillis % HOUR) / MINUTE;
        long durationSec = (durationInMillis % MINUTE) / SECOND;

        String dispTime = durationMint + ":" + durationSec;

        return dispTime;
    }
    public static String getFileName(String fileLink) {
        String fName = "";
        try {
            File file = new File(fileLink);
            fName = file.getName();
        } catch (Exception e) {
        }
        return fName;
    }


    public static String buildFileName(String fileName){
        if(fileName == null) return null;

        String ext = getExtension(fileName);
        if(ext == null) return null;

        if (fileName.indexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName+"_"+System.currentTimeMillis()+"."+ext;
    }

    /*
     * Get the extension of a file.
     */
    public static String getExtension(String fileName) {

        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            return fileName.substring(i + 1).toLowerCase();
        }
        return null;
    }


}
