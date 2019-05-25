package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.wewin.hichat.R;

/**
 * Created by Darren on 2019/2/28
 */
public class SexDialog extends Dialog {

    private SexDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class SexBuilder implements View.OnClickListener {
        private SexDialog sexDialog;
        private TextView secretTv, maleTv, femaleTv, cancelTv;
        private OnSexSelectListener sexSelectListener;

        public SexBuilder(Activity activity){
            sexDialog = new SexDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_more_personal_sex, null);
            sexDialog.setContentView(dialogView);
            secretTv = dialogView.findViewById(R.id.tv_dialog_sex_keep_secret);
            maleTv = dialogView.findViewById(R.id.tv_dialog_sex_male);
            femaleTv = dialogView.findViewById(R.id.tv_dialog_sex_female);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_sex_cancel);
        }

        public SexDialog create(){
            secretTv.setOnClickListener(this);
            maleTv.setOnClickListener(this);
            femaleTv.setOnClickListener(this);
            cancelTv.setOnClickListener(this);

            sexDialog.setCancelable(true);
            sexDialog.setCanceledOnTouchOutside(true);
            if (sexDialog.getWindow() != null){
                sexDialog.getWindow().setGravity(Gravity.BOTTOM);
            }

            return sexDialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_dialog_sex_keep_secret:
                    if (sexSelectListener != null){
                        sexSelectListener.secretClick();
                    }
                    break;

                case R.id.tv_dialog_sex_male:
                    if (sexSelectListener != null){
                        sexSelectListener.maleClick();
                    }
                    break;

                case R.id.tv_dialog_sex_female:
                    if (sexSelectListener != null){
                        sexSelectListener.femaleClick();
                    }
                    break;

                case R.id.tv_dialog_sex_cancel:
                    if (sexDialog != null){
                        sexDialog.dismiss();
                    }
                    break;

                default:
                    break;
            }
            if (sexDialog != null){
                sexDialog.dismiss();
            }
        }

        public SexBuilder setOnSexSelectListener(OnSexSelectListener sexSelectListener){
            this.sexSelectListener = sexSelectListener;
            return this;
        }

        public interface OnSexSelectListener{
            void secretClick();

            void maleClick();

            void femaleClick();
        }
    }

}
