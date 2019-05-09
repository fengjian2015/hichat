package com.wewin.hichat.view.more;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.CommonUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.TransUtil;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.dialog.SexDialog;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.http.HttpLogin;
import com.wewin.hichat.model.http.HttpMore;
import com.wewin.hichat.view.login.LoginActivity;

/**
 * 主页-通知
 * Created by Darren on 2018/12/13.
 */
public class MoreFragment extends BaseFragment {

    private RelativeLayout personalRl;
    private ImageView avatarIv, notifyRedPointIv;
    private TextView nameTv, signTv, phoneNumTv, genderTv, stateTv, logoutTv;
    private FrameLayout audioRemindFl, vibrateRemindFl, notifyFl, modifyPwdFl, loginRecordFl, aboutFl,
            genderFl, stateFl;
    private CheckBox audioRemindCb, vibrateRemindCb;
    private SexDialog sexDialog;
    private boolean isAudioRemindOpen = false;
    private boolean isVibrateRemindOpen = false;
    private int genderType;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void initViews() {
        personalRl = parentView.findViewById(R.id.rl_more_personal_container);
        avatarIv = parentView.findViewById(R.id.civ_more_personal_avatar);
        nameTv = parentView.findViewById(R.id.tv_more_personal_name);
        signTv = parentView.findViewById(R.id.tv_more_personal_sign);
        phoneNumTv = parentView.findViewById(R.id.tv_more_personal_phone_num);
        genderTv = parentView.findViewById(R.id.tv_more_personal_gender);
        stateTv = parentView.findViewById(R.id.tv_more_personal_state);
        logoutTv = parentView.findViewById(R.id.tv_more_personal_logout);
        audioRemindFl = parentView.findViewById(R.id.fl_more_personal_audio_remind);
        vibrateRemindFl = parentView.findViewById(R.id.fl_more_personal_vibrate_remind);
        notifyFl = parentView.findViewById(R.id.fl_more_personal_notify);
        modifyPwdFl = parentView.findViewById(R.id.fl_more_personal_modify_pwd);
        loginRecordFl = parentView.findViewById(R.id.fl_more_personal_login_record);
        aboutFl = parentView.findViewById(R.id.fl_more_personal_about);
        genderFl = parentView.findViewById(R.id.fl_more_personal_gender_container);
        stateFl = parentView.findViewById(R.id.fl_more_personal_state_container);
        audioRemindCb = parentView.findViewById(R.id.cb_more_personal_audio_remind);
        vibrateRemindCb = parentView.findViewById(R.id.cb_more_personal_vibrate_remind);
        notifyRedPointIv = parentView.findViewById(R.id.iv_more_new_notify_res_point);
    }

    @Override
    protected void initViewsData() {
        setViewData();
        if (SpCons.getNotifyRedPointVisible(getHostActivity())) {
            notifyRedPointIv.setVisibility(View.VISIBLE);
        } else {
            notifyRedPointIv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            genderType = UserDao.user.getGender();
            LogUtil.i("genderType", genderType);
            getPersonalInfo();
        }
    }

    @Override
    protected void setListener() {
        personalRl.setOnClickListener(this);
        genderFl.setOnClickListener(this);
        stateFl.setOnClickListener(this);
        audioRemindFl.setOnClickListener(this);
        vibrateRemindFl.setOnClickListener(this);
        notifyFl.setOnClickListener(this);
        modifyPwdFl.setOnClickListener(this);
        loginRecordFl.setOnClickListener(this);
        aboutFl.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        audioRemindCb.setOnClickListener(this);
        vibrateRemindCb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_more_personal_container:
                startActivity(new Intent(getActivity(), PersonalInfoModifyActivity.class));
                break;

            case R.id.fl_more_personal_gender_container:
                showSexDialog();
                break;

            case R.id.cb_more_personal_audio_remind:
            case R.id.fl_more_personal_audio_remind:
                isAudioRemindOpen = !isAudioRemindOpen;
                audioRemindCb.setChecked(isAudioRemindOpen);
                modifyAccountInfo(TransUtil.booleanToInt(isAudioRemindOpen), genderType,
                        TransUtil.booleanToInt(isVibrateRemindOpen));
                break;

            case R.id.cb_more_personal_vibrate_remind:
            case R.id.fl_more_personal_vibrate_remind:
                isVibrateRemindOpen = !isVibrateRemindOpen;
                vibrateRemindCb.setChecked(isVibrateRemindOpen);
                modifyAccountInfo(TransUtil.booleanToInt(isAudioRemindOpen), genderType,
                        TransUtil.booleanToInt(isVibrateRemindOpen));
                break;

            case R.id.fl_more_personal_notify:
                startActivity(new Intent(getActivity(), NotifyActivity.class));
                break;

            case R.id.fl_more_personal_modify_pwd:
                startActivity(new Intent(getActivity(), PasswordModifyActivity.class));
                break;

            case R.id.fl_more_personal_login_record:
                startActivity(new Intent(getActivity(), LoginRecordActivity.class));
                break;

            case R.id.fl_more_personal_about:

                break;

            case R.id.tv_more_personal_logout:
                showPromptDialog();
                break;
        }
    }

    private void setViewData() {
        if (UserDao.user != null) {
            ImgUtil.load(getActivity(), UserDao.user.getAvatar(), avatarIv);
            nameTv.setText(UserDao.user.getUsername());
            signTv.setText(UserDao.user.getSign());
            phoneNumTv.setText(UserDao.user.getPhone());
            if (UserDao.user.getGender() == 0) {
                genderTv.setText(getString(R.string.female));
            } else if (UserDao.user.getGender() == 1) {
                genderTv.setText(getString(R.string.male));
            } else {
                genderTv.setText(getString(R.string.keep_secret));
            }
            audioRemindCb.setChecked(TransUtil.intToBoolean(UserDao.user.getAudioCues()));
            vibrateRemindCb.setChecked(TransUtil.intToBoolean(UserDao.user.getVibratesCues()));
            isAudioRemindOpen = audioRemindCb.isChecked();
            isVibrateRemindOpen = vibrateRemindCb.isChecked();
        }
    }

    private void showPromptDialog() {
        PromptDialog.PromptBuilder builder = new PromptDialog.PromptBuilder(getActivity());
        PromptDialog promptDialog = builder.setPromptContent(R.string.prompt_confirm_logout)
                .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        logout();
                    }
                }).create();
        promptDialog.show();
    }

    private void showSexDialog() {
        if (sexDialog == null) {
            SexDialog.SexBuilder sexBuilder = new SexDialog.SexBuilder(getActivity());
            sexDialog = sexBuilder.setOnSexSelectListener(new SexDialog.SexBuilder.OnSexSelectListener() {
                @Override
                public void secretClick() {
                    genderTv.setText(getString(R.string.keep_secret));
                    genderType = 2;
                    modifyAccountInfo(TransUtil.booleanToInt(isAudioRemindOpen), genderType,
                            TransUtil.booleanToInt(isVibrateRemindOpen));
                }

                @Override
                public void maleClick() {
                    genderTv.setText(getString(R.string.male));
                    genderType = 1;
                    modifyAccountInfo(TransUtil.booleanToInt(isAudioRemindOpen), genderType,
                            TransUtil.booleanToInt(isVibrateRemindOpen));
                }

                @Override
                public void femaleClick() {
                    genderTv.setText(getString(R.string.female));
                    genderType = 0;
                    modifyAccountInfo(TransUtil.booleanToInt(isAudioRemindOpen), genderType,
                            TransUtil.booleanToInt(isVibrateRemindOpen));
                }
            }).create();
        }
        sexDialog.show();
    }

    private void modifyAccountInfo(final int audioCues, final int gender, final int vibratesCues) {
        HttpMore.modifyAccountInfo(audioCues, gender, UserDao.user.getId(), UserDao.user.getSign(),
                UserDao.user.getUsername(), vibratesCues, new HttpCallBack(getActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        UserDao.user.setGender(gender);
                        UserDao.user.setAudioCues(audioCues);
                        UserDao.user.setVibratesCues(vibratesCues);
                        UserDao.setUser(UserDao.user);
                        LogUtil.i("saveAccountInfo", UserDao.user);
                        EventTrans.post(EventMsg.MORE_PERSONAL_INFO_REFRESH);
                    }
                });
    }

    private void getPersonalInfo() {
        HttpMore.getPersonalInfo(new HttpCallBack(getActivity(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    LoginUser loginUser = JSON.parseObject(data.toString(), LoginUser.class);
                    LogUtil.i("getPersonalInfo", loginUser);
                    if (loginUser != null) {
                        UserDao.setUser(loginUser);
                        UserDao.user = loginUser;
                        setViewData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logout() {
        HttpLogin.logout(new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                CommonUtil.clearDataByLogout(getHostActivity());
                startActivity(new Intent(getActivity(), LoginActivity.class));
                EventTrans.post(EventMsg.MORE_LOGOUT_FINISH);
                getHostActivity().finish();
            }
        });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.MORE_PERSONAL_INFO_REFRESH
                || msg.getKey() == EventMsg.MORE_PERSONAL_AVATAR_REFRESH) {
            initViewsData();

        } else if (msg.getKey() == EventMsg.CONTACT_NOTIFY_REFRESH) {
            if (SpCons.getNotifyRedPointVisible(getHostActivity())) {
                notifyRedPointIv.setVisibility(View.VISIBLE);
            } else {
                notifyRedPointIv.setVisibility(View.INVISIBLE);
            }
        }
    }

}
