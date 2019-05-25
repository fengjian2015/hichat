package com.wewin.hichat.view;


import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.androidlib.widget.HtmlWebView;
import com.wewin.hichat.component.base.BaseActivity;

public class TestActivity extends BaseActivity {

    private ImageView iv_base_left_back;
    private ImageView iv_base_left_cancel;
    private HtmlWebView htmlWebview;
    private TextView tv_center_title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initViews() {
        iv_base_left_back = findViewById(R.id.iv_base_left_back);
        iv_base_left_cancel = findViewById(R.id.iv_base_left_cancel);
        htmlWebview = findViewById(R.id.html_webview);
        tv_center_title = findViewById(R.id.tv_center_title);
        htmlWebview.setHtml5Url("");
    }

    @Override
    protected void setListener() {
        iv_base_left_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //可返回可关闭
                if (htmlWebview.getCanGoBack()) {
                    htmlWebview.goBack();
                } else {
                    finish();
                }
            }
        });
        iv_base_left_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        htmlWebview.setOnHtmlListener(new HtmlWebView.OnHtmlListener() {
            @Override
            public void goBack() {
                if (htmlWebview.getCanGoBack()) {
                    iv_base_left_cancel.setVisibility(View.VISIBLE);
                } else {
                    iv_base_left_cancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void setTitle(String title) {
                if (TextUtils.isEmpty(title)) {
                    return;
                }
                tv_center_title.setText(title);
            }

            @Override
            public void setShareView(boolean isShow) {

            }
        });
    }
}
