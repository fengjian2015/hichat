package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wewin.hichat.R;

/**
 * 提示框dialog
 * Created by Darren on 2019/1/19
 */
public class PromptDialog extends Dialog {

    private PromptDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class PromptBuilder{

        private PromptDialog promptDialog;
        private TextView contentTv, cancelTv, confirmTv, dividerTv;
        private Activity activity;
        private OnConfirmClickListener confirmClickListener;
        private boolean isCancelableOnTouchOutside = true;

        public PromptBuilder(Activity activity){
            promptDialog = new PromptDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_prompt, null);
            promptDialog.addContentView(dialogView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            contentTv = dialogView.findViewById(R.id.tv_dialog_prompt_content);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_prompt_cancel);
            confirmTv = dialogView.findViewById(R.id.tv_dialog_prompt_confirm);
            dividerTv = dialogView.findViewById(R.id.tv_dialog_prompt_bot_divider);
            this.activity = activity;
        }

        public PromptDialog create(){
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (promptDialog != null){
                        promptDialog.dismiss();
                    }
                }
            });

            confirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (confirmClickListener != null){
                        confirmClickListener.confirmClick();
                    }
                    if (promptDialog != null){
                        promptDialog.dismiss();
                    }
                }
            });

            promptDialog.setCancelable(true);
            promptDialog.setCanceledOnTouchOutside(isCancelableOnTouchOutside);

            return promptDialog;
        }

        public interface OnConfirmClickListener{
            void confirmClick();
        }

        public PromptBuilder setOnConfirmClickListener(OnConfirmClickListener confirmClickListener){
            this.confirmClickListener = confirmClickListener;
            return this;
        }

        public PromptBuilder setPromptContent(String content){
            contentTv.setText(content);
            return this;
        }

        public PromptBuilder setPromptContent(int resourceId){
            contentTv.setText(activity.getString(resourceId));
            return this;
        }

        public PromptBuilder setCancelVisible(boolean isVisible){
            if (isVisible){
                cancelTv.setVisibility(View.VISIBLE);
                dividerTv.setVisibility(View.VISIBLE);
            }else {
                cancelTv.setVisibility(View.GONE);
                dividerTv.setVisibility(View.GONE);
            }
            return this;
        }

        public PromptBuilder setCancelableOnTouchOutside(boolean state){
            this.isCancelableOnTouchOutside = state;
            return this;
        }
    }


}
