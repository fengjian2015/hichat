package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.component.service.DownApkService;

/**
 * @author jason
 * 版本提示框dialog
 * Created by Darren on 2019/1/19
 */
public class VersionDialog extends Dialog {

    private VersionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class VersionBuilder {

        private VersionDialog versionDialog;
        private TextView contentTv, cancelTv, confirmTv, dividerTv;
        private Activity activity;
        private OnConfirmClickListener confirmClickListener;
        private OnCancelClickListener cancelClickListener;
        private boolean isCancelableOnTouchOutside = true;
        private int downloadState=-1;

        public VersionBuilder(Activity activity) {
            versionDialog = new VersionDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_version, null);
            versionDialog.addContentView(dialogView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            contentTv = dialogView.findViewById(R.id.tv_dialog_version_content);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_version_cancel);
            confirmTv = dialogView.findViewById(R.id.tv_dialog_version_confirm);
            dividerTv = dialogView.findViewById(R.id.tv_dialog_version_bot_divider);
            this.activity = activity;
        }

        public VersionDialog create() {
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (versionDialog != null) {
                        versionDialog.dismiss();
                    }
                }
            });

            confirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (confirmClickListener != null) {
                        confirmClickListener.confirmClick();
                    }

                }
            });

            versionDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (cancelClickListener != null) {
                        cancelClickListener.cancelClick();
                    }
                }
            });

            versionDialog.setCancelable(true);
            versionDialog.setCanceledOnTouchOutside(isCancelableOnTouchOutside);

            return versionDialog;
        }

        public interface OnConfirmClickListener {
            void confirmClick();
        }

        public interface OnCancelClickListener {
            void cancelClick();
        }

        public VersionBuilder setOnCancelClickListener(OnCancelClickListener cancelClickListener) {
            this.cancelClickListener = cancelClickListener;
            return this;
        }

        public VersionBuilder setOnConfirmClickListener(OnConfirmClickListener confirmClickListener) {
            this.confirmClickListener = confirmClickListener;
            return this;
        }

        public VersionBuilder setPromptContent(String content) {
            contentTv.setText(content);
            return this;
        }

        public VersionBuilder setPromptContent(int resourceId) {
            contentTv.setText(activity.getString(resourceId));
            return this;
        }

        public VersionBuilder setCancelVisible(boolean isVisible) {
            if (isVisible) {
                cancelTv.setVisibility(View.VISIBLE);
                dividerTv.setVisibility(View.VISIBLE);
            } else {
                cancelTv.setVisibility(View.GONE);
                dividerTv.setVisibility(View.GONE);
            }
            return this;
        }

        public VersionBuilder setDownState(int state) {
            downloadState=state;
            confirmTv.setEnabled(false);
            if (state == DownApkService.ON_FAIL) {
                confirmTv.setEnabled(true);
                confirmTv.setText(activity.getString(R.string.click_again_download));
            } else if((state == -1)){
                confirmTv.setEnabled(true);
                confirmTv.setText(activity.getString(R.string.update_immediately));
            }else if(state==DownApkService.ON_COMPLETE){
                confirmTv.setEnabled(true);
                confirmTv.setText(activity.getString(R.string.click_install));
            }else {
                confirmTv.setText(activity.getString(R.string.download_now));
            }
            return this;
        }

        public VersionBuilder setCancelableOnTouchOutside(boolean state) {
            this.isCancelableOnTouchOutside = state;
            return this;
        }

        public VersionBuilder dismiss() {
            if (ActivityUtil.isActivityOnTop(activity)&&versionDialog!=null) {
                versionDialog.dismiss();
            }
            return this;
        }

        public boolean isShow() {
            if (versionDialog==null){
                return false;
            }
            return versionDialog.isShowing();
        }

        public int getDownloadState(){
            return downloadState;
        }
    }


}
