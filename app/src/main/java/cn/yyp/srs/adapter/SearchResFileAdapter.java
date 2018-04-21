package cn.yyp.srs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.yyp.srs.R;
import cn.yyp.srs.model.ResFile;
import cn.yyp.srs.model.i.DownloadResFileListener;
import cn.yyp.srs.util.FileUtil;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.yyp.srs.util.Util;


/**
 * 资源适配器
 */
public class SearchResFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ResFile> files = new ArrayList<>();

    public void setDatas(List<ResFile> list) {
        files.clear();
        if (null != list) {
            files.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        files.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(parent.getContext(), parent, onRecyclerViewListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class MyHolder extends BaseViewHolder<ResFile>{

        @Bind(R.id.res_file_name)
        public TextView file_name;
        @Bind(R.id.res_file_upload_time)
        public TextView file_upload_time;
        @Bind(R.id.res_file_download_count)
        public TextView file_download_count;
        @Bind(R.id.res_file_type)
        TextView file_type;
        @Bind(R.id.res_file_download)
        public ImageView btn_file_download;

        public MyHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
            super(context, root, R.layout.item_res_file, onRecyclerViewListener);
        }

        @Override
        public void bindData(final ResFile resFile) {
            file_name.setText(resFile.getFileName());
            file_upload_time.setText(resFile.getUploadTime());
            if(resFile.getDownloadCount() <= 0){
                file_download_count.setVisibility(View.GONE);
            }else if(resFile.getDownloadCount() < 100){
                file_download_count.setVisibility(View.VISIBLE);
                file_download_count.setText("已下载"+resFile.getDownloadCount()+"次");
            }else{
                file_download_count.setVisibility(View.VISIBLE );
                file_download_count.setText("已下载99+次");
            }
            file_type.setText(FileUtil.getResTypeName(resFile.getResType()));
            btn_file_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //下载文件
                    BmobFile bmobfile =new BmobFile(resFile.getFileName(), "", resFile.getFileUrl());
                    //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
                    File saveFile = new File(Util.getSDPath()+"/StudyResShare/", resFile.getFileName());
                    bmobfile.download(saveFile, new DownloadFileListener() {

                        @Override
                        public void onStart() {
                            if(downloadResFileListener != null){
                                downloadResFileListener.downloadStart();
                            }
                        }

                        @Override
                        public void done(String savePath,BmobException e) {
                            if(e==null){
                                if(downloadResFileListener != null){
                                    downloadResFileListener.downloadSuccess(savePath, resFile);
                                }
                            }else{
                                if(downloadResFileListener != null){
                                    downloadResFileListener.downloadError(e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onProgress(Integer value, long newworkSpeed) {
                            if(downloadResFileListener != null){
                                downloadResFileListener.downloading(value);
                            }
                        }

                    });
                }
            });
        }
    }

    private DownloadResFileListener downloadResFileListener;
    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public void setDownloadResFileListener(DownloadResFileListener downloadResFileListener) {
        this.downloadResFileListener = downloadResFileListener;
    }
}
