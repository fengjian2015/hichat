package com.wewin.hichat.view.more;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.LubanCallBack;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.LoadingDialog;
import com.wewin.hichat.component.dialog.PhotoDialog;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.http.HttpMore;
import com.wewin.hichat.view.album.AlbumChoiceActivity;
import com.wewin.hichat.androidlib.utils.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 编辑资料
 * Created by Darren on 2018/12/27.
 */
public class PersonalInfoModifyActivity extends BaseActivity {

    private PhotoDialog photoDialog;
    private ImageView avatarIv;
    private EditText signEt, nicknameEt;
    private TextView nicknameCountTv, signCountTv;
    private List<String> selectImgList = new ArrayList<>();
    private LoadingDialog loadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_edit_info;
    }

    @Override
    protected void initViews() {
        avatarIv = findViewById(R.id.civ_more_personal_info_avatar);
        signEt = findViewById(R.id.et_more_personal_info_sign);
        nicknameEt = findViewById(R.id.et_more_personal_info_nickname);
        nicknameCountTv = findViewById(R.id.tv_more_personal_info_nickname_count);
        signCountTv = findViewById(R.id.tv_more_personal_info_sign_count);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.edit_info);
        setLeftText(R.string.back);
        setRightTv(R.string.finish);

        ImgUtil.load(this, SpCons.getUser(getAppContext()).getAvatar(), avatarIv);
    }

    @Override
    protected void setListener() {
        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });

        nicknameEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                nicknameCountTv.setText(s.toString().length() + "/7");
            }
        });

        signEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                signCountTv.setText(s.toString().length() + "/50");
            }
        });

        signEt.setText(SpCons.getUser(getAppContext()).getSign());
        nicknameEt.setText(SpCons.getUser(getAppContext()).getUsername());

    }

    @Override
    protected void onRightTvClick() {
        String nicknameStr = nicknameEt.getText().toString().trim();
        String signStr = signEt.getText().toString().trim();
        if (TextUtils.isEmpty(nicknameStr)) {
            ToastUtil.showShort(getApplicationContext(), R.string.nickname_cannot_null);

        } else if (signStr.equals(SpCons.getUser(getAppContext()).getSign())
                && nicknameStr.equals(SpCons.getUser(getAppContext()).getUsername())) {
            this.finish();

        } else {
            modifyAccountInfo(nicknameStr, signStr);
        }

        if (selectImgList != null && !selectImgList.isEmpty()) {
            compressImg(selectImgList.get(selectImgList.size() - 1));
        }
    }

    private void showPhotoDialog() {
        if (photoDialog == null) {
            PhotoDialog.PhotoBuilder dialogBuilder = new PhotoDialog.PhotoBuilder(this);
            photoDialog = dialogBuilder.setOnSelectClickListener(new PhotoDialog.PhotoBuilder.OnSelectClickListener() {
                @Override
                public void albumClick() {
                    startActivityForResult(new Intent(getApplicationContext(), AlbumChoiceActivity.class),
                            ImgUtil.REQUEST_ALBUM);
                }

                @Override
                public void cameraClick() {
                    ImgUtil.openCamera(PersonalInfoModifyActivity.this);
                }
            }).create();
        }
        photoDialog.show();
    }

    private void showLoadingDialog(){
        if (loadingDialog == null){
            LoadingDialog.LoadingBuilder builder = new LoadingDialog.LoadingBuilder(this);
            loadingDialog = builder.create();
        }
        loadingDialog.show();
    }

    private void modifyAccountInfo(final String username, final String sign) {
        HttpMore.modifyAccountInfo(SpCons.getUser(getAppContext()).getAudioCues(),
                SpCons.getUser(getAppContext()).getGender(),
                SpCons.getUser(getAppContext()).getId(), sign, username,
                SpCons.getUser(getAppContext()).getVibratesCues(),
                new HttpCallBack(this, ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getApplicationContext(), R.string.modify_success);
                        LoginUser user = SpCons.getUser(getAppContext());
                        user.setSign(sign);
                        user.setUsername(username);
                        SpCons.setUser(getAppContext(), user);
                        UserDao.setUser(user);
                        LogUtil.i("saveAccountInfo", SpCons.getUser(getAppContext()));
                        EventTrans.post(EventMsg.MORE_PERSONAL_INFO_REFRESH);
                        getHostActivity().finish();
                    }
                });
    }

    private void compressImg(String filePath) {
        ImgUtil.compress(this, filePath, new LubanCallBack() {
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                LogUtil.i("compressImg", file.getAbsoluteFile());
                uploadPersonalAvatar(file);
            }
        });
    }

    private void uploadPersonalAvatar(File file) {
        HttpMore.uploadPersonalAvatar(file, SpCons.getUser(getAppContext()).getId(), "", "jpg",
                new HttpCallBack(this, ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            LoginUser loginUser = JSON.parseObject(data.toString(), LoginUser.class);
                            LogUtil.i("uploadPersonalAvatar", loginUser);
                            ToastUtil.showShort(getApplicationContext(), R.string.upload_success);
                            LoginUser user = SpCons.getUser(getAppContext());
                            user.setAvatar(loginUser.getAvatar().replace("\\", "/"));
                            SpCons.setUser(getAppContext(), user);
                            UserDao.setUser(user);
                            EventTrans.post(EventMsg.MORE_PERSONAL_AVATAR_REFRESH);
                            getHostActivity().finish();

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
                        ImgUtil.load(PersonalInfoModifyActivity.this, dataList.get(0), avatarIv);
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
                        ImgUtil.load(PersonalInfoModifyActivity.this, ImgUtil.cropOutputPath, avatarIv);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.deleteDir(FileUtil.getSDCachePath(getApplicationContext()));
    }

}
