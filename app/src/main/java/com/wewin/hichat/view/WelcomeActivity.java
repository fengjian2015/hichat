package com.wewin.hichat.view;

import android.content.Intent;
import android.os.Handler;

import com.wewin.hichat.MainActivity;
import com.wewin.hichat.R;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.view.login.LoginMainActivity;

/**
 * Created by Darren on 2018/12/13.
 */
public class WelcomeActivity extends BaseActivity {

    private Handler handler = new Handler();
    private Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            if (SpCons.getLoginState(getApplicationContext())){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }else {
                startActivity(new Intent(getApplicationContext(), LoginMainActivity.class));
            }
            WelcomeActivity.this.finish();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initViews() {
        setCenterTitle(0);
    }

    @Override
    protected void initViewsData() {
        handler.postDelayed(delayRunnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacks(delayRunnable);
            delayRunnable = null;
            handler = null;
        }
    }


}
