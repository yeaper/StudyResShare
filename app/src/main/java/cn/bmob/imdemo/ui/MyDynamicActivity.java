package cn.bmob.imdemo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.DynamicAdapter;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.bean.Friend;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.model.CampusDynamic;
import cn.bmob.imdemo.model.DynamicComment;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.model.i.OnDynamicCommentListener;
import cn.bmob.imdemo.util.TimeUtil;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MyDynamicActivity extends ParentWithNaviActivity implements OnDynamicCommentListener {

    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.rc_view)
    RecyclerView recyclerView;
    DynamicAdapter adapter;
    List<CampusDynamic> datas = new ArrayList<>();

    @Override
    protected String title() {
        return "我的动态";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamic);
        ButterKnife.bind(this);
        initNaviView();

        adapter = new DynamicAdapter();
        adapter.setOnDynamicCommentListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDynamic();
            }
        });
        getDynamic();
    }

    private void getDynamic(){
        refreshLayout.setRefreshing(true);
        adapter.clear();
        datas.clear();
        BmobQuery<CampusDynamic> query = new BmobQuery<>();
        query.addWhereEqualTo("authorId", UserModel.getInstance().getCurrentUser().getObjectId());
        query.findObjects(new FindListener<CampusDynamic>() {
            @Override
            public void done(final List<CampusDynamic> list, BmobException e) {
                refreshLayout.setRefreshing(false);
                if(e == null){
                    if(list.size()>0){
                        datas.addAll(list);
                        adapter.setDatas(datas);
                    }else{
                        refreshLayout.setRefreshing(false);
                        showToast("暂无动态");
                    }
                }else{
                    refreshLayout.setRefreshing(false);
                    showToast("暂无动态");
                }

            }
        });


    }

    @Override
    public void onComment(final User user, final String dynamicId) {
        final EditText et = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("评论")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(et.getText().toString().trim().length() > 0){
                            comment(et.getText().toString().trim(), user, dynamicId);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 评论操作
     * @param content
     */
    private void comment(final String content, final User user, final String dynamicId){
        CampusDynamic dynamic = new CampusDynamic();
        dynamic.setObjectId(dynamicId);
        dynamic.add("commentList", new DynamicComment(user.getObjectId(), user.getUsername()
                , content, TimeUtil.getCurrTime(System.currentTimeMillis())));
        dynamic.update(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    for(int i=0;i<datas.size();i++){
                        if(datas.get(i).getObjectId().equals(dynamicId)){
                            datas.get(i).getCommentList().add(new DynamicComment(user.getObjectId()
                                    , user.getUsername(), content, ""));
                            adapter.setDatas(datas);
                            break;
                        }
                    }
                }else{
                    showToast("评论失败："+e.getMessage());
                }
            }

        });
    }
}
