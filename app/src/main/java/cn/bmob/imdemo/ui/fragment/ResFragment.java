package cn.bmob.imdemo.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.SearchResFileAdapter;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.model.ResFile;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.model.global.C;
import cn.bmob.imdemo.model.i.DownloadResFileListener;
import cn.bmob.imdemo.model.i.UploadResFileListener;
import cn.bmob.imdemo.util.FileUtil;
import cn.bmob.imdemo.util.TimeUtil;
import cn.bmob.imdemo.util.Util;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.app.Activity.RESULT_OK;

/**
 * 资源页面
 */
public class ResFragment extends ParentWithNaviFragment implements UploadResFileListener, DownloadResFileListener {

    @Bind(R.id.res_root)
    FrameLayout res_root;
    @Bind(R.id.et_file_name)
    EditText et_file_name;
    @Bind(R.id.fab_upload_res)
    FloatingActionButton upload_res;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.rc_view)
    RecyclerView recyclerView;
    SearchResFileAdapter adapter;
    List<ResFile> datas = new ArrayList<>();

    int res_type = C.ResType.Other;
    private ListPopupWindow mPopup;

    ProgressDialog progressDialog;


    @Override
    protected String title() {
        return "资源";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_res, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPopup = new ListPopupWindow(getActivity());
        adapter = new SearchResFileAdapter();
        adapter.setDownloadResFileListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @OnClick({R.id.btn_search, R.id.fab_upload_res})
    public void click(View v){
        switch (v.getId()){
            case R.id.btn_search:
                if(et_file_name.getText().toString().trim().length() > 0){
                    Util.HideKeyboard(recyclerView);
                    refreshLayout.setRefreshing(true);
                    searchFile(et_file_name.getText().toString().trim());
                }else{
                    showToast("请输入搜索内容");
                }
                break;
            case R.id.fab_upload_res:
                showUploadType();
                break;
        }
    }

    @OnTouch({R.id.res_root, R.id.rc_view})
    public boolean touch(View v){
        switch (v.getId()){
            case R.id.res_root:
            case R.id.rc_view:
                Util.HideKeyboard(recyclerView);
                break;
        }
        return false;
    }

    private static final int SELECT_FILE_CODE = 0x01;

    /**
     * 选择上传资源类型
     */
    public void showUploadType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
        builder.setTitle("资源类型");
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(C.upload_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                res_type = FileUtil.getResType(which);
                // 选择文件
                new LFilePicker().withSupportFragment(ResFragment.this)
                        .withRequestCode(SELECT_FILE_CODE)
                        .withIconStyle(Constant.ICON_STYLE_BLUE)
                        .withTitle("选择文件")
                        .withMaxNum(1)
                        .start();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE_CODE) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                if(list.size() > 0){
                    showPD();
                    FileUtil.uploadFile(list.get(0), this);
                }
            }
        }
    }

    private void showPD() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在上传，请稍后……");
        progressDialog.show();
    }

    @Override
    public void uploading(int progress) {
    }

    @Override
    public void uploadSuccess(String fileName, String fileUrl) {
        // 保存文件信息到后台
        ResFile file = new ResFile();
        file.setResType(res_type);
        file.setFileName(fileName);
        file.setFileUrl(fileUrl);
        file.setDownloadCount(0);
        file.setUploadTime(TimeUtil.getCurrTime(System.currentTimeMillis()));
        file.setUploadUserId(UserModel.getInstance().getCurrentUser().getObjectId());
        file.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    progressDialog.dismiss();
                    showToast("文件上传成功");
                }
            }
        });
    }

    @Override
    public void uploadError(String errorMsg) {
        progressDialog.dismiss();
        showToast("文件上传失败："+errorMsg);
    }

    /**
     * 搜索文件
     * @param word
     */
    private void searchFile(final String word){
        adapter.clear();
        datas.clear();
        BmobQuery<ResFile> query = new BmobQuery<>();
        query.findObjects(new FindListener<ResFile>() {
            @Override
            public void done(List<ResFile> list, BmobException e) {
                refreshLayout.setRefreshing(false);
                if(e == null){
                    if(list.size() > 0){
                        for (int i=0;i<list.size();i++) {
                            if(list.get(i).getFileName().contains(word)) {
                                datas.add(list.get(i));
                            }
                        }
                        if(datas.size() > 0){
                            adapter.setDatas(datas);
                        }else{
                            showToast("找不到相关文件");
                        }
                    }else{
                        showToast("找不到相关文件");
                    }
                }else{
                    showToast("搜索失败："+e.getMessage());
                }
            }
        });
    }

    @Override
    public void downloadStart() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在下载，请稍后……");
        progressDialog.show();
    }

    @Override
    public void downloading(int progress) {
    }

    @Override
    public void downloadSuccess(String savePath, final ResFile resFile) {
        progressDialog.dismiss();
        showToast("已保存至："+savePath);
        //更新下载次数
        final int count = resFile.getDownloadCount()+1;
        resFile.setDownloadCount(count);
        resFile.update(resFile.getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e == null){
                    for(int i=0;i<datas.size();i++){
                        if(datas.get(i).getObjectId().equals(resFile.getObjectId())){
                            datas.get(i).setDownloadCount(count);
                            adapter.setDatas(datas);
                            break;
                        }
                    }
                }
            }
        });

    }

    @Override
    public void downloadError(String errorMsg) {
        progressDialog.dismiss();
        showToast("下载失败："+ errorMsg);
    }
}
