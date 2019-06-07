package com.wewin.hichat.view.contact.group;


import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.LubanCallBack;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.adapter.ContactGroupCreateMemberRcvAdapter;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PhotoDialog;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.view.album.AlbumChoiceActivity;
import com.wewin.hichat.view.search.FriendSearchActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 建群
 * Created by Darren on 2018/12/24.
 */
public class GroupCreateFragment extends BaseFragment {

    private ImageView avatarIv, avatarIconIv;
    private TextView nameCountTv, introduceCountTv;
    private EditText groupNameEt, introduceEt;
    private LinearLayout firstLimitLl, secondLimitLl, thirdLimitLl, needVerifyLl, notVerifyLl;
    private CheckBox firstLimitCb, secondLimitCb, thirdLimitCb, needVerifyCb, notVerifyCb;
    private RecyclerView containerRcv;
    private final int firstLimit = 200;
    private final int secondLimit = 500;
    private final int thirdLimit = 1000;
    private int groupLimit = firstLimit;
    private final int needVerify = 1;
    private final int notVerify = 0;
    private int verifyType = needVerify;
    private List<FriendInfo> mFriendList = new ArrayList<>();
    private List<String> selectImgList = new ArrayList<>();
    private PhotoDialog photoDialog;
    private ContactGroupCreateMemberRcvAdapter rcvAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_group_create;
    }

    @Override
    protected void initViews() {
        avatarIv = parentView.findViewById(R.id.iv_contact_group_create_avatar);
        avatarIconIv = parentView.findViewById(R.id.iv_contact_group_create_icon);
        groupNameEt = parentView.findViewById(R.id.et_contact_group_create_name);
        nameCountTv = parentView.findViewById(R.id.tv_contact_group_create_name_count);
        introduceEt = parentView.findViewById(R.id.et_contact_group_create_introduce);
        introduceCountTv = parentView.findViewById(R.id.tv_contact_group_create_introduce_count);
        firstLimitLl = parentView.findViewById(R.id.ll_contact_group_create_200);
        secondLimitLl = parentView.findViewById(R.id.ll_contact_group_create_500);
        thirdLimitLl = parentView.findViewById(R.id.ll_contact_group_create_1000);
        needVerifyLl = parentView.findViewById(R.id.ll_contact_group_create_need_verify);
        notVerifyLl = parentView.findViewById(R.id.ll_contact_group_create_not_verify);
        firstLimitCb = parentView.findViewById(R.id.cb_contact_group_create_200);
        secondLimitCb = parentView.findViewById(R.id.cb_contact_group_create_500);
        thirdLimitCb = parentView.findViewById(R.id.cb_contact_group_create_1000);
        needVerifyCb = parentView.findViewById(R.id.cb_contact_group_create_need_verify);
        notVerifyCb = parentView.findViewById(R.id.cb_contact_group_create_not_verify);
        containerRcv = parentView.findViewById(R.id.rcv_contact_group_create_member);
    }

    @Override
    protected void initViewsData() {
        initRecyclerView();
    }

    @Override
    protected void setListener() {
        firstLimitLl.setOnClickListener(this);
        secondLimitLl.setOnClickListener(this);
        thirdLimitLl.setOnClickListener(this);
        needVerifyLl.setOnClickListener(this);
        notVerifyLl.setOnClickListener(this);
        avatarIv.setOnClickListener(this);
        firstLimitCb.setOnClickListener(this);
        secondLimitCb.setOnClickListener(this);
        thirdLimitCb.setOnClickListener(this);
        needVerifyCb.setOnClickListener(this);
        notVerifyCb.setOnClickListener(this);

        groupNameEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String groupName = groupNameEt.getText().toString().trim();
                nameCountTv.setText(groupName.length() + "/12");
            }
        });
        introduceEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String introduce = introduceEt.getText().toString().trim();
                introduceCountTv.setText(introduce.length() + "/120");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_contact_group_create_avatar:
                showPhotoDialog();
                break;

            case R.id.cb_contact_group_create_200:
            case R.id.ll_contact_group_create_200:
                firstLimitCb.setChecked(true);
                secondLimitCb.setChecked(false);
                thirdLimitCb.setChecked(false);
                groupLimit = firstLimit;
                break;

            case R.id.cb_contact_group_create_500:
            case R.id.ll_contact_group_create_500:
                firstLimitCb.setChecked(false);
                secondLimitCb.setChecked(true);
                thirdLimitCb.setChecked(false);
                groupLimit = secondLimit;
                break;

            case R.id.cb_contact_group_create_1000:
            case R.id.ll_contact_group_create_1000:
                firstLimitCb.setChecked(false);
                secondLimitCb.setChecked(false);
                thirdLimitCb.setChecked(true);
                groupLimit = thirdLimit;
                break;

            case R.id.cb_contact_group_create_need_verify:
            case R.id.ll_contact_group_create_need_verify:
                needVerifyCb.setChecked(true);
                notVerifyCb.setChecked(false);
                verifyType = needVerify;
                break;

            case R.id.cb_contact_group_create_not_verify:
            case R.id.ll_contact_group_create_not_verify:
                needVerifyCb.setChecked(false);
                notVerifyCb.setChecked(true);
                verifyType = notVerify;
                break;

            default:

                break;

        }
    }

    //拍照裁剪结束后通过activity调用该方法
    public void setCameraImg(String path) {
        if (!TextUtils.isEmpty(path)) {
            selectImgList.add(path);
            ImgUtil.load(getActivity(), path, avatarIv);
            avatarIconIv.setVisibility(View.INVISIBLE);
        }
    }

    public void createGroup() {
        String groupName = groupNameEt.getText().toString().trim();
        String introduce = introduceEt.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            ToastUtil.showShort(getActivity(), R.string.input_group_name);
        } else {
            String friendIdStr = "";
            if (!mFriendList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (FriendInfo friend : mFriendList) {
                    sb.append(friend.getId()).append(",");
                }
                friendIdStr = sb.toString().substring(0, sb.toString().length() - 1);
            }
            createGroupCommit(introduce, groupName, friendIdStr);
        }
    }

    private void initRecyclerView() {
        rcvAdapter = new ContactGroupCreateMemberRcvAdapter(getActivity(), mFriendList);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        containerRcv.setLayoutManager(layoutManager);
        containerRcv.setAdapter(rcvAdapter);
        rcvAdapter.setOnRcvItemClickListener(new ContactGroupCreateMemberRcvAdapter.OnRcvItemClickListener() {
            @Override
            public void itemClick(int position) {
                if (position == 0) {
                    List<FriendInfo> friendInfoList = FriendDao.getFriendList();
                    for (FriendInfo friendInfo : friendInfoList) {
                        for (FriendInfo selectFriend : mFriendList) {
                            if (!TextUtils.isEmpty(friendInfo.getId())
                                    && friendInfo.getId().equals(selectFriend.getId())){
                                friendInfo.setChecked(true);
                            }
                        }
                    }
                    Intent intent = new Intent(getActivity(), FriendSearchActivity.class);
                    intent.putExtra(ContactCons.EXTRA_CONTACT_FRIEND_SEARCH_TITLE, getString(R.string.create_group));
                    intent.putExtra(ContactCons.EXTRA_CONTACT_FRIEND_LIST, (Serializable) friendInfoList);
                    startActivityForResult(intent, ContactCons.REQ_CONTACT_FRIEND_LIST_SELECT);
                }
            }
        });
    }

    private void showPhotoDialog() {
        if (photoDialog == null) {
            PhotoDialog.PhotoBuilder dialogBuilder = new PhotoDialog.PhotoBuilder(getActivity());
            photoDialog = dialogBuilder.setOnSelectClickListener(new PhotoDialog.PhotoBuilder.OnSelectClickListener() {
                @Override
                public void albumClick() {
                    startActivityForResult(new Intent(getActivity(), AlbumChoiceActivity.class),
                            ImgUtil.REQUEST_ALBUM);
                }

                @Override
                public void cameraClick() {
                    ImgUtil.openCamera(getActivity());
                }
            }).create();
        }
        photoDialog.show();
    }

    private void createGroupCommit(String introduction, String name, String friendIdStr) {
        HttpContact.createGroup(introduction, groupLimit, name, SpCons.getUser(getHostActivity()).getId(),
                verifyType, friendIdStr, new HttpCallBack(getActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            GroupInfo groupInfo = JSON.parseObject(data.toString(), GroupInfo.class);
                            ToastUtil.showShort(getContext(), R.string.create_success);
                            EventTrans.post(EventMsg.CONTACT_GROUP_CREATE_REFRESH);
                            if (!selectImgList.isEmpty()) {
                                compressImg(groupInfo, selectImgList.get(selectImgList.size() - 1));
                            }
                            getHostActivity().finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void compressImg(final GroupInfo groupInfo, String path) {
        ImgUtil.compress(getActivity(), path, new LubanCallBack() {
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                String fileName = file.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                uploadGroupAvatar(groupInfo, file, suffix);
            }
        });
    }

    private void uploadGroupAvatar(final GroupInfo groupInfo, File file, String suffix) {
        HttpContact.uploadGroupAvatar(file, groupInfo.getId(), suffix,
                new HttpCallBack(getActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            ToastUtil.showShort(getHostActivity(), R.string.upload_success);
                            LogUtil.i("uploadGroupAvatar", data);
                            JSONObject object = JSON.parseObject(data.toString());
                            String avatar = object.getString("avatar");
                            groupInfo.setGroupAvatar(avatar);
                            EventTrans.post(EventMsg.CONTACT_GROUP_AVATAR_REFRESH, groupInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("GroupSearchFragment onActivityResult");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImgUtil.REQUEST_ALBUM:
                    List<String> dataList = data.getStringArrayListExtra(ImgUtil.IMG_SELECT_PATH_LIST);
                    if (dataList != null && !dataList.isEmpty()) {
                        selectImgList.addAll(dataList);
                        ImgUtil.load(getActivity(), dataList.get(0), avatarIv);
                        avatarIconIv.setVisibility(View.INVISIBLE);
                    }
                    break;

                case ContactCons.REQ_CONTACT_FRIEND_LIST_SELECT:
                    List<FriendInfo> friendDataList = (List<FriendInfo>) data
                            .getSerializableExtra(ContactCons.EXTRA_CONTACT_FRIEND_LIST);
                    LogUtil.i("friendDataList", friendDataList);
                    mFriendList.clear();
                    mFriendList.addAll(friendDataList);
                    rcvAdapter.notifyDataSetChanged();
                    break;

                default:

                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            FileUtil.deleteDir(FileUtil.getSDCachePath(getActivity()));
        }
    }

}
