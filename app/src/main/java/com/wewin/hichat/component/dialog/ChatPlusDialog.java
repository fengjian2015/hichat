package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.wewin.hichat.R;

/**
 * 聊天页附加选项dialog
 * Created by Darren on 2018/12/27.
 */
public class ChatPlusDialog extends Dialog {

    private ChatPlusDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder{

        private ChatPlusDialog plusDialog;
        private View dialogView;
        private TextView albumTv, cameraTv, fileSendTv, cancelTv;
        private OnSelectClickListener selectClickListener;

        public Builder(Activity activity){
            plusDialog = new ChatPlusDialog(activity, R.style.dialog_common);
            dialogView = View.inflate(activity, R.layout.dialog_message_chat_plus, null);
            plusDialog.setContentView(dialogView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            albumTv = dialogView.findViewById(R.id.tv_dialog_chat_plus_from_album);
            cameraTv = dialogView.findViewById(R.id.tv_dialog_chat_plus_camera);
            fileSendTv = dialogView.findViewById(R.id.tv_dialog_chat_plus_send_file);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_setting_cancel);
        }

        public ChatPlusDialog create(){
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (plusDialog != null){
                        plusDialog.dismiss();
                    }
                }
            });

            albumTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectClickListener != null){
                        selectClickListener.albumClick();
                    }
                    if (plusDialog != null){
                        plusDialog.dismiss();
                    }
                }
            });

            cameraTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectClickListener != null){
                        selectClickListener.cameraClick();
                    }
                    if (plusDialog != null){
                        plusDialog.dismiss();
                    }
                }
            });

            fileSendTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectClickListener != null){
                        selectClickListener.fileSend();
                    }
                    if (plusDialog != null){
                        plusDialog.dismiss();
                    }
                }
            });

            plusDialog.setCancelable(true);
            plusDialog.setCanceledOnTouchOutside(true);
            Window window = plusDialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
            }

            return plusDialog;
        }

        public interface OnSelectClickListener{
            void albumClick();

            void cameraClick();

            void fileSend();
        }

        public Builder setOnSelectClickListener(OnSelectClickListener selectClickListener){
            this.selectClickListener = selectClickListener;
            return this;
        }

    }

}
