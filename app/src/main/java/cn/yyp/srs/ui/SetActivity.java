package cn.yyp.srs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yyp.srs.R;
import cn.yyp.srs.base.ParentWithNaviActivity;

public class SetActivity extends ParentWithNaviActivity {

    @Override
    protected String title() {
        return "设置";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        initNaviView();
    }

    @OnClick({R.id.layout_update_app,R.id.layout_feedback})
    public void click(View v){
        switch (v.getId()){
            case R.id.layout_update_app:
                showToast("已是最新版本");
                break;
            case R.id.layout_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
        }
    }
}
