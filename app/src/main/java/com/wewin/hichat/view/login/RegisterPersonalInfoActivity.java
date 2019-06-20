package com.wewin.hichat.view.login;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.LubanCallBack;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PhotoDialog;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.CountryInfo;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.http.HttpLogin;
import com.wewin.hichat.model.http.HttpMore;
import com.wewin.hichat.view.album.AlbumChoiceActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册-个人信息
 * Created by Darren on 2019/2/7
 */
public class RegisterPersonalInfoActivity extends BaseActivity {

    private ImageView avatarIv, cameraIconIv;
    private EditText nicknameEt, signEt;
    private TextView nicknameTextCountTv, signTextCountTv;
    private RadioButton maleRb, femaleRb, secretRb;
    private Button finishBtn;
    private int sexType = 1;
    private PhotoDialog photoDialog;
    private List<String> selectImgList = new ArrayList<>();
    private String phoneNum;
    private String code;
    private String password;
    private CountryInfo countryInfo;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (nicknameEt == null) {
                return;
            }
            nicknameEt.requestFocus();
            SystemUtil.showKeyboard(getHostActivity(), nicknameEt);
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_register_personal_info;
    }

    @Override
    protected void initViews() {
        avatarIv = findViewById(R.id.civ_login_register_personal_info_avatar);
        nicknameEt = findViewById(R.id.et_login_register_personal_info_nickname);
        signEt = findViewById(R.id.et_login_register_personal_info_sign);
        nicknameTextCountTv = findViewById(R.id.tv_login_register_personal_info_nickname_text_count);
        signTextCountTv = findViewById(R.id.tv_login_register_personal_info_sign_text_count);
        maleRb = findViewById(R.id.rb_login_register_personal_info_male);
        femaleRb = findViewById(R.id.rb_login_register_personal_info_female);
        secretRb = findViewById(R.id.rb_login_register_personal_info_secret);
        finishBtn = findViewById(R.id.btn_login_register_personal_info_finish);
        cameraIconIv = findViewById(R.id.iv_login_register_personal_info_camera_icon);
    }

    @Override
    protected void getIntentData() {
        phoneNum = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM);
        code = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_AREA_CODE);
        password = getIntent().getStringExtra(LoginCons.EXTRA_LOGIN_PASSWORD);
        countryInfo = (CountryInfo) getIntent().getSerializableExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
        LogUtil.i("countryInfo", countryInfo);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.register);
        handler.postDelayed(runnable, 300);
    }

    @Override
    protected void setListener() {
        avatarIv.setOnClickListener(this);
        maleRb.setOnClickListener(this);
        femaleRb.setOnClickListener(this);
        secretRb.setOnClickListener(this);
        finishBtn.setOnClickListener(this);

        nicknameEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String nickname = nicknameEt.getText().toString().trim();
                nicknameTextCountTv.setText(nickname.length() + "/7");
                if (nickname.length() > 0) {
                    finishBtn.setEnabled(true);
                } else {
                    finishBtn.setEnabled(false);
                }
            }
        });

        signEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String sign = signEt.getText().toString().trim();
                signTextCountTv.setText(sign.length() + "/120");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_login_register_personal_info_avatar:
                showPhotoDialog();
                break;

            case R.id.rb_login_register_personal_info_male:
                sexType = 1;
                break;

            case R.id.rb_login_register_personal_info_female:
                sexType = 0;
                break;

            case R.id.rb_login_register_personal_info_secret:
                sexType = 2;
                break;

            case R.id.btn_login_register_personal_info_finish:
                register(nicknameEt.getText().toString().trim(), signEt.getText().toString().trim());
                break;

            default:
                break;
        }
    }

    private void showPhotoDialog() {
        SystemUtil.hideKeyboard(getHostActivity());
        if (photoDialog == null) {
            PhotoDialog.PhotoBuilder dialogBuilder = new PhotoDialog.PhotoBuilder(this);
            photoDialog = dialogBuilder.setOnSelectClickListener(new PhotoDialog.PhotoBuilder.OnSelectClickListener() {
                @Override
                public void albumClick() {
                    startActivityForResult(new Intent(getApplicationContext(),
                            AlbumChoiceActivity.class), ImgUtil.REQUEST_ALBUM);
                }

                @Override
                public void cameraClick() {
                    ImgUtil.openCamera(RegisterPersonalInfoActivity.this);
                }
            }).create();
        }
        photoDialog.show();
    }

    public void register(String nickname, String sign) {
        HttpLogin.register(code, sexType, nickname, password, phoneNum, sign,
                new HttpCallBack(this, ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            LoginUser user = JSON.parseObject(data.toString(), LoginUser.class);
                            if (selectImgList.size() > 0) {
                                compressImg(selectImgList.get(selectImgList.size() - 1), user.getId());
                            }
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.putExtra(LoginCons.EXTRA_LOGIN_PHONE_NUM, phoneNum);
                            intent.putExtra(LoginCons.EXTRA_LOGIN_PASSWORD, password);
                            intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, countryInfo);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void compressImg(String filePath, final String userId) {
        ImgUtil.compress(this, filePath, new LubanCallBack() {
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                uploadPersonalAvatar(file, userId);
            }
        });
    }

    private void uploadPersonalAvatar(File file, String userId) {
        HttpMore.uploadPersonalAvatar(file, userId, "pass", "jpg",
                new HttpCallBack(this, ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            ToastUtil.showShort(getAppContext(), R.string.upload_success);
                            LoginUser loginUser = JSON.parseObject(data.toString(), LoginUser.class);
                            SpCons.getUser(getAppContext()).setAvatar(loginUser.getAvatar().replace("\\", "/"));
                            UserDao.setUser(SpCons.getUser(getAppContext()));
                            EventTrans.post(EventMsg.MORE_PERSONAL_AVATAR_REFRESH);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImgUtil.REQUEST_ALBUM:
                    List<String> dataList = data.getStringArrayListExtra(ImgUtil.IMG_SELECT_PATH_LIST);
                    if (dataList != null && !dataList.isEmpty()) {
                        selectImgList.addAll(dataList);
                        ImgUtil.load(RegisterPersonalInfoActivity.this, dataList.get(0), avatarIv);
                        cameraIconIv.setVisibility(View.INVISIBLE);
                    }
                    break;

                case ImgUtil.REQUEST_CAMERA:
                    if (!TextUtils.isEmpty(ImgUtil.cameraOutputPath)) {
                        ImgUtil.cropPic(this, ImgUtil.cameraOutputPath, ImgUtil.IMG_CROP_SIZE_1);
                    }
                    break;

                case ImgUtil.REQUEST_CROP:
                    if (!TextUtils.isEmpty(ImgUtil.cropOutputPath)) {
                        selectImgList.add(ImgUtil.cropOutputPath);
                        ImgUtil.load(RegisterPersonalInfoActivity.this, ImgUtil.cropOutputPath, avatarIv);
                        cameraIconIv.setVisibility(View.INVISIBLE);
                    }
                    break;

                default:
                    break;
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
        FileUtil.deleteDir(FileUtil.getSDCachePath(getAppContext()));
    }

}
