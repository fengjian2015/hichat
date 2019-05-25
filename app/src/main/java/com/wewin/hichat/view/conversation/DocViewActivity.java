package com.wewin.hichat.view.conversation;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.NetworkUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.androidlib.utils.FileUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文档查看页面
 * Created by Darren on 2019/1/14.
 */
public class DocViewActivity extends BaseActivity {

    private WebView webView;
    private LinearLayout containerLl;
    private ProgressBar mProgressBar;
    private WebSettings webSettings;
    private String webUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_chat_doc_view;
    }

    @Override
    protected void initViews() {
        containerLl = findViewById(R.id.ll_message_chat_doc_view_container);
        mProgressBar=findViewById(R.id.progressBar);
    }

    @Override
    protected void getIntentData() {
        webUrl = getIntent().getStringExtra(ContactCons.EXTRA_MESSAGE_CHAT_WEB_PATH);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.title);
        setHtml5Url();
    }

    public void setHtml5Url() {
        mProgressBar.setMax(100);
        if(webUrl.startsWith("file:///android_asset/")){

        }else if (!checkLinkedExe(webUrl)) {
            webUrl = "http://" + webUrl;
        }
        initWebView();
    }

    private void initWebView() {
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView = new WebView(getAppContext());
        webView.setLayoutParams(params);
        containerLl.addView(webView);
        webView.setWebViewClient(new CustomWebViewClient());

        webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        webSettings.setJavaScriptEnabled(true);
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        if (NetworkUtil.isNetworkAvailable(getAppContext())) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }

        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能

        webSettings.setAppCachePath(FileUtil.getSDCachePath(getAppContext())); //设置  Application Caches 缓存目录
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setMax(100);
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (view == null) {
                    return;
                }
                setCenterTitle(title);
            }

        });
        webView.loadUrl( webUrl);
        LogUtil.i("webView url", webUrl);
    }

    private static class CustomWebViewClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webSettings != null){
            webSettings.setJavaScriptEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (webSettings != null){
            webSettings.setJavaScriptEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }


    // 判断是否为网址
    public static boolean checkLinkedExe(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            return false;
        }
        resultString = resultString.toLowerCase();
        String pattern = "^(http://|https://|ftp://|mms://|rtsp://){1}.*";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(resultString);
        return m.matches();
    }

}
