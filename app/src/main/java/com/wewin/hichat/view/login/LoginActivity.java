package com.wewin.hichat.view.login;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import com.wewin.hichat.androidlib.utils.StrUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.CountryCode;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.db.entity.SortModel;
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
    private CountryCode countryCode;
    private String phoneNum;
    private String password;
    private boolean isCookieInvalid;//cookie是否失效
    private boolean isCookieInvalidDialog;//是否弹出cookie失效弹框
    private boolean isDialogShowed = false;//cookie失效提示框是否显示过；
    private String cookieInvalidInfo;//cookie失效提示

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void getIntentData() {
        phoneNum = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM);
        password = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PASSWORD);
        countryCode = (CountryCode) getIntent().getSerializableExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
        isCookieInvalid = getIntent().getBooleanExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, false);
        isCookieInvalidDialog = getIntent().getBooleanExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, false);
        cookieInvalidInfo = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_INFO);
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
        if (countryCode == null) {
            countryCode = new CountryCode(getString(R.string.philippines), "+63");
        }
        countryTv.setText(countryCode.getCountry());
        areaCodeTv.setText(countryCode.getCode());

        if (isCookieInvalid) {
            logout();
        }

//        loginBtn.setFocusable(true);
//        loginBtn.setFocusableInTouchMode(true);
//        loginBtn.requestFocus();
//        getLocation();
    }

    @Override
    protected void onWindowFocus() {
        super.onWindowFocus();
        if (isCookieInvalidDialog && !isDialogShowed) {
            showPromptDialog(cookieInvalidInfo);
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
                if (!TextUtils.isEmpty(phoneNum) && StrUtil.isPhoneNumRight(countryCode.getCode(), phoneNum)
                        && pwd.length() > 5) {
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
                Intent intent = new Intent(getAppContext(), CountrySearchActivity.class);
                startActivityForResult(intent, INTENT_REQ_COUNTRY_SEARCH);
                break;

            case R.id.tv_login_find_back_pwd:
                Intent intent1 = new Intent(getAppContext(), RegisterPhoneNumActivity.class);
                intent1.putExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, LoginCons.TYPE_PASSWORD_FIND);
                intent1.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryCode);
                String phoneNum = phoneNumEt.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNum)) {
                    intent1.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                }
                startActivity(intent1);
                break;

            case R.id.btn_login:
                clickLogin();
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

    private void clickLogin() {
        String phoneNum = phoneNumEt.getText().toString().trim();
        String pwd = pwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), R.string.phone_num_input_please);
        } else if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showShort(getApplicationContext(), R.string.pwd_input_please);
        } else if (!StrUtil.isPhoneNumRight(countryCode.getCode(), phoneNum)) {
            ToastUtil.showShort(getApplicationContext(), countryCode.getCountry()
                    + getString(R.string.phone_num_format_error));
        } else {
            login(phoneNum, pwd);
        }
    }

    private void login(String phone, String password) {
        HttpLogin.login(phone, password, "acc",
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            LoginUser user = JSON.parseObject(data.toString(), LoginUser.class);
                            UserDao.setUser(user);
                            startActivity(new Intent(getAppContext(), MainActivity.class));
                            EventTrans.post(EventMsg.LOGIN_FINISH);
                            SpCons.setLoginState(getAppContext(), true);
                            LogUtil.i("login", data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void logout() {
        HttpLogin.logout(new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                SpCons.setCuid(getAppContext(), "");
                SpCons.setDomain(getAppContext(), "");
                SpCons.setLoginState(getAppContext(), false);
//                ChatSocket.getInstance().stop();
            }
        });
    }

    private void getLocation() {
        final StringBuffer sb = new StringBuffer();
        Rigger.on(this).permissions(Permission.ACCESS_FINE_LOCATION)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        if (LocationUtil.isGpsEnabled(getAppContext())) {
                            LocationUtil.register(getAppContext(), 0, 0,
                                    new LocationUtil.OnLocationChangeListener() {
                                        @Override
                                        public void getLastKnownLocation(Location location) {
                                            double latitude = location.getLatitude();
                                            double longitude = location.getLongitude();
                                            LogUtil.i("latitude", latitude);
                                            LogUtil.i("longitude", longitude);
                                            sb.append("latitude:" + latitude).append(" longitude:" + longitude);
                                            promptTv.setText(sb.toString());

                                            String countryName = LocationUtil.getCountryName(getApplicationContext(),
                                                    location.getLatitude(), location.getLongitude());
                                            LogUtil.i("countryName", countryName);
                                            ToastUtil.showShort(getApplicationContext(), "countryName" + countryName);
                                            sb.append("latitude:" + latitude).append(" longitude:" + longitude)
                                                    .append(" countryName: " + countryName);
                                            promptTv.setText(sb.toString());
                                        }

                                        @Override
                                        public void onLocationChanged(Location location) {

                                        }

                                        @Override
                                        public void onStatusChanged(String provider, int status, Bundle extras) {

                                        }
                                    });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showShort(getAppContext(), R.string.not_open_gps_location_prompt);
                                }
                            });
                        }
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

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
                    countryCode.setCountry(sortModel.getName());
                    countryCode.setCode(sortModel.getCode());
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
}
