package com.yk.media.utils;

import android.text.TextUtils;

import java.io.File;

public class FileUtils {

    public static String getFilePath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = new File(path);
        if (file.exists()) {
            file.exists();
        }
        return file.getPath();
    }

    public static String getSectionFilePath(String path, int sectionIndex) {
        String dir = path.substring(0, path.lastIndexOf("."));
        String sectionPath = dir + "_section_" + sectionIndex + ".mp4";
        return getFilePath(sectionPath);
    }

}
