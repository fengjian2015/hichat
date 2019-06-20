package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ActivityUtil;

/**
 * Created by GA on 2017/11/15.
 * 加载框
 */

public class LoadingProgressDialog extends Dialog {
    private Context context = null;
    private static LoadingProgressDialog sLoadingProgressDialog = null;
    private ProgressCancelListener mProgressCancelListener;

    public LoadingProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setProgressCancelListener( ProgressCancelListener mProgressCancelListener){
        this.mProgressCancelListener=mProgressCancelListener;
    }

    public static LoadingProgressDialog createDialog(Context context) {
        sLoadingProgressDialog = new LoadingProgressDialog(context, R.style.LoadingProgressDialog);
        sLoadingProgressDialog.setContentView(R.layout.dialog_loading_progress);
        sLoadingProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return sLoadingProgressDialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (sLoadingProgressDialog == null) {
            return;
        }
        ImageView imageView = (ImageView) sLoadingProgressDialog.findViewById(R.id.iv_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    /**
     * [Summary]
     * setTitile 标题
     *
     * @param strTitle
     * @return
     */
    public LoadingProgressDialog setTitile(String strTitle) {
        return sLoadingProgressDialog;
    }

    /**
     * [Summary]
     * setMessage 提示内容
     *
     * @param strMessage
     * @return
     */
    public LoadingProgressDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) sLoadingProgressDialog.findViewById(R.id.tv_loading);
        if (tvMsg != null) {
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(strMessage);
        }
        return sLoadingProgressDialog;
    }

    public void showDialog(){
        if(!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        if (context instanceof Activity) {
            show();
        }
    }


    public void hideDialog(){
        if(!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        if(mProgressCancelListener!=null) {
            mProgressCancelListener.onCancelProgress();
        }
        if (context instanceof Activity) {
            dismiss();
        }
    }

    public interface ProgressCancelListener{
        void onCancelProgress();
    }
}