package com.wewin.hichat.androidlib.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.wewin.hichat.androidlib.utils.CommonUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.model.db.entity.HttpResult;
import com.wewin.hichat.view.login.LoginActivity;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Darren on 2018/12/18.
 */
public class HttpCallBack implements Callback {

    private String classMethodName;
    private Context context;
    private Handler handler;
    private final int CODE_COOKIE_INVALID = -7;

    protected HttpCallBack(Context context, String classMethodName) {
        this.classMethodName = classMethodName;
        this.context = context;
        handler = new Handler();
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        LogUtil.i(classMethodName, e.getMessage());
        handler.post(new Runnable() {
            @Override
            public void run() {
                failure("");
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        ResponseBody body = response.body();
        if (body != null) {
            String result = body.string();
            LogUtil.i(classMethodName + "httpResult", result);
            try {
                final HttpResult httpResult = JSON.parseObject(result, HttpResult.class);
                if (httpResult.getCode() == 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (httpResult.getPages() > 0) {
                                success(httpResult.getData(), httpResult.getCount(),
                                        httpResult.getPages());
                            } else {
                                success(httpResult.getData(), httpResult.getCount());
                            }
                        }
                    });
                    successOnChildThread(httpResult.getData(), httpResult.getCount(),
                            httpResult.getPages());

                } else if (httpResult.getCode() == CODE_COOKIE_INVALID) {
                    startLoginActivityForCookieInvalid(context, "");

                } else if (context != null) {
                    LogUtil.i(classMethodName, httpResult.getCode() + httpResult.getDesc());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            failure(httpResult.getDesc());
                            failure(httpResult.getCode(), httpResult.getDesc());
                        }
                    });
                    failureOnChildThread(httpResult.getCode(), httpResult.getDesc());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //主线程回调
    public void success(Object data, int count) {
    }

    //主线程回调
    public void success(Object data, int count, int pages) {
    }

    //子线程回调
    public void successOnChildThread(Object data, int count, int pages) {
    }

    //主线程回调
    public void failure(String desc) {
        if (!TextUtils.isEmpty(desc)) {
            ToastUtil.showShort(context, desc);
        }
    }

    //主线程回调
    private void failure(int code, String desc) {
    }

    //子线程回调
    private void failureOnChildThread(int code, String desc) {
    }

    //cookie失效跳转登录页面
    private void startLoginActivityForCookieInvalid(Context context, String cookieValidInfo) {
        LogUtil.i("startLoginActivityForCookieInvalid");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, true);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_DIALOG, false);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_INFO, cookieValidInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        CommonUtil.clearDataByLogout(context);
    }


}
