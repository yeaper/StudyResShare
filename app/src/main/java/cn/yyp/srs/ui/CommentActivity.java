package cn.yyp.srs.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yyp.srs.R;
import cn.yyp.srs.adapter.CommentListAdapter;
import cn.yyp.srs.base.ParentWithNaviActivity;
import cn.yyp.srs.model.CampusDynamic;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CommentActivity extends ParentWithNaviActivity {

    @Bind(R.id.comment_list)
    RecyclerView recyclerView;
    CommentListAdapter adapter;
    String dynamicId = "";

    @Override
    protected String title() {
        return "评论列表";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initNaviView();

        if(getIntent() != null){
            dynamicId = getIntent().getStringExtra("dynamicId");
        }

        adapter = new CommentListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        getCommentData();
    }

    /**
     * 获取评论数据
     */
    private void getCommentData(){
        BmobQuery<CampusDynamic> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", dynamicId);
        query.findObjects(new FindListener<CampusDynamic>() {
            @Override
            public void done(List<CampusDynamic> list, BmobException e) {
                if(e == null){
                  if(list.size()>0){
                      adapter.setDatas(list.get(0).getCommentList());
                  }
                }else{
                    showToast("评论信息获取失败");
                }
            }
        });
    }
}
