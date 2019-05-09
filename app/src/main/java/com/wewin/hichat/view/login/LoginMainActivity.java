package com.wewin.hichat.view.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.wewin.hichat.R;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.component.constant.LoginCons;

/**
 * Created by Darren on 2018/12/14.
 */
public class LoginMainActivity extends BaseActivity {

    private Button loginBtn, registerBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_main;
    }

    @Override
    protected void initViews() {
        setCenterTitle(0);
        loginBtn = findViewById(R.id.btn_login_main_login);
        registerBtn = findViewById(R.id.btn_login_main_register);
    }

    @Override
    protected void setListener() {
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login_main_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;

            case R.id.btn_login_main_register:
                Intent intent = new Intent(getApplicationContext(), RegisterPhoneNumActivity.class);
                intent.putExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, LoginCons.TYPE_REGISTER);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.LOGIN_FINISH){
            this.finish();
        }
    }

}
