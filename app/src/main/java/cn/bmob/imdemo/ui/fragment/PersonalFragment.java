package cn.bmob.imdemo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.ui.LoginActivity;
import cn.bmob.imdemo.ui.MyDynamicActivity;
import cn.bmob.imdemo.ui.UserInfoActivity;
import cn.bmob.imdemo.ui.SetActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;

/**
 * 个人
 */
public class PersonalFragment extends ParentWithNaviFragment {

    @Bind(R.id.tv_set_name)
    TextView tv_set_name;

    @Override
    protected String title() {
        return "个人";
    }

    public static PersonalFragment newInstance() {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PersonalFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        tv_set_name.setText(TextUtils.isEmpty(username) ? "" : username);
        return rootView;
    }

    @OnClick({R.id.layout_info,R.id.layout_my_dynamic,R.id.personal_set})
    public void click(View view) {
        switch (view.getId()){
            case R.id.layout_info:
                Bundle bundle = new Bundle();
                bundle.putSerializable("u", BmobUser.getCurrentUser(User.class));
                startActivity(UserInfoActivity.class, bundle);
                break;
            case R.id.layout_my_dynamic:
                startActivity(new Intent(getActivity(), MyDynamicActivity.class));
                break;
            case R.id.personal_set:
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;
        }

    }

    @OnClick(R.id.btn_logout)
    public void onLogoutClick(View view) {
        UserModel.getInstance().logout();
        //TODO 连接：3.2、退出登录需要断开与IM服务器的连接
        BmobIM.getInstance().disConnect();
        getActivity().finish();
        startActivity(LoginActivity.class, null);
    }
}
