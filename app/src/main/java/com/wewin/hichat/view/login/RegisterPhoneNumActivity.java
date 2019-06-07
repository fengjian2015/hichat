package com.wewin.hichat.view.login;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.PhoneUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.entity.CountryInfo;
import com.wewin.hichat.model.db.entity.SortModel;
import com.wewin.hichat.model.http.HttpLogin;
import com.wewin.hichat.view.search.CountrySearchActivity;

/**
 * 注册-手机号输入
 * Created by Darren on 2018/12/14.
 */
public class RegisterPhoneNumActivity extends BaseActivity {

    private Button verifyBtn;
    private TextView countryTv, codeTv;
    private EditText phoneNumEt;
    private CountryInfo countryInfo;
    private int openType;
    private String phoneNum;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (phoneNumEt == null){
                return;
            }
            phoneNumEt.requestFocus();
            SystemUtil.showKeyboard(getHostActivity(), phoneNumEt);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_register_phone_num;
    }

    @Override
    protected void getIntentData() {
        countryInfo = (CountryInfo) getIntent().getSerializableExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
        openType = getIntent().getIntExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, 0);
        phoneNum = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM);
    }

    @Override
    protected void initViews() {
        setCenterTitle(R.string.register);
        setDividerInvisible();
        verifyBtn = findViewById(R.id.btn_register_verify_num);
        countryTv = findViewById(R.id.tv_login_register_country);
        codeTv = findViewById(R.id.tv_login_register_code);
        phoneNumEt = findViewById(R.id.et_login_register_phone_num);
    }

    @Override
    protected void initViewsData() {
        if (openType == LoginCons.TYPE_REGISTER) {
            setCenterTitle(R.string.register);
        } else {
            setCenterTitle(R.string.password_find_back);
        }
        if (countryInfo == null) {
            countryInfo = new CountryInfo(getString(R.string.philippines), "+63");
        }
        countryTv.setText(countryInfo.getCountry());
        codeTv.setText(countryInfo.getCode());

        handler.postDelayed(runnable, 300);
    }

    @Override
    protected void setListener() {
        verifyBtn.setOnClickListener(this);
        countryTv.setOnClickListener(this);

        phoneNumEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    return true;
                }
                return false;
            }
        });

        phoneNumEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                String code = codeTv.getText().toString().trim();
                String phoneNum = phoneNumEt.getText().toString().trim();
                if (PhoneUtil.isPhoneNumValid(code, phoneNum)) {
                    verifyBtn.setEnabled(true);
                } else {
                    verifyBtn.setEnabled(false);
                }
            }
        });
        if (!TextUtils.isEmpty(phoneNum)) {
            phoneNumEt.setText(phoneNum);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_verify_num:
                String areaCode = codeTv.getText().toString();
                String phoneNum = phoneNumEt.getText().toString().trim();
                boolean changePhone=((("+63".equals(areaCode))
                        ||("+855".equals(areaCode))
                        ||("+60".equals(areaCode))
                        ||("+886".equals(areaCode)))
                        && phoneNum.startsWith("0"));

                if (TextUtils.isEmpty(phoneNum)) {
                    ToastUtil.showShort(getAppContext(), getString(R.string.phone_num_cannot_null));
                } else if (!PhoneUtil.isPhoneNumValid(countryInfo.getCode(), phoneNum)) {
                    ToastUtil.showShort(getAppContext(), countryInfo.getCountry()
                            + getString(R.string.phone_num_format_error));
                } else if (openType == LoginCons.TYPE_REGISTER) {
                    if (changePhone){
                        phoneNum = phoneNum.substring(1, phoneNum.length());
                    }
                    getVerifyCode(areaCode, phoneNum, "register");
                } else {
                    if (changePhone){
                        phoneNum = phoneNum.substring(1, phoneNum.length());
                    }
                    getVerifyCode(areaCode, phoneNum, "retrieve");
                }
                break;

            case R.id.tv_login_register_country:
                SystemUtil.hideKeyboard(getHostActivity());
                Intent intent = new Intent(getAppContext(), CountrySearchActivity.class);
                intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryInfo);
                startActivityForResult(intent, LoginCons.INTENT_REQ_COUNTRY_SEARCH);
                break;

            default:
                break;
        }
    }

    private void getVerifyCode(final String areaCode, final String phoneNum, String type) {
        LogUtil.i("phoneNum", phoneNum);
        HttpLogin.getSms(areaCode.replace("+", ""), phoneNum, type, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                Intent intent = new Intent(getAppContext(), RegisterVerifyCodeActivity.class);
                intent.putExtra(LoginCons.EXTRA_LOGIN_AREA_CODE, areaCode);
                intent.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                intent.putExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, openType);
                intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryInfo);
                startActivity(intent);
                SpCons.setCountryName(getAppContext(), countryInfo.getCountry());
                SpCons.setCountryCode(getAppContext(), countryInfo.getCode());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LoginCons.INTENT_REQ_COUNTRY_SEARCH) {
                SortModel sortModel = (SortModel) data.getSerializableExtra(
                        LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
                if (sortModel == null){
                    return;
                }
                countryTv.setText(sortModel.getName());
                codeTv.setText(sortModel.getCode());
                countryInfo.setCountry(sortModel.getName());
                countryInfo.setCode(sortModel.getCode());
                String phoneNum = phoneNumEt.getText().toString().trim();
                if (PhoneUtil.isPhoneNumValid(sortModel.getCode(), phoneNum)) {
                    verifyBtn.setEnabled(true);
                } else {
                    verifyBtn.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null){
            handler.removeCallbacks(runnable);
            handler = null;
        }
        super.onDestroy();
    }

}
