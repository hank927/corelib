package com.hank.corelib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/**
 * @author Hank
 * @date 2016-08-10
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            FileChannel in = fi.getChannel();//
            FileChannel out = fo.getChannel();//
            in.transferTo(0, in.size(), out);//
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) fo.close();
                if (fi != null) fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /***
     * detete file by path
     */
    public static boolean deleteFile(File file)throws IOException{
        return file != null && file.delete();
    }

    /***
     * get file extension
     * @param filename
     * @return file extension
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }


    /**
     *
     */
    public static String getFileOutputString(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path), 8192);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n").append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
