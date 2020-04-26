package com.yk.media.utils;

import android.text.TextUtils;

import java.io.File;

public class FileUtils {

    public static String getFilePath(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        return file.getPath();
    }

    public static String getRecordFilePath(String path, int index) {
        if (TextUtils.isEmpty(path) || !path.contains(".")) {
            return null;
        }
        String dir = path.substring(0, path.lastIndexOf("."));
        String recordPath = dir + "_section_" + index + ".mp4";
        return getFilePath(recordPath);
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

}
