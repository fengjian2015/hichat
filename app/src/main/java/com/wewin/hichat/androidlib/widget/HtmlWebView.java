package com.wewin.hichat.androidlib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author jsaon
 * @date 2019/2/28
 * H5适配控件
 */
public class HtmlWebView extends LinearLayout{
    ProgressBar mProgressBar;
    ExtendedWebView mWebView;

    private Context mContext;

    private View view;
    private String html5Url;

    //错误界面
//    private ErrorView mErrorView;

    //回调接口
    private OnHtmlListener mOnHtmlListener;
    //错误回调
    private OnErrorListener mOnErrorListener;
    //滚动回调
    private OnWbScrollChanged onWbScrollChanged;

    public HtmlWebView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public HtmlWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_html, this);
        mProgressBar = view.findViewById(R.id.progressBar);
        mWebView = view.findViewById(R.id.web_view);
//        mErrorView = new ErrorView(mContext, view);
//        mErrorView.setTvError(mContext.getString(R.string.error));
//        mErrorView.setOnContinueListener(this);

    }

    public void setHtml5Url(String url) {
        mProgressBar.setMax(100);
        html5Url = url;
        if (html5Url.startsWith("file:///android_asset/")) {

        } else if (!checkLinkedExe(html5Url)) {
            html5Url = "http://" + html5Url;
        }
        html5Url="file:///android_asset/android_js.html";
        initWebView();
    }

    private void initWebView() {
        mWebView.initWebView();
        addJs();
        mWebView.loadUrl(html5Url);
        mWebView.setOnWebViewListeren(new ExtendedWebView.OnWebViewListeren() {
            @Override
            public void onPageFinished(String url) {
                if (mProgressBar == null) {
                    return;
                }
                if (mWebView.isError) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    hintError();
                    mProgressBar.setVisibility(View.GONE);
                    if (mWebView.canGoBack()) {
                        if (mOnHtmlListener != null) {
                            mOnHtmlListener.goBack();
                        }
                    }
                }
                mWebView.writeData();
            }

            @Override
            public void onError() {
                if (mWebView == null) {
                    return;
                }
                showError();
            }

            @Override
            public void onPageStarted() {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(int progress) {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setMax(100);
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onReceivedTitle(String title) {
                if (mOnHtmlListener != null) {
                    mOnHtmlListener.setTitle(title);
                }
            }
        });
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


    public ExtendedWebView getWebView() {
        return mWebView;
    }

//    @Override
//    public void again() {
//        mWebView.reloadLoad();
//    }

    /**
     * 获取是否回退
     *
     * @return
     */
    public boolean getCanGoBack() {
        return mWebView.canGoBack();
    }

    /**
     * 回退
     */
    public void goBack() {
        mWebView.back();
        hintError();
    }

    /**
     * 显示错误提示
     */
    public void showError() {
        mWebView.setVisibility(View.GONE);
        if (mOnErrorListener != null) {
            mOnErrorListener.onShow();
        } else {
//            mErrorView.errorShow(true);
        }
    }

    /**
     * 隐藏错误提示
     */
    public void hintError() {
        mWebView.setVisibility(View.VISIBLE);
        if (mOnErrorListener != null) {
            mOnErrorListener.onHint();
        } else {
//            mErrorView.hint();
        }

    }


    /**
     * 设置回调
     *
     * @param onHtmlListener
     */
    public void setOnHtmlListener(OnHtmlListener onHtmlListener) {
        mOnHtmlListener = onHtmlListener;
    }

    public void setOnWbScrollChanged(OnWbScrollChanged onWbScrollChanged) {
        this.onWbScrollChanged = onWbScrollChanged;
    }

    public interface OnWbScrollChanged {
        void onScrollChanged(float starY, float scrollY);
    }

    /**
     * 设置错误回调,设置后将不使用本控件的错误界面
     *
     * @param mOnErrorListener
     */
    public void setmOnErrorListener(OnErrorListener mOnErrorListener) {
        this.mOnErrorListener = mOnErrorListener;
    }

    /**
     * 回调
     */
    public interface OnHtmlListener {
        void goBack();

        void setTitle(String title);

        void setShareView(boolean isShow);
    }

    /**
     * 错误回调，使用情况：不想使用本控件自带的错误提示界面时使用
     */
    public interface OnErrorListener {
        //显示错误提示
        void onShow();

        //隐藏错误提示
        void onHint();
    }

    @Override
    public void destroyDrawingCache() {
        try {
            if (mWebView != null) {
                mWebView.clearCache(true);
                mWebView.onPause();
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.destroyDrawingCache();
    }

    private final int GET_USER_INFO = 1;//获取用户信息
    private final int GO_VIDEO_PLAY = 2;//跳转到播放器
    private final int GO_VIDEO_PLAY_LOGIN = 3;//跳转到播放器需登录
    private final int SHOW_DIALOG = 5;//弹窗提示
    private final int SHOW_DIALOG_RELOAD = 6;//弹窗提示,重新刷新
    private final int IS_LONG = 7;//判断是否登录
    private final int IS_SHOW_SHARE = 8;//是否显示分享按钮
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case GET_USER_INFO:
                    getUserInfo();
                    break;
                case GO_VIDEO_PLAY:
                    goVideoPlay((String) msg.obj, false);
                    break;
                case GO_VIDEO_PLAY_LOGIN:
                    goVideoPlay((String) msg.obj, true);
                    break;
                case SHOW_DIALOG:
                case SHOW_DIALOG_RELOAD:
//
                    break;
                case IS_LONG:
//
                    break;
                case IS_SHOW_SHARE:
                    if (mOnHtmlListener == null) {
                        break;
                    }
                    int isShow = (int) msg.obj;
                    if (1 == isShow) {
                        mOnHtmlListener.setShareView(true);
                    } else {
                        mOnHtmlListener.setShareView(false);
                    }
                    break;

                default:

                    break;
            }
            return false;
        }
    });

    /**
     * * 视频播放
     *
     * @param content
     * @param isNeesLogin true需要登录
     */
    private void goVideoPlay(String content, boolean isNeesLogin) {
        ToastUtil.showShort(mContext,"goVideoPlay："+content);
//        try {
//            Map map = JSONObject.parseObject(content, Map.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(BaseInfoConstants.PULL_URL, map.get(BaseInfoConstants.PULL_URL) + "");
//            bundle.putString(BaseInfoConstants.URL, map.get(BaseInfoConstants.HTML_URL) + "");
//            bundle.putString(BaseInfoConstants.WECHAT, map.get(BaseInfoConstants.WECHAT) + "");
//            bundle.putString(BaseInfoConstants.ADSCONTENT, map.get(BaseInfoConstants.ADSCONTENT) + "");
//            bundle.putString(BaseInfoConstants.WXIMAGE, map.get(BaseInfoConstants.WXIMAGE) + "");
//            bundle.putString(BaseInfoConstants.TITLE, map.get(BaseInfoConstants.TITLE) + "");
//            if (isNeesLogin) {
//                IntentStart.starLogin(mContext, VideoDetailsActivity.class, bundle);
//            } else {
//                IntentStart.star(mContext, VideoDetailsActivity.class, bundle);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /*-----------------------------------以下是java调用js-------------------------------------------------*/

    /**
     * 获取用户信息回调h5
     */
    private void getUserInfo() {
        ToastUtil.showShort(mContext,"getUserInfo：");
//        UserInfo userInfo = UserInfoDao.queryUserInfo(SignOutUtil.getUserId());
//        if (userInfo == null) {
//            userInfo = new UserInfo();
//        }
//        if (mWebView == null) return;
        mWebView.loadUrl("javascript:appGetUserInfo('" + "调用H5接口" + " ');");
    }

    /*-----------------------------------以下是java调用js,并且返回值-------------------------------------------------*/
    /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mWebView.evaluateJavascript("javascript:appGetUserInfo('" + userInfo.getJson() + " ')", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                LogUtil.Log("返回值"+value);
            }
        });
    }*/

    /*------------------------------------以下是h5调用java方法--------------------------------------------*/
    @SuppressLint("JavascriptInterface")
    private void addJs() {
        mWebView.addJavascriptInterface(this, "app");
    }

    /**
     * 获取用户信息
     */
    @JavascriptInterface
    public void jsGetUserInfo() {
        ToastUtil.showShort(mContext,"jsGetUserInfo：");
        handler.sendEmptyMessage(GET_USER_INFO);
    }

    /**
     * 跳转到新的activity进行展示H5内容
     */
    @JavascriptInterface
    public void jsNewActivity(String url) {
        ToastUtil.showShort(mContext,"jsNewActivity："+url);
//        Bundle bundle = new Bundle();
//        bundle.putString(BaseInfoConstants.URL, url);
//        IntentStart.star(mContext, HtmlActivity.class, bundle);

    }

    /**
     * 跳转播放器
     */
    @JavascriptInterface
    public void jsPlay(String content) {
        ToastUtil.showShort(mContext,"jsPlay："+content);
        Message message = new Message();
        message.what = GO_VIDEO_PLAY;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 跳转播放器需要登录才可以进入.
     */
    @JavascriptInterface
    public void jsPlayOrLogin(String content) {
        ToastUtil.showShort(mContext,"jsPlayOrLogin："+content);
        Message message = new Message();
        message.what = GO_VIDEO_PLAY_LOGIN;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 跳转到登录界面
     */
    @JavascriptInterface
    public void jsLogin() {
        ToastUtil.showShort(mContext,"jsLogin：");
//        IntentStart.starLogin(mContext);
    }

    /**
     * 判断是否登录，登录状态刷新界面，否则跳转登录
     */
    @JavascriptInterface
    public void jsIsLogin() {
        ToastUtil.showShort(mContext,"jsIsLogin：");
        handler.sendEmptyMessage(IS_LONG);
    }

    /**
     * 退出登录
     */
    @JavascriptInterface
    public void jsSignOut() {
        ToastUtil.showShort(mContext,"jsSignOut：");
//        SignOutUtil.signOut();
    }

    /**
     * 弹窗,不刷新
     */
    @JavascriptInterface
    public void jsShowDialog(String content) {
        ToastUtil.showShort(mContext,"jsShowDialog："+content);
        Message message = new Message();
        message.what = SHOW_DIALOG;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 弹窗
     */
    @JavascriptInterface
    public void jsShowDialog(String content, String isReload) {
        ToastUtil.showShort(mContext,"jsShowDialog："+content);
        Message message = new Message();
        message.what = SHOW_DIALOG_RELOAD;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 获取页面滚动
     */
    @JavascriptInterface
    public void jsScrollChanged(String starY, String scrollY) {
        ToastUtil.showShort(mContext,"jsScrollChanged："+starY+"  "+scrollY);
//        onWbScrollChanged.onScrollChanged(UtilTool.parseFloat(starY), UtilTool.parseFloat(scrollY));
    }


    /**
     * 获取动画，下载和展示动画
     */
    @JavascriptInterface
    public void jsAnimation(String url, String fileName) {
        ToastUtil.showShort(mContext,"jsAnimation："+url+"  "+fileName);
//        MessageEvent messageEvent = new MessageEvent(START_ANIMATION);
//        messageEvent.setUrl(url);
//        messageEvent.setFileName(fileName);
//        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 获取动画，下载和展示动画GIF
     */
    @JavascriptInterface
    public void jsAnimationGif(String url, String fileName) {
        ToastUtil.showShort(mContext,"jsAnimationGif："+url+"  "+fileName);
//        MessageEvent messageEvent = new MessageEvent(START_GIF);
//        messageEvent.setUrl(url);
//        messageEvent.setFileName(fileName);
//        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 是否显示分享按钮
     */
    @JavascriptInterface
    public void jsIsShowShare(int isShow, String shareData) {
        ToastUtil.showShort(mContext,"jsIsShowShare："+isShow+"  "+shareData);
//        if (!StringUtils.isEmpty(shareData)) {
//            MessageEvent messageEvent = new MessageEvent(SHARE_DATA);
//            Map map = JSONObject.parseObject(shareData, Map.class);
//            messageEvent.setMap(map);
//            EventBus.getDefault().post(messageEvent);
//            Message message = new Message();
//            message.what = IS_SHOW_SHARE;
//            message.obj = isShow;
//            handler.sendMessage(message);
//        }
    }

}
