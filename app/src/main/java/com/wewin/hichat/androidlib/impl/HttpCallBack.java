package com.wewin.hichat.androidlib.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.CommonUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.dialog.LoadingProgressDialog;
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
public class HttpCallBack implements Callback ,LoadingProgressDialog.ProgressCancelListener{

    private String classMethodName;
    private Context context;
    private Handler handler;
    private final int CODE_COOKIE_INVALID = -7;
    private LoadingProgressDialog progressDialog;
    /**
     * 是否需要显示默认Loading
     */
    private boolean showProgress = true;

    protected HttpCallBack(Context context, String classMethodName) {
        this.classMethodName = classMethodName;
        this.context = context;
        handler = new Handler();

    }

    protected HttpCallBack(Context context, String classMethodName,boolean showProgress) {
        this.classMethodName = classMethodName;
        this.context = context;
        handler = new Handler();
        this.showProgress=showProgress;
        if (showProgress){
            progressDialog = LoadingProgressDialog.createDialog(context);
            progressDialog.setProgressCancelListener(this);
            showProgressDialog();
        }
    }

    protected HttpCallBack(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    protected HttpCallBack(String classMethodName,boolean showProgress) {
        this.classMethodName = classMethodName;
        this.showProgress=showProgress;
        if (showProgress){
            progressDialog = LoadingProgressDialog.createDialog(context);
            progressDialog.setProgressCancelListener(this);
            showProgressDialog();
        }
    }

    private void showProgressDialog() {
        if (showProgress && null != progressDialog && ActivityUtil.isActivityOnTop(context)) {
            progressDialog.showDialog();
        }
    }

    private void dismissProgressDialog() {
        if (showProgress && null != progressDialog) {
            progressDialog.hideDialog();
        }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
        dismissProgressDialog();
        LogUtil.i(classMethodName, e.getMessage());
        if (handler != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    failure("请求失败");
                }
            });
        }
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        dismissProgressDialog();
        ResponseBody body = response.body();
        if (body != null) {
            String result = body.string();
            LogUtil.i(classMethodName + "httpResult", result);
            try {
                final HttpResult httpResult = JSON.parseObject(result, HttpResult.class);
                if (httpResult.getCode() == 0) {
                    if (handler != null) {
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
                    } else {
                        successOnChildThread(httpResult.getData(), httpResult.getCount(),
                                httpResult.getPages());
                    }

                } else if (httpResult.getCode() == CODE_COOKIE_INVALID) {
                    startLoginActivityForCookieInvalid(context);

                } else {
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                failure(httpResult.getDesc());
                                failure(httpResult.getCode(), httpResult.getDesc());
                            }
                        });
                    } else {
                        failureOnChildThread(httpResult.getCode(), httpResult.getDesc());
                    }
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
    public void failure(int code, String desc) {
    }

    //子线程回调
    private void failureOnChildThread(int code, String desc) {
        LogUtil.i("failureOnChildThread code ", code + ", desc: " + desc);
    }

    //cookie失效跳转登录页面
    private void startLoginActivityForCookieInvalid(Context context) {
        if (context == null) {
            return;
        }
        LogUtil.i("startLoginActivityForCookieInvalid");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, true);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_DIALOG, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        CommonUtil.clearDataByLogout(context);
    }

    @Override
    public void onCancelProgress() {

    }
}
