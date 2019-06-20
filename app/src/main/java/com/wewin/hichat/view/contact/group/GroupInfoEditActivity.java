package com.wewin.hichat.view.contact.group;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PhotoDialog;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.view.album.AlbumChoiceActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 编辑群资料
 * Created by Darren on 2018/12/31.
 */
public class GroupInfoEditActivity extends BaseActivity {

    private GroupInfo mGroupInfo;
    private EditText nameEt, introduceEt;
    private TextView nameCountTv, introduceCountTv;
    private ImageView avatarIv;
    private PhotoDialog photoDialog;
    private List<String> selectImgList = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_group_edit_info;
    }

    @Override
    protected void initViews() {
        avatarIv = findViewById(R.id.civ_contact_group_info_avatar);
        nameEt = findViewById(R.id.et_contact_group_info_name);
        introduceEt = findViewById(R.id.et_contact_group_info_sign);
        nameCountTv = findViewById(R.id.tv_contact_group_info_name_count);
        introduceCountTv = findViewById(R.id.tv_contact_group_info_sign_count);
    }

    @Override
    protected void getIntentData() {
        mGroupInfo = (GroupInfo) getIntent()
                .getSerializableExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.modify_group_info);
        setRightTv(R.string.finish);
        setLeftCancelVisible();
    }

    @Override
    protected void setListener() {
        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        nameEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                nameCountTv.setText(s.toString().length() + "/12");
            }
        });
        introduceEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                introduceCountTv.setText(s.toString().length() + "/120");
            }
        });
        if (mGroupInfo != null) {
            nameEt.setText(mGroupInfo.getGroupName());
            introduceEt.setText(mGroupInfo.getDescription());
            ImgUtil.load(getHostActivity(), mGroupInfo.getGroupAvatar(), avatarIv,
                    R.drawable.img_avatar_group_default);
            LogUtil.i("groupInfo.getAvatar", mGroupInfo.getGroupAvatar());
        }
    }

    @Override
    protected void onRightTvClick() {
        String groupName = nameEt.getText().toString().trim();
        String introduce = introduceEt.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            ToastUtil.showShort(getAppContext(), R.string.input_group_name);
        } else if (groupName.equals(mGroupInfo.getGroupName())
                && introduce.equals(mGroupInfo.getDescription())) {
            GroupInfoEditActivity.this.finish();
        } else {
            modifyGroupInfo(groupName, introduce);
        }
        if (!selectImgList.isEmpty()) {
            compressImg(selectImgList.get(selectImgList.size() - 1));
        }
    }

    private void modifyGroupInfo(final String groupName, final String introduce) {
        HttpContact.modifyGroupInfo(introduce, mGroupInfo.getId(), groupName, -1,
                new HttpCallBack(this, ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getApplicationContext(), R.string.modify_success);
                        mGroupInfo.setGroupName(groupName);
                        mGroupInfo.setDescription(introduce);
                        GroupDao.updateName(mGroupInfo.getId(), groupName);
                        EventTrans.post(EventMsg.CONTACT_GROUP_INFO_REFRESH, mGroupInfo);
                        GroupInfoEditActivity.this.finish();
                    }
                });
    }

    private void showPhotoDialog() {
        if (photoDialog == null) {
            PhotoDialog.PhotoBuilder dialogBuilder = new PhotoDialog.PhotoBuilder(this);
            photoDialog = dialogBuilder.setOnSelectClickListener(new PhotoDialog.PhotoBuilder.OnSelectClickListener() {
                @Override
                public void albumClick() {
                    startActivityForResult(new Intent(getAppContext(), AlbumChoiceActivity.class),
                            ImgUtil.REQUEST_ALBUM);
                }

                @Override
                public void cameraClick() {
                    ImgUtil.openCamera(GroupInfoEditActivity.this);
                }
            }).create();
        }
        photoDialog.show();
    }

    private void compressImg(String filePath) {
        ImgUtil.compress(this, filePath, new LubanCallBack() {
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                LogUtil.i("compressImg", file.getAbsoluteFile());
                uploadGroupAvatar(file);
            }
        });
    }

    private void uploadGroupAvatar(File file) {
        HttpContact.uploadGroupAvatar(file, mGroupInfo.getId(), "jpg", new HttpCallBack(this, ClassUtil.classMethodName(),true) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    JSONObject object = JSON.parseObject(data.toString());
                    String avatarUrl = object.getString("avatar");
                    LogUtil.i("uploadGroupAvatar", avatarUrl);
                    ToastUtil.showShort(getAppContext(), R.string.upload_success);
                    mGroupInfo.setGroupAvatar(avatarUrl);
                    GroupDao.updateAvatar(mGroupInfo.getId(), avatarUrl);
                    EventTrans.post(EventMsg.CONTACT_GROUP_AVATAR_REFRESH, mGroupInfo);

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
                        ImgUtil.load(GroupInfoEditActivity.this, dataList.get(0), avatarIv);
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
                        ImgUtil.load(GroupInfoEditActivity.this, ImgUtil.cropOutputPath, avatarIv);
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

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()){
            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupInfo1 = (GroupInfo) msg.getData();
                FriendInfo receiver = (FriendInfo) msg.getThirdData();
                if (groupInfo1 == null || TextUtils.isEmpty(groupInfo1.getId())
                        || receiver == null || TextUtils.isEmpty(receiver.getId())){
                    return;
                }
                if (groupInfo1.getId().equals(mGroupInfo.getId())
                        && receiver.getId().equals(SpCons.getUser(getAppContext()).getId())){
                    ToastUtil.showShort(getAppContext(), R.string.you_are_moved_out_from_group);
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_DISBAND:
                String groupId2 = msg.getData().toString();
                if (!TextUtils.isEmpty(groupId2) && groupId2.equals(mGroupInfo.getId())){
                    getHostActivity().finish();
                }
                break;

            default:

                break;
        }
    }

}
