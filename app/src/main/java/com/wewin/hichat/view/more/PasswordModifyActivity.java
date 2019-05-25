package com.wewin.hichat.view.more;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.PwdUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.http.HttpLogin;

/**
 * 修改密码
 * Created by Darren on 2018/12/27.
 */
public class PasswordModifyActivity extends BaseActivity {

    private EditText oldPwdEt, firstNewEt, secondNewEt;
    private Button confirmBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_modify_password;
    }

    @Override
    protected void initViews() {
        oldPwdEt = findViewById(R.id.et_more_modify_pwd_old);
        firstNewEt = findViewById(R.id.et_more_modify_pwd_new_first);
        secondNewEt = findViewById(R.id.et_more_modify_pwd_new_second);
        confirmBtn = findViewById(R.id.btn_setting_modify_pwd_confirm);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.modify_password);
        setLeftText(R.string.back);
    }

    @Override
    protected void setListener() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = oldPwdEt.getText().toString().trim();
                String firstNew = firstNewEt.getText().toString().trim();
                String secondNew = secondNewEt.getText().toString().trim();
                if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(firstNew) || TextUtils.isEmpty(secondNew)) {
                    ToastUtil.showShort(getApplicationContext(), R.string.pwd_input_please);

                } else if (!firstNew.equals(secondNew)) {
                    ToastUtil.showShort(getApplicationContext(), R.string.please_confirm_password_same);

                } else if (oldPwd.equals(firstNew)) {
                    ToastUtil.showShort(getAppContext(), R.string.new_pwd_same_as_old);

                } else {
                    modifyPassword(firstNew, oldPwd);
                }
            }
        });

        oldPwdEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkPwdInput();
            }
        });

        firstNewEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkPwdInput();
            }
        });

        secondNewEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkPwdInput();
            }
        });
    }

    private void checkPwdInput() {
        String oldPwd = oldPwdEt.getText().toString().trim();
        String firstNew = firstNewEt.getText().toString().trim();
        String secondNew = secondNewEt.getText().toString().trim();
        if (oldPwd.length() > 5 && firstNew.length() > 5 && secondNew.length() > 5 && firstNew.equals(secondNew)
                && PwdUtil.isLetterAndDigit(firstNew)) {
            confirmBtn.setEnabled(true);

        } else {
            confirmBtn.setEnabled(false);
        }
    }

    private void modifyPassword(String newPwd, String oldPwd) {
        HttpLogin.modifyPassword(newPwd, oldPwd, SpCons.getUser(getAppContext()).getPhone(), 0,
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getApplicationContext(), R.string.modify_success);
                        EventTrans.post(EventMsg.LOGIN_FINISH);
                        PasswordModifyActivity.this.finish();
                    }
                });
    }

}
