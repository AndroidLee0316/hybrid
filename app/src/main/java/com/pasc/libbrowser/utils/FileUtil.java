package com.pasc.libbrowser.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    private final static String TAG = "FileUtil";
    private final static int BUFF_SIZE = 2048;

    /**
     * smt目录
     */
    public static final String APP_FOLDER_PATH = "smt/";
    /**
     * smt目录
     */
    public static final String APP_IMG_FOLDER_PATH = "img/";

    /**
     * Get sdcard path
     *
     * @return the path
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist =
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

    /**
     * Get the App root path
     *
     * @return the path
     */
    public static String getRootFolderPath() {
        return getSDPath() + "/" + APP_FOLDER_PATH;
    }

    /**
     * Get the App img path
     *
     * @return the path
     */
    public static String getImgFolderPath() {
        String path = getSDPath() + "/" + APP_FOLDER_PATH + APP_IMG_FOLDER_PATH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * Get brandArray random file name according to current date
     *
     * @return brandArray random file name
     */
    public static String getRandomFileName() {
        Date todate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return sdf.format(todate);
    }

    /**
     * Get brandArray random img path according to current date
     *
     * @return brandArray random img path
     */
    public static String getRandomImgPath() {
        return getImgFolderPath() + getRandomFileName() + ".jpeg";
    }

    /**
     * Create dir recursively
     *
     * @param path path name
     */
    public static void createDirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 拍照路径 6.19
     */

    private static String FILE_NAME = "userIcon.jpg";
    public static String PATH_PHOTOGRAPH = "/smt/";

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 保存图片
     */
    public static void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream bos = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            bos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getDCIMFile(String filePath, String imageName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 文件可用
            File dirs = new File(Environment.getExternalStorageDirectory(), "DCIM" + filePath);
            if (!dirs.exists()) dirs.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),
                    "DCIM" + filePath + imageName);
            if (!file.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    file.createNewFile();
                } catch (Exception e) {
                }
            }
            return file;
        } else {
            return null;
        }
    }

    public static File getBaseFile(String filePath) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 文件可用
            File f = new File(Environment.getExternalStorageDirectory(), filePath);
            if (!f.exists()) f.mkdirs();
            return f;
        } else {
            return null;
        }
    }

    public static String getFileName() {
        String fileName = FILE_NAME;
        return fileName;
    }

    /**
     * 由指定的路径和文件名创建文件
     */
    public static File createFile(String path, String name) throws IOException {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 文件大小（长度转化文字展示）
     *
     * @param var0 长度
     */
    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes"
                : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F)) + "KB"
                        : (var0 < 1073741824L ? var2.format(
                                (double) ((float) var0 / 1024.0F / 1024.0F)) + "MB"
                                : (var0 < 0L ? var2.format(
                                        (double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                                        + "GB" : "error")));
    }

    /**
     * 获取文件对应的URI
     */
    public static Uri getUri(Activity activity, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //data是file类型,忘了复制过来
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider",
                    file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 压缩文件
     *
     * @param fs 需要压缩的文件
     * @param zipFilePath 被压缩后存放的路径
     * @return 成功返回 true，否则 false
     */
    public static boolean zipFiles(File fs[], String zipFilePath) {
        if (fs == null) {
            throw new NullPointerException("fs == null");
        }
        boolean result = false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath)));
            for (File file : fs) {
                if (file == null || !file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    recursionZip(zos, file, file.getName() + File.separator);
                } else {
                    recursionZip(zos, file, "");
                }
            }
            result = true;
            zos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "zip file failed err: " + e.getMessage());
        } finally {
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir)
            throws Exception {
        if (file.isDirectory()) {
            Log.i(TAG,
                    "the file is dir name -->>" + file.getName() + " the baseDir-->>>" + baseDir);
            File[] files = file.listFiles();
            for (File fileSec : files) {
                if (fileSec == null) {
                    continue;
                }
                if (fileSec.isDirectory()) {
                    baseDir = file.getName() + File.separator + fileSec.getName() + File.separator;
                    Log.i(TAG, "basDir111-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                } else {
                    Log.i(TAG, "basDir222-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                }
            }
        } else {
            Log.i(TAG, "the file name is -->>" + file.getName() + " the base dir -->>" + baseDir);
            byte[] buf = new byte[BUFF_SIZE];
            InputStream input = new BufferedInputStream(new FileInputStream(file));
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            int len;
            while ((len = input.read(buf)) != -1) {
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }

    /**
     * 获取网络附件的名字（包含文件格式）
     */
    public static void getHttpFileName(final String urlStr,
            final OnHttpHeaderListener httpHeaderListener) {

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection connection = null;
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int code = 0;
                    code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        String fileName = connection.getHeaderField("Content-Disposition");
                        // 通过Content-Disposition获取文件名
                        if (fileName == null || fileName.length() < 1) {
                            // 通过截取URL来获取文件名
                            URL downloadUrl = connection.getURL();
                            // 获得实际下载文件的URL
                            fileName = downloadUrl.getFile();
                            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        } else {
                            fileName = URLDecoder.decode(
                                    fileName.substring(fileName.indexOf("filename=") + 9), "UTF-8");
                            // 存在文件名会被包含在""里面，所以要去掉，否则读取异常
                            fileName = fileName.replaceAll("\"", "");
                            fileName = new String(fileName.getBytes("utf-8"), "gb2312");
                        }
                        final String finalFileName = fileName;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                           @Override public void run() {
                               httpHeaderListener.httpHeader(finalFileName);
                           }
                       });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override public void run() {
                            httpHeaderListener.httpHeader("");
                        }
                    });

                }
            }
        }).start();
    }

    /**
     * 删除目录下所有的文件(保存子目录)
     * @param root
     */
   public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {

                        }
                    }
                }
            }
    }

    /**
     * 删除目录下的所有文件包括子目录
     * @param root
     */
    public static void deleteAllFilesAndDirectory(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {

                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {

                        }
                    }
                }
            }
    }

}
