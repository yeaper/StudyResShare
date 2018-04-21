package cn.yyp.srs.model;

import android.content.Context;

import cn.yyp.srs.BaseApplication;


public abstract class BaseModel {

    public int CODE_NULL=1000;
    public static int CODE_NOT_EQUAL=1001;

    public static final int DEFAULT_LIMIT=20;

    public Context getContext(){
        return BaseApplication.INSTANCE();
    }
}
