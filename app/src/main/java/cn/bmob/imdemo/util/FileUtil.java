package cn.bmob.imdemo.util;

import java.io.File;

import cn.bmob.imdemo.model.global.C;
import cn.bmob.imdemo.model.i.UploadResFileListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 文件工具
 */

public class FileUtil {

    /**
     * 获取文件类型
     * @param which
     * @return
     */
    public static int getResType(int which){
        switch (which){
            case 0:
                return C.ResType.English_Zone;
            case 1:
                return C.ResType.Sport_Health;
            case 2:
                return C.ResType.Study_Res;
            case 3:
                return C.ResType.Poem;
            case 4:
                return C.ResType.Tour_Strategy;
            case 5:
                return C.ResType.Music;
            case 6:
                return C.ResType.Other;
            default:
                return C.ResType.Other;
        }
    }

    /**
     * 获取文件类型名
     * @param which
     * @return
     */
    public static String getResTypeName(int which){
        switch (which){
            case 1:
                return C.upload_type[0];
            case 2:
                return C.upload_type[1];
            case 3:
                return C.upload_type[2];
            case 4:
                return C.upload_type[3];
            case 5:
                return C.upload_type[4];
            case 6:
                return C.upload_type[5];
            case 7:
                return C.upload_type[6];
            default:
                return C.upload_type[6];
        }
    }

    /**
     * 上传文件
     * @param path
     */
    public static void uploadFile(String path, final UploadResFileListener listener){
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e == null){
                    if(listener != null){
                        listener.uploadSuccess(bmobFile.getFilename(), bmobFile.getFileUrl());
                    }
                }else{
                    if(listener != null){
                        listener.uploadError(e.getMessage());
                    }
                }
            }

            @Override
            public void onProgress(Integer value) {
                if(listener != null){
                    listener.uploading(value);
                }
            }
        });
    }
}
