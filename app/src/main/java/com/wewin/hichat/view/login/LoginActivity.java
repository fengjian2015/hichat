package com.wewin.hichat.view.login;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.MainActivity;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.utils.LocationUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.PhoneUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.CountryInfo;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.db.entity.SortModel;
import com.wewin.hichat.model.db.entity.UserRecordInfo;
import com.wewin.hichat.model.http.HttpLogin;
import com.wewin.hichat.view.search.CountrySearchActivity;

import java.util.HashMap;

/**
 * 手机号登录
 * Created by Darren on 2018/12/14.
 */
public class LoginActivity extends BaseActivity {

    private TextView countryTv, areaCodeTv, findBackTv;
    private EditText phoneNumEt, pwdEt;
    private Button loginBtn;
    private TextView promptTv;
    private final int INTENT_REQ_COUNTRY_SEARCH = 100;
    private CountryInfo countryInfo;
    private UserRecordInfo userRecordInfo;
    private String phoneNum;
    private String password;
    private boolean isCookieInvalid;//cookie是否失效
    private boolean isCookieInvalidDialog;//是否弹出cookie失效弹框
    private boolean isDialogShowed = false;//cookie失效提示框是否显示过；
    private String cookieInvalidPrompt;//cookie失效提示
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (phoneNumEt == null) {
                return;
            }
            phoneNumEt.requestFocus();
            SystemUtil.showKeyboard(getHostActivity(), phoneNumEt);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void getIntentData() {
        phoneNum = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM);
        password = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PASSWORD);
        countryInfo = (CountryInfo) getIntent().getSerializableExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
        isCookieInvalid = getIntent().getBooleanExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, false);
        isCookieInvalidDialog = getIntent().getBooleanExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, false);
        cookieInvalidPrompt = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_INFO);
        if (TextUtils.isEmpty(phoneNum)){
            String user=SpCons.getString(this,SpCons.USER_RECORD);
            if (!TextUtils.isEmpty(user)){
                userRecordInfo=JSON.parseObject(user,UserRecordInfo.class);
                phoneNum=userRecordInfo.getPhoneNum();
                countryInfo=new CountryInfo();
                countryInfo.setCode(userRecordInfo.getCode());
                countryInfo.setCountry(userRecordInfo.getCountry());
            }
        }
        if (userRecordInfo==null){
            userRecordInfo=new UserRecordInfo();
        }
    }

    @Override
    protected void initViews() {
        countryTv = findViewById(R.id.tv_login_country);
        areaCodeTv = findViewById(R.id.tv_login_area_code);
        phoneNumEt = findViewById(R.id.et_login_phone_num);
        pwdEt = findViewById(R.id.et_login_pwd);
        loginBtn = findViewById(R.id.btn_login);
        findBackTv = findViewById(R.id.tv_login_find_back_pwd);
        promptTv = findViewById(R.id.tv_login_prompt);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.login_by_phone_num);
        if (countryInfo == null) {
            countryInfo = new CountryInfo(getString(R.string.philippines), "+63");
        }
        countryTv.setText(countryInfo.getCountry());
        areaCodeTv.setText(countryInfo.getCode());

        if (isCookieInvalid) {
            logout();
        }

        handler.postDelayed(runnable, 300);

    }

    @Override
    protected void onWindowFocus() {
        super.onWindowFocus();
        if (isCookieInvalidDialog && !isDialogShowed) {
            showPromptDialog(cookieInvalidPrompt);
        }
    }

    @Override
    protected void setListener() {
        countryTv.setOnClickListener(this);
        findBackTv.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        phoneNumEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String phoneNum = phoneNumEt.getText().toString().trim();
                String pwd = pwdEt.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNum)
                        && PhoneUtil.isPhoneNumValid(countryInfo.getCode(), phoneNum)
                        && pwd.length() > 5) {
                    loginBtn.setEnabled(true);
                } else {
                    loginBtn.setEnabled(false);
                }
            }
        });

        pwdEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String phoneNum = phoneNumEt.getText().toString().trim();
                String pwd = pwdEt.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNum) && pwd.length() > 5) {
                    loginBtn.setEnabled(true);
                } else {
                    loginBtn.setEnabled(false);
                }
            }
        });

        phoneNumEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    pwdEt.requestFocus();
                    return true;
                }
                return false;
            }
        });

        pwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clickLogin();
                    return true;
                }
                return false;
            }
        });

        if (!TextUtils.isEmpty(phoneNum)) {
            phoneNumEt.setText(phoneNum);
        }
        if (!TextUtils.isEmpty(password)) {
            pwdEt.setText(password);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_country:
                SystemUtil.hideKeyboard(getHostActivity());
                Intent intent = new Intent(getAppContext(), CountrySearchActivity.class);
                startActivityForResult(intent, INTENT_REQ_COUNTRY_SEARCH);
                break;

            case R.id.tv_login_find_back_pwd:
                Intent intent1 = new Intent(getAppContext(), RegisterPhoneNumActivity.class);
                intent1.putExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, LoginCons.TYPE_PASSWORD_FIND);
                intent1.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryInfo);
                String phoneNum = phoneNumEt.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNum)) {
                    intent1.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                }
                startActivity(intent1);
                break;

            case R.id.btn_login:
                clickLogin();
                break;

            default:
                break;
        }
    }

    private void showPromptDialog(String promptContent) {
        if (TextUtils.isEmpty(promptContent)) {
            promptContent = getString(R.string.cookie_invalid_prompt);
        }
        PromptDialog.PromptBuilder builder = new PromptDialog.PromptBuilder(this);
        PromptDialog promptDialog = builder.setCancelVisible(false)
                .setPromptContent(promptContent)
                .setCancelableOnTouchOutside(false)
                .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        isDialogShowed = true;
                    }
                })
                .create();
        promptDialog.show();
    }

    private boolean isLoginAvailable(String phoneNum, String pwd) {
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), R.string.phone_num_input_please);
            return false;
        } else if (!PhoneUtil.isPhoneNumValid(countryInfo.getCode(), phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), countryInfo.getCountry()
                    + getString(R.string.phone_num_format_error));
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showShort(getApplicationContext(), R.string.pwd_input_please);
            return false;
        } else if (pwd.length() < 6) {
            ToastUtil.showShort(getAppContext(), R.string.pwd_length_at_least_6);
            return false;
        } else {
            return true;
        }
    }

    private void clickLogin() {
        SystemUtil.hideKeyboard(getHostActivity());
        String phoneNum = phoneNumEt.getText().toString().trim();
        String pwd = pwdEt.getText().toString().trim();
        userRecordInfo.setPhoneNum(phoneNum);
        userRecordInfo.setCode(countryInfo.getCode());
        userRecordInfo.setCountry(countryInfo.getCountry());
        SpCons.setString(getHostActivity(),SpCons.USER_RECORD,JSON.toJSONString(userRecordInfo));
        if (isLoginAvailable(phoneNum, pwd)) {
            boolean changePhone=((("+63".equals(countryInfo.getCode()))
                    ||("+855".equals(countryInfo.getCode()))
                    ||("+60".equals(countryInfo.getCode()))
                    ||("+886".equals(countryInfo.getCode())))
                    && phoneNum.startsWith("0"));
            if (changePhone) {
                phoneNum = phoneNum.substring(1, phoneNum.length());
            }
            login(phoneNum, pwd);
        }
    }

    private void login(String phone, String password) {
        HttpLogin.login(phone, password, "acc",
                new HttpCallBack(this, ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            LoginUser user = JSON.parseObject(data.toString(), LoginUser.class);
                            LogUtil.i("login", user);
                            UserDao.setUser(user);
                            EventTrans.post(EventMsg.LOGIN_FINISH);
                            SpCons.setUser(getAppContext(), user);
                            SpCons.setLoginState(getAppContext(), true);
                            startActivity(new Intent(getAppContext(), MainActivity.class));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void logout() {
        HttpLogin.logout(new HttpCallBack(this, ClassUtil.classMethodName(),true) {
            @Override
            public void success(Object data, int count) {
                SpCons.setCuid(getAppContext(), "");
                SpCons.setDomain(getAppContext(), "");
                SpCons.setLoginState(getAppContext(), false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_REQ_COUNTRY_SEARCH) {
                SortModel sortModel = (SortModel) data.getSerializableExtra(
                        LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
                if (sortModel != null) {
                    countryTv.setText(sortModel.getName());
                    areaCodeTv.setText(sortModel.getCode());
                    countryInfo.setCountry(sortModel.getName());
                    countryInfo.setCode(sortModel.getCode());
                }
            }
        }
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.LOGIN_FINISH) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
        super.onDestroy();
    }

}
