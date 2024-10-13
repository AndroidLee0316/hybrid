package com.pasc.lib.smtbrowser.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUiUtils {

    private static final String TAG = "FileUiUtils";
    /**
     *
     */
    public static String ROOT_PATH = null;

    /**
     * 公共资源下载缓存文件路径
     */
    public static String RESOURCE_COMMON_CACHE_PATH = null;
    /**
     * 用户文件资源下载缓存文件路径
     */
    public static String RESOURCE_USER_FILE_CACHE_PATH = null;
    /**
     * 用户图片资源下载缓存文件路径
     */
    public static String RESOURCE_USER_IMAGE_CACHE_PATH = null;
    /**
     * 用户DB路径
     */
    public static String RESOURCE_USER_DB_CACHE_PATH = null;
    /**
     * 用户录音资源下载缓存文件路径
     */
    public static String RESOURCE_USER_RECORD_CACHE_PATH = null;
    /**
     * 用户视频资源下载缓存文件路径
     */
    public static String RESOURCE_USER_VIDEO_CACHE_PATH = null;

    public static String COPY_FILE_PATH = null;

    public static String RESOURCE_DOWNLOAD_PATH = null;
    /**
     * 内存空间不足
     */
    public static String SAVE_FILE_STATUS_MEMORY_ERROR = "MEMORY_ERROR";
    /**
     * sdcard不存在
     */
    public static String SAVE_FILE_STATUS_SDCARD_ERROR = "SDCARD_ERROR";
    /**
     * 系统错误
     */
    public static String SAVE_FILE_STATUS_SYSTEM_ERROR = "SYSTEM_ERROR";

    public static String IMAGE_TYPE_JPEG = "jpg";


    // 返回文件大小格式
    public static class FileSizeType {
        public static final int BB = 0;// byte
        public static final int KB = 1;// kb
        public static final int MB = 2;// mb
        public static final int GB = 3;// gb
    }

    /**
     * 初始化应用存储路径
     *
     * @param context
     */
    public static void initAppStoragePath(Context context, String userName) {
        FileUiUtils.ROOT_PATH = getAppStorageDir(context);

        initCopyFilePath();

        initCommonCache(context);

        if (TextUtils.isEmpty(userName)) {
            userName = "temp";
        }
        initLoginStoragePath(userName);
    }

    private static void initCopyFilePath() {
        COPY_FILE_PATH = Environment.getExternalStorageDirectory().toString();// 相册目录
        if (null == COPY_FILE_PATH) {
            COPY_FILE_PATH = getSDCardDir();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(COPY_FILE_PATH).append(File.separator).append("DCIM").append(File.separator).append("camera");
        COPY_FILE_PATH = builder.toString();
        new File(COPY_FILE_PATH).mkdirs();
    }

    private static void initCommonCache(Context context) {
        if (FileUiUtils.ROOT_PATH != null) {
            FileUiUtils.RESOURCE_COMMON_CACHE_PATH = FileUiUtils.ROOT_PATH + File.separator + ".resource";
        }
        // /下载地址
        RESOURCE_DOWNLOAD_PATH = getStorageDir(context, false) + File.separator + "download";

        new File(RESOURCE_COMMON_CACHE_PATH).mkdirs();
        new File(RESOURCE_DOWNLOAD_PATH).mkdirs();
    }

    /**
     * 用户个人文件
     *
     * @param userName
     */
    public static void initLoginStoragePath(String userName) {
        if (FileUiUtils.ROOT_PATH != null) {// TODO 资源目录开发中先不建隐藏目录。
            StringBuilder builder = new StringBuilder();
            builder.append(FileUiUtils.ROOT_PATH).append(File.separator).append(userName).append(File.separator);
            String basePath = builder.toString();
            FileUiUtils.RESOURCE_USER_DB_CACHE_PATH = basePath.concat("database");
            FileUiUtils.RESOURCE_USER_FILE_CACHE_PATH = basePath.concat("file");
            FileUiUtils.RESOURCE_USER_IMAGE_CACHE_PATH = basePath.concat("image");
            FileUiUtils.RESOURCE_USER_RECORD_CACHE_PATH = basePath.concat("record");
            FileUiUtils.RESOURCE_USER_VIDEO_CACHE_PATH = basePath.concat("video");

            new File(RESOURCE_USER_DB_CACHE_PATH).mkdirs();
            new File(RESOURCE_USER_FILE_CACHE_PATH).mkdirs();
            new File(RESOURCE_USER_IMAGE_CACHE_PATH).mkdirs();
            new File(RESOURCE_USER_RECORD_CACHE_PATH).mkdirs();
            new File(RESOURCE_USER_VIDEO_CACHE_PATH).mkdirs();
        }
    }

    public static String getUserRecordCacheDir(String username) {
        initLoginStoragePath(username);
        checkDirectory(new File(RESOURCE_USER_RECORD_CACHE_PATH));
        return RESOURCE_USER_RECORD_CACHE_PATH;
    }

    public static String getUserImageCacheDir(String username) {
        initLoginStoragePath(username);
        return RESOURCE_USER_IMAGE_CACHE_PATH;
    }

    public static String getUserCaptureCacheFilePath(String username) {
        return getUserImageCacheDir(username) + File.separator + "captureTemp.jpg";
    }

    public static String getUserCaptureCacheFilePath(String username, String postfix) {
        return new StringBuilder(getUserImageCacheDir(username)).append(File.separator).append("capture_temp_").append(postfix).append(".jpg").toString();
    }

    public static void checkDirectory(File dir) {
        if (dir.exists()) {
            if (!dir.isDirectory() && !dir.delete() && !dir.mkdirs()) {
                throw new RuntimeException("create file(" + dir + ") fail.");
            }
        } else if (!dir.mkdirs()) {
            throw new RuntimeException("create file(" + dir + ") fail.");
        }
    }

    /**
     * 获取APP 存储的路径
     *
     * @param context
     * @return
     */
    public static String getAppStorageDir(Context context) {
        // 获取Android程序在Sd上的保存目录约定 当程序卸载时，系统会自动删除。
        File f = context.getExternalFilesDir(null);
        // 如果约定目录不存在
        if (f == null) {
            // 获取外部存储目录即 SDCard
            return getStorageDir(context);
        } else {
            String storageDirectory = f.getAbsolutePath();
            Log.i(TAG, "项目存储路径采用系统给的路径地址  storageDirectory:" + storageDirectory);
            return storageDirectory;
        }

    }

    /**
     * 获取可用的sdcard路径
     *
     * @param context
     * @return
     */
    public static String getStorageDir(Context context) {
        return getStorageDir(context, true);
    }

    /**
     * 获取可用的sdcard路径
     *
     * @param context
     * @return
     */
    public static String getStorageDir(Context context, boolean isAllowUseCache) {
        // 获取外部存储目录即 SDCard
        String storageDirectory = Environment.getExternalStorageDirectory().toString();
        File fDir = new File(storageDirectory);
        StringBuilder builder = new StringBuilder(storageDirectory);
        // 如果sdcard目录不可用
        if (!fDir.canWrite()) {
            // 获取可用
            storageDirectory = getSDCardDir();
            if (storageDirectory != null) {
                builder.delete(0, builder.length());
                builder.append(storageDirectory).append(File.separator).append("com.pingan").append(File.separator).append("pachat");
                Log.i(TAG, "项目存储路径采用自动找寻可用存储空间的方式   storageDirectory:" + builder.toString());
                return builder.toString();

            } else {
                if (isAllowUseCache) {
                    Log.e(TAG, "没有找到可用的存储路径  采用cachedir");
                    return context.getCacheDir().toString();
                } else {
                    return null;
                }
            }
        } else {
            builder.append(File.separator).append("com.pingan").append(File.separator).append("pachat");
            Log.i(TAG, "项目存储路径采用sdcard的地址   storageDirectory:" + builder.toString());
            return builder.toString();
        }
    }

    /**
     * 获取视频压缩路径
     *
     * @param context
     * @return 被压缩视频路径
     */
    public static String getCompressPath(Context context) {
        StringBuilder builder = new StringBuilder();
        builder.append(getStorageDir(context)).append(File.separator).append("compress").append(File.separator).append("video");
        return builder.toString();
    }

    public static boolean isFile(String filePath) {
        if (filePath != null) {
            if (new File(filePath).isFile()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取一个可用的存储路径（可能是内置的存储路径）
     *
     * @return 可用的存储路径
     */
    private static String getSDCardDir() {
        String pathDir = null;
        // 先获取内置sdcard路径
        File sdfile = Environment.getExternalStorageDirectory();
        // 获取内置sdcard的父路径
        File parentFile = sdfile.getParentFile();
        // 列出该父目录下的所有路径
        File[] listFiles = parentFile.listFiles();
        // 如果子路径可以写 就是拓展卡（包含内置的和外置的）

        if (listFiles == null) {
            return null;
        }
        long freeSizeMax = 0L;
        int length = listFiles.length;
        for (int i = 0; i < length; i++) {
            if (listFiles[i].canWrite()) {
                // listFiles[i]就是SD卡路径
                String tempPathDir = listFiles[i].getAbsolutePath();
                long tempSize = getSDFreeSize(tempPathDir);
                if (tempSize > freeSizeMax) {
                    freeSizeMax = tempSize;
                    pathDir = tempPathDir;
                }
            }
        }
        return pathDir;
    }

    /**
     * 获取指定目录剩余空间
     *
     * @return
     * @author EX-LIJINHUA001
     * @date 2013-6-7
     */
    public static long getSDFreeSize(String filePath) {

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            android.os.StatFs statfs = new android.os.StatFs(filePath);

            long nBlocSize = statfs.getBlockSize(); // 获取SDCard上每个block的SIZE

            long nAvailaBlock = statfs.getAvailableBlocks(); // 获取可供程序使用的Block的数量

            long nSDFreeSize = nAvailaBlock * nBlocSize; // 计算 SDCard
            // 剩余大小B
            return nSDFreeSize;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG, "httpFrame threadName:" + Thread.currentThread().getName()
                    + " getSDFreeSize  无法计算文件夹大小 folderPath:" + filePath);
        }

        return -1;
    }

    /**
     * 清除缓存文件
     */
    public static void clearAllData(Context context) {
        File file = new File("/data/data/" + context.getPackageName());
        File[] files = file.listFiles();
        if (null != files) {
            int length = files.length;
            for (int i = 0; i < length; i++) {
                File f = files[i];
                if (!f.getName().equals("lib")) {
                    deleteFile(f);
                }
            }
        }
        File file2 = new File(ROOT_PATH);
        deleteFile(file2);
    }

    /**
     * 删除文件
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            Log.v(TAG, "deleteFile  正在删除文件夹：" + file.getPath());
            File[] files = file.listFiles();
            if (null != files && files.length >= 1) {
                Log.v(TAG, "deleteFile  文件夹 包含" + files.length + "个File");
                int length = files.length;
                for (int i = 0; i < length; i++) {
                    deleteFile(files[i]);
                }
            }
            boolean ch = file.delete();
            Log.v(TAG, "deleteFile  删除文件夹(" + file.getPath() + ")：" + ch);
        } else {
            Log.v(TAG, "deleteFile  正在删除文件：" + file.getPath());
            boolean ch = file.delete();
            Log.v(TAG, "deleteFile  删除文件(" + file.getPath() + ")：" + ch);
        }
    }

    public static void deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            deleteFile(new File(filePath));
        }
    }

    public static String getFileName(String filePath) {
        if (filePath.length() > 0 && filePath != null) { // --截取文件名
            int i = filePath.lastIndexOf("/");
            return filePath.substring(i + 1, filePath.length());
        }
        return null;
    }

    /**
     * 通过filetype 来区分两种 小， 中 文件
     *
     * @param filePath
     * @param fileType
     * @return
     */
    public static String getFileName(String filePath, String fileType) {
        if (filePath.length() > 0 && filePath != null) { // --截取文件名
            int i = filePath.lastIndexOf("/");
            return fileType + "_" + filePath.substring(i + 1, filePath.length());
        }
        return null;
    }

    public static String copyImageFile2SdCard(String copyFilePath, String saveFloderPath) {

        // 如果传过来的路径为空
        if (TextUtils.isEmpty(copyFilePath)) {
            return null;
        }

        if (saveFloderPath == null || saveFloderPath.trim().equals("")) {
            return SAVE_FILE_STATUS_SDCARD_ERROR;
        }
        File srcPath = new File(copyFilePath);
        // 如果空间足够
        if (makeDir(saveFloderPath) && srcPath.length() < getSDFreeSize(saveFloderPath)) {
            File destPath = new File(saveFloderPath + File.separator + getFileName(copyFilePath) + ".jpg");

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(srcPath);
                out = new FileOutputStream(destPath);

                byte[] buffer = new byte[1024];

                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                return destPath.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                return SAVE_FILE_STATUS_SYSTEM_ERROR;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } else {
            return SAVE_FILE_STATUS_MEMORY_ERROR;
        }
    }

    private static boolean makeDir(String dir) {
        File destFloderPath = new File(dir);
        if (!destFloderPath.isDirectory()) {
            return destFloderPath.mkdirs();
        }
        return true;
    }

    /*
     * Java文件操作 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static String getPathFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1 && start < end) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }

    }

    public static boolean isDirectoryName(String filePath) {
        if (filePath.length() > 0 && filePath != null) {
            int i = filePath.lastIndexOf("/");
            return (i > -1) && (i < (filePath.length() - 1));
        }
        return false;
    }

    /**
     * 保存图片至相册
     *
     * @param cr        要保存内容的上下文
     * @param title     标题
     * @param dateTaken 时间戳
     * @param directory 目录
     * @param filename  文件名
     * @param source    图片文件
     */
    public static Uri addImage(ContentResolver cr, String title, long dateTaken, String directory, String filename,
                               Bitmap source) {
        File file = null;
        if (!FileUtils.isExistsSDCard() || filename == null)
            return null;

        String noFileName = getPathFileName(filename);
        String extName = "jpg";
        // 处理文件名
        if (noFileName == null) {
            noFileName = filename;
        }

        filename = noFileName + "." + extName;

        OutputStream outputStream = null;
        directory = getDir() + directory;
        String filePath = directory + "/" + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists())
                dir.mkdirs();

            // 处理重复名称
            boolean isFileExists = false;
            int index = 0;
            do {
                isFileExists = false;
                file = new File(filePath);
                if (file.exists()) {
                    isFileExists = true;
                    index++;
                    filePath = new StringBuilder().append(directory).append("/").append(noFileName).append("(").append(index).append(").").append(extName).toString();
                    filename = new StringBuilder().append(noFileName).append("(").append(index).append(").").append(extName).toString();
                }
            } while (isFileExists);

            outputStream = new FileOutputStream(file);
            if (source != null) {
                source.compress(CompressFormat.JPEG, 100, outputStream);
            } else
                return null;
        } catch (FileNotFoundException ex) {
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 插入数据
        long size = new File(directory, filename).length();
        ContentValues values = new ContentValues(7);
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, filename);
        values.put(Images.Media.DATE_TAKEN, dateTaken);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);

        return cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);

    }

    /**
     * 得到相册的目录
     */
    private static String getDir() {
        return Environment.getExternalStorageDirectory().toString() + "/DCIM/";
    }

    /**
     * 用时间戳生成照片/视频名称
     *
     * @param str jpg,png,3gp等格式
     * @return
     */
    public static String getImageAndVideoFileName(String str) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return dateFormat.format(date) + "." + str;
    }

    /**
     * @param fileName
     * @return
     * @describe 获取一个文件的大小
     * @author 陈大龙
     * @date 2014-12-19
     * @time 下午5:48:37
     * @type long
     */
    public static long getFileSize(String fileName) {

        long length = 0;

        if (TextUtils.isEmpty(fileName) || !isFile(fileName)) {
            return length;
        }
        FileInputStream fileIs = null;
        try {
            fileIs = new FileInputStream(new File(fileName));
            length = fileIs.available();

            if (fileIs != null) {
                fileIs.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return length;
        } catch (IOException e) {
            e.printStackTrace();
            return length;
        }
        return length;
    }

    /**
     * @return
     * @describe 返回文件的大小 KB MB GB
     * @author 陈大龙
     * @date 2014-12-19
     * @time 下午5:56:09
     * @paramfileName
     * @type String
     */
    public static String getFileSizeValue(long filesize) {

        String value = "0B";
        double size = (double) filesize;// 预先转换
        if (0 == size) {
            return value;
        }

        DecimalFormat format = new DecimalFormat("#.00");
        if (size < 1024) {
            value = format.format(size) + "B";
        } else if (size < (1024 * 1024)) {
            value = format.format(size / 1024) + "KB";
        } else if (size < (1024 * 1024 * 1024)) {
            value = format.format(size / 1048576) + "MB";
        } else {
            value = format.format(size / 1073741824) + "GB";
        }
        return value;
    }

    /**
     * @param filesize
     * @param format
     * @return
     * @describe 按照指定格式返回大小
     * @author 陈大龙
     * @date 2014-12-22
     * @time 上午12:11:25
     * @type double
     */
    public static double getFileSizeFormat(long filesize, int format) {

        double size = 0;
        double doublesize = (double) filesize;

        DecimalFormat df = new DecimalFormat("#.00");

        switch (format) {
            case FileSizeType.BB:
                size = Double.valueOf(df.format(doublesize));
                break;
            case FileSizeType.KB:
                size = Double.valueOf(df.format(doublesize / 1024));
                break;
            case FileSizeType.MB:
                size = Double.valueOf(df.format(doublesize / 1048576));
                break;
            case FileSizeType.GB:
                size = Double.valueOf(df.format(doublesize / 1073741824));
                break;
        }
        return size;
    }

    public static String getFileSizeFormat(double filesize) {
        String mSize = null;

        double doublesize = filesize;

        DecimalFormat df = new DecimalFormat("#.0");
        if (doublesize < 1024) {
            mSize = Double.valueOf(df.format(doublesize)) + "BB";
        } else if (doublesize >= 1024 && doublesize < Math.pow(1024, 2)) {
            mSize = Double.valueOf(df.format(doublesize / 1024)) + "KB";
        } else if (doublesize >= Math.pow(1024, 2) && doublesize < Math.pow(1024, 3)) {
            mSize = Double.valueOf(df.format(doublesize / 1048576)) + "MB";
        } else if (doublesize >= Math.pow(1024, 3) && doublesize < Math.pow(1024, 4)) {
            mSize = Double.valueOf(df.format(doublesize / 1073741824)) + "GB";
        }

        return mSize;
    }

    /**
     * @param filePath
     * @return
     * @describe 判断是否是gif文件 不能通过文件后缀名去判断！！
     * @author 陈大龙
     * @date 2014-12-22
     * @time 上午12:21:41
     * @type boolean
     */
    public static boolean isGifFile(String filePath) {

        // 作必要判断
        if (TextUtils.isEmpty(filePath) || !FileUiUtils.isFile(filePath)) {
            return false;
        }

        InputStream is = null;
        ByteArrayOutputStream os = null;

        try {
            is = new BufferedInputStream(new FileInputStream(new File(filePath)), 16 * 1024);
            os = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
            byte[] bb = os.toByteArray();
            Movie mMovie = Movie.decodeByteArray(bb, 0, bb.length);

            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            return mMovie != null;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param content
     * @return
     * @describe 没有文件的时候 简单判断
     * @author 陈大龙
     * @date 2014-12-22
     * @time 下午3:57:28
     * @type boolean
     */
    public static boolean isGifFileFormat(String content) {

        if (TextUtils.isEmpty(content)) {
            return false;
        }
        return content.toLowerCase().endsWith(".gif");
    }

    /**
     * @param localPath 本地路径
     * @param maxWh     最大宽高
     * @return
     * @describe 获取图片的宽高度
     * @author 陈大龙
     * @date 2014-12-23
     * @time 下午2:45:50
     * @type int[] 宽高数组[0]:宽 [1]:高
     */
    public static int[] getImageHeightWidth(String localPath, int maxWh) {

        if (TextUtils.isEmpty(localPath) || !FileUiUtils.isFile(localPath)) {
            return null;
        }

        int[] wh = new int[2];
        Bitmap bitmap = BitmapFactory.decodeFile(localPath);
        if (bitmap != null) {
            wh[0] = bitmap.getWidth() > maxWh ? maxWh : bitmap.getWidth();
            wh[1] = bitmap.getHeight() > maxWh ? maxWh : bitmap.getHeight();
            bitmap.recycle();
            return wh;
        }
        return null;
    }

    public static Drawable getImageDrawable(String path) {

        Drawable drawable = Drawable.createFromPath(path);

        return drawable;
    }

    public static String saveBarCode(Bitmap barCode, String groupId, String saveFloderPath) {
        return saveBarCode(barCode, groupId, saveFloderPath, false);
    }

    public static String saveBarCode(Bitmap barCode, String groupId, String saveFloderPath, boolean needTime) {

        // 如果传过来的路径为空
        if (barCode == null) {
            return null;
        }


        if (saveFloderPath == null || saveFloderPath.trim().equals("")) {
            return SAVE_FILE_STATUS_SDCARD_ERROR;
        }
        long size = barCode.getWidth() * barCode.getHeight() * 4l;
        // 如果空间足够
        if (makeDir(saveFloderPath) && size < getSDFreeSize(saveFloderPath)) {
            StringBuilder path = new StringBuilder();
            if (needTime) {
                path.append(saveFloderPath).append(File.separator).append(groupId).append("_").append(String.valueOf(System.currentTimeMillis())).append(".jpg");
            } else {
                path.append(saveFloderPath).append(File.separator).append(groupId).append(".jpg");
            }

            File f = new File(path.toString());
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream fOut = null;
            try {

                fOut = new FileOutputStream(f);
                barCode.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                return f.getAbsolutePath();

            } catch (Exception e) {
                e.printStackTrace();
                return SAVE_FILE_STATUS_SYSTEM_ERROR;
            } finally {
                try {
                    if (fOut != null) {
                        fOut.flush();
                        fOut.close();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } else {
            return SAVE_FILE_STATUS_MEMORY_ERROR;
        }
    }

    /**
     * uri转成path路径
     */
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                }
            } catch (Exception e) {
                Log.d("error", "获取路径失败");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isDamage(String photoPath) {
        BitmapFactory.Options options = null;
        if (options == null)
            options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(photoPath, options);
        return (options.mCancel || options.outWidth == -1
                || options.outHeight == -1);
    }

    public static boolean isFileExists(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        File file = new File(path);
        return file.exists();
    }

    public static String getCopyFilePath() {
        return COPY_FILE_PATH;
    }

    public static File getExternalCacheDir(Context context) {
        File cacheFloder = context.getExternalCacheDir();
        if (cacheFloder == null) {
            final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
            cacheFloder = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
        }

        if (!cacheFloder.isDirectory()) {
            boolean mkResult = cacheFloder.mkdirs();
            if (!mkResult) {
                cacheFloder = context.getCacheDir();
            }
        }
        return cacheFloder;
    }

    public static boolean copy(InputStream is, OutputStream out, boolean close) {
        boolean result = false;
        try {
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (close) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return result;
    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 解压到指定目录
     *
     * @param zipPath
     * @param descDir
     * @author isea533
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        UnZipFolder(zipPath, descDir);
    }

    /**
     * 解压缩zip包
     *
     * @param zipFileString zip包路径
     * @param outPathString 解压输出路径
     * @throws IOException
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws IOException {
        if (TextUtils.isEmpty(zipFileString) || TextUtils.isEmpty(outPathString)) {
            throw new IOException("压缩包路径或解压路径为空!");
        }
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
        File oldFile = new File(zipFileString);
        if (null != oldFile && oldFile.exists()) {
            oldFile.delete();
        }
    }

    private static final int PERMISSION_OPEN_READ_SDK = 120;

}
