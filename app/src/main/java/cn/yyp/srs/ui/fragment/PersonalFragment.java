package cn.yyp.srs.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yyp.srs.BaseApplication;
import cn.yyp.srs.R;
import cn.yyp.srs.base.ImageLoaderFactory;
import cn.yyp.srs.base.ParentWithNaviFragment;
import cn.yyp.srs.bean.User;
import cn.yyp.srs.event.AvatarUpdateEvent;
import cn.yyp.srs.event.RetUsernameEvent;
import cn.yyp.srs.model.UserModel;
import cn.yyp.srs.ui.LoginActivity;
import cn.yyp.srs.ui.MyDynamicActivity;
import cn.yyp.srs.ui.UserInfoActivity;
import cn.yyp.srs.ui.SetActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;
import cn.yyp.srs.util.Util;

/**
 * 个人
 */
public class PersonalFragment extends ParentWithNaviFragment {

    @Bind(R.id.personal_avatar)
    ImageView avatar;
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
        EventBus.getDefault().register(this);
        User user = UserModel.getInstance().getCurrentUser();
        ImageLoaderFactory.getLoader().loadAvator(avatar, user.getAvatar(), R.mipmap.head);
        String username = user.getUsername();
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
        BaseApplication.clearActivity();
        getActivity().finish();
        startActivity(LoginActivity.class, null);
    }

    @Subscribe
    public void onEventMainThread(RetUsernameEvent event){
        //更新用户名
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        tv_set_name.setText(TextUtils.isEmpty(username) ? "" : username);
    }

    @Subscribe
    public void onEventMainThread(AvatarUpdateEvent event){
        //更新头像
        ImageLoaderFactory.getLoader().loadAvator(avatar, event.getAvatarUrl(), R.mipmap.head);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
