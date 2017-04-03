package com.kaipingzhou.mindcloud.utils;

import android.database.Cursor;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by 周开平 on 2017/3/27 20:56.
 * qq 275557625@qq.com
 * 作用：
 */

public class Logger {
    private static FileWriter fw;
    private final static Date date = new Date();
    private final static String APP = "GalleryLib";

    public static void log(final Throwable ex) {
        log(ex.getMessage());
        for (StackTraceElement ste : ex.getStackTrace()) {
            log(ste.toString());
        }
    }

    public static void log(final Cursor c) {

        c.moveToFirst();
        String title = "";
        for (int i = 0; i < c.getColumnCount(); i++)
            title += c.getColumnName(i) + " | ";
        log(title);
        while (!c.isAfterLast()) {
            title = "";
            for (int i = 0; i < c.getColumnCount(); i++)
                title += c.getString(i) + " | ";
            log(title);
            c.moveToNext();
        }
    }

    @SuppressWarnings("deprecation")
    public static void log(final String msg) {

        android.util.Log.d(APP, msg);
        try {
            if (fw == null) {
                fw = new FileWriter(new File(
                        Environment.getExternalStorageDirectory().toString() + "/" + APP + ".log"),
                        true);
            }
            date.setTime(System.currentTimeMillis());
            fw.write(date.toLocaleString() + " - " + msg + "\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (fw != null) fw.close();
        } finally {
            super.finalize();
        }
    }
}
