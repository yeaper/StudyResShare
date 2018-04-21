package cn.yyp.srs.model.i;

/**
 * 文件上传接口
 */

public interface UploadResFileListener {
    void uploading(int progress);
    void uploadSuccess(String fileName, String fileUrl);
    void uploadError(String errorMsg);
}
