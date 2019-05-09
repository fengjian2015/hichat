package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wewin.hichat.R;

/**
 * 图片选择dialog
 * Created by Darren on 2018/12/27.
 */
public class PhotoDialog extends Dialog {

    private PhotoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class PhotoBuilder{
        private PhotoDialog photoDialog;
        private TextView albumTv, cameraTv, cancelTv;
        private OnSelectClickListener selectClickListener;

        public PhotoBuilder(Activity activity){
            photoDialog = new PhotoDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_setting_photo_select, null);
            photoDialog.setContentView(dialogView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            albumTv = dialogView.findViewById(R.id.tv_dialog_setting_from_album);
            cameraTv = dialogView.findViewById(R.id.tv_dialog_setting_camera);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_setting_cancel);
        }

        public PhotoDialog create(){
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (photoDialog != null){
                        photoDialog.dismiss();
                    }
                }
            });

            albumTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectClickListener != null){
                        selectClickListener.albumClick();
                    }
                    if (photoDialog != null){
                        photoDialog.dismiss();
                    }
                }
            });

            cameraTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectClickListener != null){
                        selectClickListener.cameraClick();
                    }
                    if (photoDialog != null){
                        photoDialog.dismiss();
                    }
                }
            });

            photoDialog.setCancelable(true);
            photoDialog.setCanceledOnTouchOutside(true);

            if (photoDialog.getWindow() != null) {
                photoDialog.getWindow().setGravity(Gravity.BOTTOM);
            }
            return photoDialog;
        }

        public interface OnSelectClickListener{
            void albumClick();

            void cameraClick();
        }

        public PhotoBuilder setOnSelectClickListener(OnSelectClickListener selectClickListener){
            this.selectClickListener = selectClickListener;
            return this;
        }
    }
}
