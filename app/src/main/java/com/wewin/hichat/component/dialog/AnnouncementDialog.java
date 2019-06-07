package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;

/**
 * @author jason
 * 公告dialog
 * Created by Darren on 2019/1/19
 */
public class AnnouncementDialog extends Dialog {

    private AnnouncementDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class PromptBuilder{

        private AnnouncementDialog promptDialog;
        private TextView contentTv, confirmTv,titleTv,nameTimeTv;
        private ImageView cancelIv;
        private Activity activity;
        private OnConfirmClickListener confirmClickListener;
        private OnCancelClickListener cancelClickListener;
        private boolean isCancelableOnTouchOutside = true;

        public PromptBuilder(Activity activity){
            promptDialog = new AnnouncementDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_announcement, null);
            promptDialog.addContentView(dialogView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            contentTv = dialogView.findViewById(R.id.tv_dialog_prompt_content);
            confirmTv = dialogView.findViewById(R.id.tv_dialog_prompt_confirm);
            cancelIv=dialogView.findViewById(R.id.iv_dialog_cancel);
            titleTv=dialogView.findViewById(R.id.tv_dialog_prompt_title);
            nameTimeTv=dialogView.findViewById(R.id.tv_dialog_prompt_name_time);
            this.activity = activity;
        }

        public AnnouncementDialog create(){
            cancelIv.setOnClickListener(new View.OnClickListener() {
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

            promptDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(cancelClickListener!=null){
                        cancelClickListener.cancelClick();
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

        public interface OnCancelClickListener{
            void cancelClick();
        }

        public PromptBuilder setOnCancelClickListener(OnCancelClickListener cancelClickListener){
            this.cancelClickListener = cancelClickListener;
            return this;
        }

        public PromptBuilder setOnConfirmClickListener(OnConfirmClickListener confirmClickListener){
            this.confirmClickListener = confirmClickListener;
            return this;
        }

        public PromptBuilder setPromptNameTime(String content){
            nameTimeTv.setText(content);
            return this;
        }

        public PromptBuilder setPromptTitle(String title){
            titleTv.setText(title);
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

        public PromptBuilder setCancelableOnTouchOutside(boolean state){
            this.isCancelableOnTouchOutside = state;
            return this;
        }
    }


}
