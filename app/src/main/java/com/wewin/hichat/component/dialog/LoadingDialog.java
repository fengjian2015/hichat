package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.wewin.hichat.R;

/**
 * Created by Darren on 2019/4/29
 **/
public class LoadingDialog extends Dialog {

    private LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class LoadingBuilder{

        private final LoadingDialog dialog;
        private final ImageView loadingIv;
        private Activity activity;

        public LoadingBuilder(Activity activity){
            this.activity = activity;
            dialog = new LoadingDialog(activity, R.style.dialog_loading);
            View dialogView = View.inflate(activity, R.layout.dialog_loading, null);
            loadingIv = dialogView.findViewById(R.id.iv_dialog_loading);
            dialog.setContentView(dialogView);
        }

        public LoadingDialog create(){
            Animation rotateAnimation = AnimationUtils.loadAnimation(activity, R.anim.rotate360_anim);
            loadingIv.startAnimation(rotateAnimation);

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    loadingIv.clearAnimation();
                }
            });
            return dialog;
        }
    }


}
