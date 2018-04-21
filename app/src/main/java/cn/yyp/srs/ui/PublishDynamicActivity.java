package cn.yyp.srs.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yyp.srs.R;
import cn.yyp.srs.base.ParentWithNaviActivity;
import cn.yyp.srs.event.PublishDynamicEvent;
import cn.yyp.srs.model.CampusDynamic;
import cn.yyp.srs.model.DynamicComment;
import cn.yyp.srs.model.UserModel;
import cn.yyp.srs.util.TimeUtil;
import cn.yyp.srs.util.Util;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class PublishDynamicActivity extends ParentWithNaviActivity{

    @Bind(R.id.dynamic_content)
    EditText dynamic_content;
    @Bind(R.id.content_length_tip)
    TextView content_length_tip;

    ProgressDialog progressDialog;

    @Override
    protected String title() {
        return "发布动态";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_dynamic);
        ButterKnife.bind(this);
        //导航栏
        initNaviView();
        dynamic_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content_length_tip.setText(s.length() + "/200字");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @OnClick({R.id.btn_publish})
    public void click(View v){
        if(dynamic_content.getText().toString().trim().length()>0){
            Util.HideKeyboard(dynamic_content);
            showPD();
            publish(dynamic_content.getText().toString().trim());
        }else{
            showToast("请输入内容");
        }
    }

    private void showPD() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在发布，请稍后……");
        progressDialog.show();
    }

    /**
     * 发布动态
     * @param content
     */
    private void publish(String content){
        CampusDynamic dynamic = new CampusDynamic();
        dynamic.setAuthorId(UserModel.getInstance().getCurrentUser().getObjectId());
        dynamic.setContent(content);
        dynamic.setAuthorName(UserModel.getInstance().getCurrentUser().getUsername());
        dynamic.setTime(TimeUtil.getCurrTime(System.currentTimeMillis()));
        dynamic.setCommentList(new ArrayList<DynamicComment>());
        dynamic.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    progressDialog.dismiss();
                    showToast("发布成功");
                    EventBus.getDefault().post(new PublishDynamicEvent(true));
                    finish();
                }else{
                    showToast("发布失败："+e.getMessage());
                }
            }
        });
    }
}
