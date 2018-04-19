package cn.bmob.imdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * 通用工具
 */
public class Util {

    public static boolean checkSdCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSDPath(){
        File sdDir = null;
        if(checkSdCard()) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        if(sdDir != null) {
            return sdDir.toString();
        }
        return "";
    }
}
