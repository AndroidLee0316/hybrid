package com.pasc.libbrowser.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.util.Base64;
import com.pasc.libbrowser.ApiGenerator;
import com.pasc.libbrowser.CommonApi;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import okhttp3.ResponseBody;

/**
 * Created by yangwen881 on 17/5/24.
 */

public class BitmapUtils {
    private final static int CV_ROTATE_90_CLOCKWISE = 0; //Rotate 90 degrees clockwise
    private final static int CV_ROTATE_180 = 1; //Rotate 180 degrees clockwise
    private final static int CV_ROTATE_90_COUNTERCLOCKWISE = 2; //Rotate 270 degrees clockwise
    public final static int CV_ROTATE_360 = 3; //Rotate 270 degrees clockwise

    /**
     * @throws IOException
     */
    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        Bitmap bitmap = null;

        int wRatio = (int) Math.ceil(options.outWidth / (float) 720);
        int hRatio = (int) Math.ceil(options.outHeight / (float) 1280);
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                options.inSampleSize = wRatio;
            } else {
                options.inSampleSize = hRatio;
            }
        }
        in = new BufferedInputStream(new FileInputStream(new File(path)));
        options.inJustDecodeBounds = false;
        // 杯具的老戳手机-1G以下的内存的某些手机无法加载高清图片大于1M以上，只能加大压缩力度
        try {
            bitmap = BitmapFactory.decodeStream(in, null, options);
        } catch (Exception e) {
            e.printStackTrace();
            wRatio = (int) Math.ceil(options.outWidth / (float) 480);
            hRatio = (int) Math.ceil(options.outHeight / (float) 800);

            if (wRatio > 1 && hRatio > 1) {
                if (wRatio > hRatio) {
                    options.inSampleSize = wRatio;
                } else {
                    options.inSampleSize = hRatio;
                }
            }
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(in, null, options);

            // 获取图片的旋转角度
            int degree = readPictureDegree(path);
            if (degree <= 0) {
                return bitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                Bitmap rotaBitmap =
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                                matrix, true);
                return rotaBitmap;
            }
        }
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将bitmap保存到SD卡中，返回路径
     *
     * @param @param bitmap 保存的图片
     * @param @param folder 存入的文件夹名称，注意，要写 "/"
     * @param @param picName 要存入的图片命名
     * @return String 返回图片在sd卡中的路径
     * @Title: saveBitmapToSDCard
     */
    public static String saveBitmap2File(Bitmap bitmap, String filePath) {
        File targetFile = new File(filePath);
        if (bitmap == null) {
            return "";
        }
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(targetFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    /**
     * @param toBmpSize 单位kb
     */
    public static Bitmap compress(Bitmap bitmap, int toBmpSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        double mid = b.length / 1024;
        while (mid > toBmpSize) {
            double i = mid / toBmpSize;
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 获取网络图片
     */
    public static Bitmap getURLImage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(true);//缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 压缩图片
     */
    public static boolean compressPhoto(String fileSrc, String fileDst, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        // 计算缩放
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileSrc, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        int angle = readPictureDegree(fileSrc);
        Bitmap bitmap = rotaingImageView(angle, BitmapFactory.decodeFile(fileSrc, bmOptions));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        FileOutputStream fos;
        try {
            File file = new File(fileDst);
            fos = new FileOutputStream(file);
            fos.write(stream.toByteArray());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片旋转回原来的
     */

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if(angle == 0){
            return bitmap;
        }
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static String BitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    public static Bitmap getBitmap(int imageWidth,int imageHeight,byte[] frame,int ori){
        Bitmap bitmap = null;
        try{
            YuvImage image = new YuvImage(frame, ImageFormat.NV21, imageWidth, imageHeight, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0,imageWidth, imageHeight), 100, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        float rotate;
        if (ori == CV_ROTATE_90_CLOCKWISE) {
            rotate = 90;
        } else if (ori  == CV_ROTATE_90_COUNTERCLOCKWISE) {
            rotate = 270;
        } else if (ori  == CV_ROTATE_180) {
            rotate = 180;
        } else {
            rotate = 360;
        }
        if(bitmap != null) {
            bitmap = rotateBitmap(bitmap, rotate);
        }
        return bitmap;
    }


    //旋转bitmap
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 附件下载
     * Bitmap回调，不需要存储权限
     */
    public static void downloadFileByBitmap(@NonNull String url,
            final FileDownLoadObserver<Bitmap> fileDownLoadObserver) {

        ApiGenerator.createApi(CommonApi.class)
                .loadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<ResponseBody, Bitmap>() {
                    @Override public Bitmap apply(@NonNull ResponseBody responseBody)
                            throws Exception {
                        return BitmapUtils.zoomImage(
                                BitmapFactory.decodeStream(responseBody.byteStream()), 100, 100);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileDownLoadObserver);
    }
}
