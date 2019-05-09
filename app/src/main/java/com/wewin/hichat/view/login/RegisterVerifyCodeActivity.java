package com.wewin.hichat.view.login;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.adapter.LoginRegisterVerifyCodeRcvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.model.db.entity.CountryCode;
import com.wewin.hichat.model.http.HttpLogin;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册-验证码输入
 * Created by Darren on 2019/2/6
 */
public class RegisterVerifyCodeActivity extends BaseActivity {

    private RecyclerView containerRcv;
    private TextView phoneNumTv, resendTv, focusTv;
    private RelativeLayout resendParentRl;
    private List<String> numList = new ArrayList<>();
    private EditText codeBgEt;
    private LoginRegisterVerifyCodeRcvAdapter rcvAdapter;
    private String phoneNum;
    private String code;
    private final int MAX_SECOND = 30;
    private int leftSecond = MAX_SECOND;
    private Handler handler = new Handler();
    private Runnable runnable;
    private Button nextStepBtn;
    private String verifyCode;
    private int openType;
    private CountryCode countryCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_register_verify_code;
    }

    @Override
    protected void initViews() {
        containerRcv = findViewById(R.id.rcv_login_register_verify_code_input);
        phoneNumTv = findViewById(R.id.tv_login_register_phone_num);
        resendTv = findViewById(R.id.tv_login_register_resend_verify_code);
        resendParentRl = findViewById(R.id.rl_login_register_resend_verify_code_parent);
        codeBgEt = findViewById(R.id.et_login_register_verify_code_bg);
        focusTv = findViewById(R.id.tv_login_register_verify_code_focus);
        nextStepBtn = findViewById(R.id.btn_login_register_next_step);
    }

    @Override
    protected void getIntentData() {
        phoneNum = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM);
        code = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_AREA_CODE);
        verifyCode = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_VERIFY_CODE);
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
        initRecyclerView();
        codeBgEt.requestFocus();
        SystemUtil.showKeyboard(this, codeBgEt);

        if (!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(code)) {
            phoneNumTv.setText(code + " " + phoneNum);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                if (leftSecond > 0) {
                    resendTv.setText(leftSecond + "s");
                    leftSecond--;
                    handler.postDelayed(this, 1000);
                } else {
                    resendTv.setText(getString(R.string.resend_verify_code));
                    leftSecond = MAX_SECOND;
                }
            }
        };
    }

    @Override
    protected void setListener() {
        focusTv.setOnClickListener(this);
        resendParentRl.setOnClickListener(this);
        nextStepBtn.setOnClickListener(this);

        codeBgEt.addTextChangedListener(new CustomTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                splitNumStr(s.toString());
                if (s.toString().length() == 6) {
                    nextStepBtn.setEnabled(true);
                } else {
                    nextStepBtn.setEnabled(false);
                }
            }
        });

        codeBgEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    return true;
                }
                return false;
            }
        });

        codeBgEt.setText(verifyCode);
        ToastUtil.showShort(getApplicationContext(), verifyCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_register_verify_code_focus:
                codeBgEt.requestFocus();
                SystemUtil.showKeyboard(RegisterVerifyCodeActivity.this, codeBgEt);
                break;

            case R.id.rl_login_register_resend_verify_code_parent:
                if (openType == LoginCons.TYPE_REGISTER) {
                    getVerifyCode(phoneNum, "register");
                } else {
                    getVerifyCode(phoneNum, "retrieve");
                }
                handler.post(runnable);
                break;

            case R.id.btn_login_register_next_step:
                String verifyCode = codeBgEt.getText().toString().trim();
                checkVerifyCode(verifyCode);
                break;
        }
    }

    private void initRecyclerView() {
        rcvAdapter = new LoginRegisterVerifyCodeRcvAdapter(getApplicationContext(), numList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        containerRcv.setAdapter(rcvAdapter);
        containerRcv.setLayoutManager(layoutManager);
    }

    private void splitNumStr(String numStr) {
        numList.clear();
        for (int i = 0; i < numStr.length(); i++) {
            numList.add(String.valueOf(numStr.charAt(i)));
        }
        rcvAdapter.notifyDataSetChanged();
    }

    private void getVerifyCode(String phoneNum, String type) {
        HttpLogin.getSms(phoneNum, type, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data != null) {
                    ToastUtil.showLong(getApplicationContext(), data.toString());
                    codeBgEt.setText(data.toString());
                }
            }
        });
    }

    private void checkVerifyCode(String verifyCode) {
        HttpLogin.checkVerifyCode(verifyCode, phoneNum, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                Intent intent = new Intent(getAppContext(), RegisterPasswordActivity.class);
                intent.putExtra(LoginCons.EXTRA_LOGIN_AREA_CODE, code);
                intent.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                intent.putExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, openType);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler = null;
    }
}
