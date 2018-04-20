package cn.bmob.imdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    /**
     * 显示键盘
     * @param context
     * @param view
     */
    public static void showInputMethod(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && !imm.isActive()) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏虚拟键盘
     */
    public static void HideKeyboard(View v){
        InputMethodManager imm = ( InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }
}
