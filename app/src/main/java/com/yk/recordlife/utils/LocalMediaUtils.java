package com.yk.recordlife.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.yk.recordlife.data.bean.Image;
import com.yk.recordlife.data.bean.Video;

import java.util.ArrayList;
import java.util.List;

public class LocalMediaUtils {
    public static List<Image> getLocalImageAll(Context context) {
        List<Image> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Images.Media.DATE_ADDED + " desc");
        if (cursor == null) {
            return list;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            Image image = new Image();
            image.setName(name);
            image.setPath(path);
            list.add(image);
        }
        cursor.close();
        return list;
    }

    public static List<Video> getLocalVideoAll(Context context) {
        List<Video> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Video.Media.DATE_ADDED + " desc");
        if (cursor == null) {
            return list;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH));
            int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT));
            Video video = new Video();
            video.setName(name);
            video.setPath(path);
            video.setWidth(width);
            video.setHeight(height);
            list.add(video);
        }
        cursor.close();
        return list;
    }
}
