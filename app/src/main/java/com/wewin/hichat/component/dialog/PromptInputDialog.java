package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ToastUtil;

/**
 * Created by Darren on 2019/3/8
 */
public class PromptInputDialog extends Dialog {


    private PromptInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class PromptInputBuilder {

        private final PromptInputDialog dialog;
        private final TextView titleTv, cancelTv, confirmTv;
        private final EditText inputEt;
        private String title;
        private String inputStr;
        private OnConfirmListener confirmListener;
        private Activity activity;
        private int maxInputLength;

        public PromptInputBuilder(Activity activity) {
            this.activity = activity;
            dialog = new PromptInputDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_contact_friend_input_prompt, null);
            dialog.setContentView(dialogView);
            titleTv = dialogView.findViewById(R.id.tv_dialog_prompt_input_title);
            inputEt = dialogView.findViewById(R.id.et_dialog_prompt_input);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_prompt_input_cancel);
            confirmTv = dialogView.findViewById(R.id.tv_dialog_prompt_input_confirm);
        }

        public PromptInputDialog create() {
            titleTv.setText(title);
            inputEt.setText(inputStr);
            inputEt.setSelection(inputStr.length());
            inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxInputLength)}); //最大输入长度
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            confirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputStr = inputEt.getText().toString().trim();
                    if (TextUtils.isEmpty(inputStr)) {
                        ToastUtil.showShort(activity, "输入内容不能为空");
                    } else if (confirmListener != null && inputStr.length() > 0) {
                        confirmListener.confirm(inputStr);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }
            });

            if (dialog != null) {
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
            }

            return dialog;
        }

        public PromptInputBuilder setTitle(int resourceId) {
            this.title = activity.getString(resourceId);
            return this;
        }

        public PromptInputBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public PromptInputBuilder setInputStr(String inputStr) {
            this.inputStr = inputStr;
            return this;
        }

        public PromptInputBuilder setMaxInputLength(int maxInputLength) {
            this.maxInputLength = maxInputLength;
            return this;
        }

        public interface OnConfirmListener {
            void confirm(String inputStr);
        }

        public PromptInputBuilder setOnConfirmListener(OnConfirmListener confirmListener) {
            this.confirmListener = confirmListener;
            return this;
        }

    }

}
