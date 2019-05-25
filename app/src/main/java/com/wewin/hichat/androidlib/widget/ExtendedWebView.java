package com.wewin.hichat.androidlib.widget;

/*
 *   author:jason
 *   date:2019/4/2615:04
 *   主要解决viewPager嵌套webView横向滚动问题
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wewin.hichat.androidlib.utils.AndroidBug5497Workaround;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.NetworkUtil;

public class ExtendedWebView extends WebView {

    private boolean isScrollX = false;
    private Context mContext;
    private OnWebViewListeren mOnWebViewListeren;
    public boolean isError = false;

    public ExtendedWebView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ExtendedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }
        //用于H5工程师区分移动端和PC端
        getSettings().setUserAgentString(getSettings().getUserAgentString() + "zhibo18.app");
        getSettings().setTextZoom(100);
        AndroidBug5497Workaround.assistActivity((Activity) mContext);
        //设置WebView支持JavaScript
        getSettings().setJavaScriptEnabled(true);
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // 把图片加载放在最后来加载渲染
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 支持多窗口
        getSettings().setSupportMultipleWindows(true);
        // 开启 DOM storage API 功能
        getSettings().setDomStorageEnabled(true);
        // 开启 Application Caches 功能
        getSettings().setAppCacheEnabled(true);
        getSettings().setBlockNetworkImage(false);


        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.i("网页链接：" + url);
                isError = false;
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mOnWebViewListeren != null) {
                    mOnWebViewListeren.onPageFinished(url);
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (request.isForMainFrame() && mOnWebViewListeren != null) {
                    isError = true;
                    mOnWebViewListeren.onError();
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return;
                }
                if (mOnWebViewListeren != null) {
                    isError = true;
                    mOnWebViewListeren.onError();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.isForMainFrame() && mOnWebViewListeren != null) {// 在这里加上个判断
                        isError = true;
                        mOnWebViewListeren.onError();
                    }
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mOnWebViewListeren != null) {
                    mOnWebViewListeren.onPageStarted();
                }
            }


        });
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (mOnWebViewListeren != null) {
                    mOnWebViewListeren.onProgressChanged(progress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (view == null) {
                    return;
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("502 Bad Gateway") || title.contains("异常捕获")) {
                        //报错
                        if (mOnWebViewListeren != null) {
                            mOnWebViewListeren.onError();
                        }
                    }
                }
                if (mOnWebViewListeren != null) {
                    mOnWebViewListeren.onReceivedTitle(title);
                }
            }

        });
    }


    /**
     * 写入数据,个人信息
     */
    public void writeData() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            evaluateJavascript("window.localStorage.setItem('" + BaseInfoConstants.TOKEN + "','" + SignOutUtil.getToken() + "');", null);
//            evaluateJavascript("window.localStorage.setItem('" + BaseInfoConstants.UID + "','" + SignOutUtil.getUserId() + "');", null);
//        } else {
//            loadUrl("javascript:localStorage.setItem('" + BaseInfoConstants.TOKEN + "','" + SignOutUtil.getToken() + "');");
//            loadUrl("javascript:localStorage.setItem('" + BaseInfoConstants.UID + "','" + SignOutUtil.getUserId() + "');");
//        }
    }

    /**
     * 重新加载
     */
    public void reloadLoad(){
        isError=false;
        reload();
    }

    public void back(){
        goBack();
        isError = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEventCompat.getPointerCount(event) == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isScrollX = false;
                    //事件由webview处理
                    getParent().getParent()
                            .requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //嵌套Viewpager时
                    getParent().getParent()
                            .requestDisallowInterceptTouchEvent(!isScrollX);
                    break;
                default:
                    getParent().getParent()
                            .requestDisallowInterceptTouchEvent(false);
            }
        } else {
            //使webview可以双指缩放（前提是webview必须开启缩放功能，并且加载的网页也支持缩放）
            getParent().getParent().
                    requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }

    //当webview滚动到边界时执行
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        isScrollX = clampedX;
    }


    public void setOnWebViewListeren(OnWebViewListeren onWebViewListeren) {
        mOnWebViewListeren = onWebViewListeren;
    }



    interface OnWebViewListeren {
        void onPageFinished(String url);

        void onError();

        void onPageStarted();

        void onProgressChanged(int progress);

        void onReceivedTitle(String title);
    }
}
