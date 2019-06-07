package com.wewin.hichat.component.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.umeng.analytics.MobclickAgent;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.MyLifecycleHandler;
import com.wewin.hichat.androidlib.utils.StatusBarUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.component.dialog.CallSmallDialog;
import com.wewin.hichat.component.manager.VoiceCallManager;

import java.lang.reflect.Field;

/**
 * Created by Darren on 2018/12/13.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements View.OnClickListener, EventTrans.OnEventTransListener {

    protected BaseActivity baseInstance;
    private RelativeLayout baseTitleLayoutRl;
    private ImageView baseBackIv, baseRightIv, baseCancelIv;
    private TextView baseCenterTv, baseRightTv, baseDividerTv, baseLeftTextTv;
    private LinearLayout baseLeftContainerLl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getWindow().setBackgroundDrawable(null);
        baseInstance = this;
        init();

        getIntentData();
        initViews();
        initViewsData();
        setListener();
    }

    protected abstract int getLayoutId();

    protected void getIntentData() {}

    protected abstract void initViews();

    protected void initViewsData() {}

    protected void setListener() {}

    protected void onWindowFocus() {}

    @Override
    public void onClick(View v) {}

    @Override
    public void onEventTrans(EventMsg msg) {}

    protected Context getAppContext() {
        return getApplicationContext();
    }

    protected Activity getHostActivity() {
        return this;
    }

    protected void onRightImgClick() {}

    protected void onRightTvClick() {}

    /**
     * 初始化状态栏
     */
    protected void initStatus() {
        StatusBarUtil.transparencyBar(this);
    }

    /**
     * 设置titleLayout
     */
    protected void setCenterTitle(int resourceId) {
        if (resourceId > 0) {
            setCenterTitle(getString(resourceId));
        } else {
            setCenterTitle("");
        }
    }

    protected void setCenterTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            baseCenterTv.setText(title);
            baseTitleLayoutRl.setVisibility(View.VISIBLE);
            baseLeftContainerLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtil.hideKeyboard(getHostActivity());
                    BaseActivity.this.finish();
                }
            });
        } else {
            baseTitleLayoutRl.setVisibility(View.GONE);
        }
    }

    protected void setLeftCancelVisible() {
        baseCancelIv.setVisibility(View.VISIBLE);
        baseLeftTextTv.setVisibility(View.VISIBLE);
        baseLeftTextTv.setText(getString(R.string.cancel));
        baseLeftTextTv.setTextColor(getResources().getColor(R.color.black_00));
        baseBackIv.setVisibility(View.GONE);
        baseLeftContainerLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏软键盘
                SystemUtil.hideKeyboard(getHostActivity());
                BaseActivity.this.finish();
            }
        });
    }

    protected void setLeftText(int resourceId) {
        setLeftText(getString(resourceId));
    }

    protected void setLeftText(String leftStr) {
        baseBackIv.setImageResource(R.drawable.return_btn_big);
        baseLeftTextTv.setVisibility(View.VISIBLE);
        baseLeftTextTv.setText(leftStr);
        baseLeftContainerLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseActivity.this.finish();
            }
        });
    }

    protected void setDividerInvisible() {
        baseDividerTv.setVisibility(View.INVISIBLE);
    }

    protected void setRightImg(int resourceId) {
        baseRightIv.setVisibility(View.VISIBLE);
        baseRightIv.setImageResource(resourceId);
        baseRightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightImgClick();
            }
        });
    }

    protected void setRightTv(int stringId) {
        if (stringId > 0) {
            baseRightTv.setVisibility(View.VISIBLE);
            baseRightTv.setText(getString(stringId));
            baseRightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightTvClick();
                }
            });
        } else {
            baseRightTv.setVisibility(View.GONE);
        }
    }

    protected RelativeLayout getBaseTitleLayoutRl(){
        return baseTitleLayoutRl;
    }

    /**
     * 初始化
     */
    private void init() {
        View view = View.inflate(this, getLayoutId(), null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        ViewFlipper containerView = findViewById(R.id.vf_base_layout_container);
        containerView.addView(view, lp);
        baseTitleLayoutRl = findViewById(R.id.rl_base_title_layout);
        baseBackIv = findViewById(R.id.iv_base_left_back);
        baseCenterTv = findViewById(R.id.tv_base_center_title);
        baseRightIv = findViewById(R.id.iv_base_right_img);
        baseRightTv = findViewById(R.id.tv_base_right_text);
        baseDividerTv = findViewById(R.id.tv_base_title_divider);
        baseLeftTextTv = findViewById(R.id.tv_base_left_text);
        baseLeftContainerLl = findViewById(R.id.ll_base_left_back_container);
        baseCancelIv = findViewById(R.id.iv_base_left_cancel);

        EventTrans.getInstance().register(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击空白位置 隐藏软键盘
        SystemUtil.hideKeyboard(getHostActivity());
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            onWindowFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        CallSmallDialog.getInstance().alertWindow();
        VoiceCallManager.get().startBackstageCallActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //隐藏软键盘
        SystemUtil.hideKeyboard(getHostActivity());
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fixInputMethod(this);
        EventTrans.getInstance().unRegister(this);
    }

    /**
     * 解决输入法导致的内存泄漏
     */
    public static void fixInputMethod(Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = null;
        try {
            inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (inputMethodManager == null) {
            return;
        }
        Field[] declaredFields = inputMethodManager.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                Object obj = declaredField.get(inputMethodManager);
                if (!(obj instanceof View)) {
                    continue;
                }
                View view = (View) obj;
                if (view.getContext() == context) {
                    declaredField.set(inputMethodManager, null);
                } else {
                    return;
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

}
