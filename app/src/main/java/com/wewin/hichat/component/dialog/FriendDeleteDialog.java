package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.wewin.hichat.R;

/**
 * Created by Darren on 2019/3/1
 */
public class FriendDeleteDialog extends Dialog {

    private FriendDeleteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class FriendDeleteBuilder{

        private OnDialogConfirmClickListener confirmClickListener;
        private final FriendDeleteDialog dialog;
        private final TextView confirmTv, cancelTv;

        public FriendDeleteBuilder(Activity activity){
            dialog = new FriendDeleteDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_contact_friend_info_delete, null);
            confirmTv = dialogView.findViewById(R.id.tv_dialog_contact_friend_info_confirm);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_contact_friend_info_cancel);
            dialog.setContentView(dialogView);
        }

        public FriendDeleteDialog create(){
            confirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (confirmClickListener != null){
                        confirmClickListener.confirmClick();
                    }
                    if (dialog != null){
                        dialog.dismiss();
                    }
                }
            });

            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null){
                        dialog.dismiss();
                    }
                }
            });

            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            if (dialog.getWindow() != null){
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }

            return dialog;
        }

        public interface OnDialogConfirmClickListener{
            void confirmClick();
        }

        public FriendDeleteBuilder setOnConfirmClickListener(OnDialogConfirmClickListener confirmClickListener){
            this.confirmClickListener = confirmClickListener;
            return this;
        }
    }

}


