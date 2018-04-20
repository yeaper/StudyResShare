package cn.bmob.imdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.model.i.IResetPwdCallback;
import cn.bmob.newim.BmobIM;

public class ResetPwdActivity extends ParentWithNaviActivity {

    @Bind(R.id.et_old_password)
    EditText old_pwd;
    @Bind(R.id.et_new_password)
    EditText new_pwd;

    @Override
    protected String title() {
        return "修改密码";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ButterKnife.bind(this);
        initNaviView();
    }

    @OnClick(R.id.btn_reset_pwd)
    public void setPwd(View v){
        UserModel.getInstance().resetPwd(old_pwd.getText().toString().trim()
                , new_pwd.getText().toString().trim(), new IResetPwdCallback() {
                    @Override
                    public void setSuccess() {
                        showToast("修改成功，请重新登录");
                        UserModel.getInstance().logout();
                        //退出登录需要断开与IM服务器的连接
                        BmobIM.getInstance().disConnect();
                        finish();
                        startActivity(LoginActivity.class, null);
                    }

                    @Override
                    public void setError(String msg) {
                        showToast("修改失败："+msg);
                    }
                });
    }
}
