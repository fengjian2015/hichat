package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wewin.hichat.R;

/**
 * Created by Darren on 2019/2/28
 */
public class OnlineDialog extends Dialog {

    private OnlineDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class OnlineBuilder implements View.OnClickListener {

        private OnlineDialog onlineDialog;
        private RelativeLayout onlineRl, hideRl;
        private TextView onlineTv, hideTv, cancelTv;
        private ImageView onlineHookIv, hideHookIv;
        private Activity activity;
        private OnOnlineItemClickListener itemClickListener;

        public OnlineBuilder(Activity activity){
            this.activity = activity;
            onlineDialog = new OnlineDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_more_personal_online, null);
            onlineDialog.setContentView(dialogView);
            onlineRl = dialogView.findViewById(R.id.rl_dialog_online_container);
            hideRl = dialogView.findViewById(R.id.rl_dialog_online_hide_container);
            onlineTv = dialogView.findViewById(R.id.tv_dialog_online);
            hideTv = dialogView.findViewById(R.id.tv_dialog_online_hide);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_online_cancel);
            onlineHookIv = dialogView.findViewById(R.id.iv_dialog_online_hook);
            hideHookIv = dialogView.findViewById(R.id.iv_dialog_online_hide_hook);
        }

        public OnlineDialog create(){
            onlineRl.setOnClickListener(this);
            hideRl.setOnClickListener(this);
            cancelTv.setOnClickListener(this);

            onlineDialog.setCancelable(true);
            onlineDialog.setCanceledOnTouchOutside(true);
            if (onlineDialog.getWindow() != null){
                onlineDialog.getWindow().setGravity(Gravity.BOTTOM);
            }

            return onlineDialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_dialog_online_container:
                    onlineTv.setTextColor(activity.getResources().getColor(R.color.blue_main));
                    onlineHookIv.setVisibility(View.VISIBLE);
                    hideTv.setTextColor(activity.getResources().getColor(R.color.black_00));
                    hideHookIv.setVisibility(View.INVISIBLE);
                    if (itemClickListener != null){
                        itemClickListener.itemClick(0);
                    }
                    break;

                case R.id.rl_dialog_online_hide_container:
                    onlineTv.setTextColor(activity.getResources().getColor(R.color.black_00));
                    onlineHookIv.setVisibility(View.INVISIBLE);
                    hideTv.setTextColor(activity.getResources().getColor(R.color.blue_main));
                    hideHookIv.setVisibility(View.VISIBLE);
                    if (itemClickListener != null){
                        itemClickListener.itemClick(1);
                    }
                    break;

                case R.id.tv_dialog_online_cancel:
                    if (onlineDialog != null){
                        onlineDialog.dismiss();
                    }
                    break;
            }
            if (onlineDialog != null){
                onlineDialog.dismiss();
            }
        }

        public interface OnOnlineItemClickListener{
            void itemClick(int position);
        }

        public OnlineBuilder setOnItemClickListener(OnOnlineItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
            return this;
        }


    }

}
