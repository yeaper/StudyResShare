package cn.yyp.srs.model.i;

import cn.yyp.srs.model.ResFile;

/**
 * 文件下载接口
 */

public interface DownloadResFileListener {
    void downloadStart();
    void downloading(int progress);
    void downloadSuccess(String savePath, ResFile resFile);
    void downloadError(String errorMsg);
}
