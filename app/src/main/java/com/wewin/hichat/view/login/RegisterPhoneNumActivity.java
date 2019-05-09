package com.wewin.hichat.view.login;

import android.content.Intent;
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
import com.wewin.hichat.androidlib.utils.StrUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.entity.CountryCode;
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
    private CountryCode countryCode;
    private int openType;
    private String phoneNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_register_phone_num;
    }

    @Override
    protected void getIntentData() {
        countryCode = (CountryCode) getIntent().getSerializableExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
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
        if (countryCode == null) {
            countryCode = new CountryCode(getString(R.string.philippines), "+63");
        }
        countryTv.setText(countryCode.getCountry());
        codeTv.setText(countryCode.getCode());
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
                if (s.toString().length() > 0) {
                    verifyBtn.setEnabled(true);
                } else {
                    verifyBtn.setEnabled(false);
                }
            }
        });
        if (!TextUtils.isEmpty(phoneNum)){
            phoneNumEt.setText(phoneNum);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_verify_num:
                String phoneNum = phoneNumEt.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    ToastUtil.showShort(getAppContext(), getString(R.string.phone_num_cannot_null));
                } else if (!StrUtil.isPhoneNumRight(countryCode.getCode(), phoneNum)) {
                    ToastUtil.showShort(getAppContext(), countryCode.getCountry()
                            + getString(R.string.phone_num_format_error));
                } else if (openType == LoginCons.TYPE_REGISTER) {
                    getVerifyCode(phoneNum, "register");
                } else {
                    getVerifyCode(phoneNum, "retrieve");
                }
                break;

            case R.id.tv_login_register_country:
                Intent intent = new Intent(getAppContext(), CountrySearchActivity.class);
                intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryCode);
                startActivityForResult(intent, LoginCons.INTENT_REQ_COUNTRY_SEARCH);
                break;
        }
    }

    private void getVerifyCode(final String phoneNum, String type) {
        HttpLogin.getSms(phoneNum, type, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                Intent intent = new Intent(getAppContext(), RegisterVerifyCodeActivity.class);
                intent.putExtra(LoginCons.EXTRA_LOGIN_AREA_CODE, codeTv.getText().toString());
                intent.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                intent.putExtra(LoginCons.EXTRA_LOGIN_VERIFY_CODE, data.toString());
                intent.putExtra(LoginCons.EXTRA_LOGIN_REGISTER_OPEN_TYPE, openType);
                intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryCode);
                startActivity(intent);
                SpCons.setCountryName(getAppContext(), countryCode.getCountry());
                SpCons.setCountryCode(getAppContext(), countryCode.getCode());
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
                if (sortModel != null) {
                    countryTv.setText(sortModel.getName());
                    codeTv.setText(sortModel.getCode());
                    countryCode.setCountry(sortModel.getName());
                    countryCode.setCode(sortModel.getCode());
                }
            }
        }
    }


}
