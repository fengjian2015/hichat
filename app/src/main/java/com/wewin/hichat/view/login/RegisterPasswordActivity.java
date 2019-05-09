package com.wewin.hichat.view.login;

import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.androidlib.utils.PwdUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.model.db.entity.CountryCode;
import com.wewin.hichat.model.http.HttpLogin;

/**
 * 注册-输入密码
 * Created by Darren on 2019/2/7
 */
public class RegisterPasswordActivity extends BaseActivity {

    private EditText firstPwdEt, secondPwdEt;
    private Button nextStepBtn;
    private String phoneNum;
    private String code;
    private int openType;
    private CountryCode countryCode;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_register_password;
    }

    @Override
    protected void initViews() {
        firstPwdEt = findViewById(R.id.et_login_register_password_first);
        secondPwdEt = findViewById(R.id.et_login_register_password_second);
        nextStepBtn = findViewById(R.id.btn_login_register_password_next_step);
    }

    @Override
    protected void getIntentData() {
        phoneNum = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM);
        code = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_AREA_CODE);
        openType = getIntent().getIntExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, 0);
        countryCode = (CountryCode) getIntent().getSerializableExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
        LogUtil.i("countryCode", countryCode);
    }

    @Override
    protected void initViewsData() {
        if (openType == LoginCons.TYPE_REGISTER) {
            setCenterTitle(R.string.register);
        } else {
            setCenterTitle(R.string.password_find_back);
        }
    }

    @Override
    protected void setListener() {
        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openType == LoginCons.TYPE_REGISTER) {
                    Intent intent = new Intent(getApplicationContext(), RegisterPersonalInfoActivity.class);
                    intent.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                    intent.putExtra(LoginCons.EXTRA_LOGIN_AREA_CODE, code);
                    intent.putExtra(LoginCons.EXTRA_LOGIN_PASSWORD, firstPwdEt.getText().toString().trim());
                    intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryCode);
                    startActivity(intent);
                } else {
                    retrievePassword(firstPwdEt.getText().toString().trim());
                }
            }
        });

        firstPwdEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String firstPwd = s.toString();
                String secondPwd = secondPwdEt.getText().toString().trim();
                if (firstPwd.length() > 5 && secondPwd.length() > 5 && firstPwd.equals(secondPwd)
                        && PwdUtil.isLetterAndDigit(firstPwd)) {
                    nextStepBtn.setEnabled(true);
                } else {
                    nextStepBtn.setEnabled(false);
                }
            }
        });

        secondPwdEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String firstPwd = firstPwdEt.getText().toString().trim();
                String secondPwd = s.toString();
                if (firstPwd.length() > 5 && secondPwd.length() > 5 && firstPwd.equals(secondPwd)
                        && PwdUtil.isLetterAndDigit(firstPwd)) {
                    nextStepBtn.setEnabled(true);
                } else {
                    nextStepBtn.setEnabled(false);
                }
            }
        });
    }

    private void retrievePassword(final String newPwd) {
        HttpLogin.retrievePassword(newPwd, phoneNum,
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getApplicationContext(), R.string.modify_success);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                        intent.putExtra(LoginCons.EXTRA_LOGIN_AREA_CODE, code);
                        intent.putExtra(LoginCons.EXTRA_LOGIN_PASSWORD, newPwd);
                        intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryCode);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.LOGIN_FINISH) {
            this.finish();
        }
    }
}
