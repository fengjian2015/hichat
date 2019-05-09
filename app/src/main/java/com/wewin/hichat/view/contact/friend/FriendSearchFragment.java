package com.wewin.hichat.view.contact.friend;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.datamanager.DataCache;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.CountryCode;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.SortModel;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.view.search.CountrySearchActivity;
import com.wewin.hichat.view.search.PhoneContactSearchActivity;
import java.util.HashMap;
import java.util.List;
import static android.app.Activity.RESULT_OK;

/**
 * 添加-找人
 * Created by Darren on 2018/12/24.
 */
public class FriendSearchFragment extends BaseFragment {

    private ImageView friendSearchIv, resultAvatarIv;
    private RelativeLayout countryRl;
    private FrameLayout resultGroupingFl;
    private LinearLayout resultContainerLl;
    private TextView countryTv, codeTv, promptTv, resultNameTv, resultSignTv, resultGroupingNameTv,
            verifyCountTv;
    private CountryCode selectCountryCode;
    private EditText phoneInputEt, resultVerifyInputEt;
    private Button resultApplyBtn;
    private PromptDialog promptDialog;
    private FrameLayout phoneContactFl, verifyFl;
    private int friendType = -1;//0自己；1好友；2陌生人
    private FriendInfo backFriend;
    private boolean isResultContainerVisible = false;
    private boolean isPromptVisible = false;
    private Subgroup friendSubgroup;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_friend_search;
    }

    @Override
    protected void initViews() {
        friendSearchIv = parentView.findViewById(R.id.iv_contact_add_friend_search);
        countryRl = parentView.findViewById(R.id.rl_contact_add_country_container);
        countryTv = parentView.findViewById(R.id.tv_contact_add_friend_country);
        phoneInputEt = parentView.findViewById(R.id.et_contact_add_friend_phone_input);
        phoneContactFl = parentView.findViewById(R.id.fl_contact_friend_search_phone_contact);
        codeTv = parentView.findViewById(R.id.tv_contact_add_friend_code);
        resultAvatarIv = parentView.findViewById(R.id.civ_contact_friend_search_result_avatar);
        resultGroupingFl = parentView.findViewById(R.id.fl_contact_friend_search_result_grouping);
        promptTv = parentView.findViewById(R.id.tv_contact_friend_search_prompt);
        resultNameTv = parentView.findViewById(R.id.tv_contact_friend_search_result_name);
        resultSignTv = parentView.findViewById(R.id.tv_contact_friend_search_result_sign);
        resultGroupingNameTv = parentView.findViewById(R.id.tv_contact_friend_search_result_grouping_name);
        verifyCountTv = parentView.findViewById(R.id.tv_contact_friend_search_result_verify_count);
        resultVerifyInputEt = parentView.findViewById(R.id.et_contact_friend_search_result_verify_info);
        resultApplyBtn = parentView.findViewById(R.id.btn_contact_friend_search_result_apply);
        verifyFl = parentView.findViewById(R.id.fl_contact_friend_search_verify_container);
        resultContainerLl = parentView.findViewById(R.id.ll_contact_friend_search_result_container);
    }

    @Override
    protected void initViewsData() {
        selectCountryCode = new CountryCode();
        if (!TextUtils.isEmpty(SpCons.getCountryName(getContext())) &&
                !TextUtils.isEmpty(SpCons.getCountryCode(getContext()))) {
            selectCountryCode.setCountry(SpCons.getCountryName(getContext()));
            selectCountryCode.setCode(SpCons.getCountryCode(getContext()));
        } else {
            selectCountryCode.setCountry(getString(R.string.philippines));
            selectCountryCode.setCode("+63");
        }
        countryTv.setText(selectCountryCode.getCountry());
        codeTv.setText(selectCountryCode.getCode());

        DataCache spCache = new SpCache(getActivity());
        friendSubgroup = (Subgroup) spCache.getObject(SpCons.SP_KEY_FRIEND_SUBGROUP);

    }

    @Override
    protected void setListener() {
        friendSearchIv.setOnClickListener(this);
        countryRl.setOnClickListener(this);
        phoneContactFl.setOnClickListener(this);
        resultGroupingFl.setOnClickListener(this);
        resultApplyBtn.setOnClickListener(this);

        phoneInputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isResultContainerVisible) {
                    isResultContainerVisible = false;
                    resultContainerLl.setVisibility(View.GONE);
                }
                if (isPromptVisible) {
                    isPromptVisible = false;
                    promptTv.setVisibility(View.GONE);
                }
            }
        });

        resultVerifyInputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String verifyInfoStr = resultVerifyInputEt.getText().toString().trim();
                verifyCountTv.setText(verifyInfoStr.length() + "/50");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_contact_add_friend_search:
                String phoneNum = phoneInputEt.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    setPrompt(R.string.please_input_search_phone_num);
                } else {
                    searchFriend(phoneNum);
                }
                break;

            case R.id.rl_contact_add_country_container:
                Intent intent = new Intent(getActivity(), CountrySearchActivity.class);
                intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, selectCountryCode);
                startActivityForResult(intent, LoginCons.INTENT_REQ_COUNTRY_SEARCH);
                break;

            case R.id.fl_contact_friend_search_phone_contact:
                Rigger.on(getHostActivity()).permissions(Permission.READ_CONTACTS)
                        .start(new PermissionCallback() {
                            @Override
                            public void onGranted() {
                                startActivity(new Intent(getContext(),
                                        PhoneContactSearchActivity.class));
                            }

                            @Override
                            public void onDenied(HashMap<String, Boolean> permissions) {

                            }
                        });
                break;

            case R.id.fl_contact_friend_search_result_grouping:
                Intent intent2 = new Intent(getActivity(), FriendSubgroupActivity.class);
                if (friendSubgroup != null) {
                    intent2.putExtra(ContactCons.EXTRA_CONTACT_FRIEND_SUBGROUP_ID, friendSubgroup.getId());
                }
                startActivityForResult(intent2, ContactCons.REQ_CONTACT_FRIEND_GROUPING_SELECT);
                break;

            case R.id.btn_contact_friend_search_result_apply:
                if (friendType == 1) {
                    ChatRoomManager.startSingleRoomActivity(getHostActivity(), backFriend.getId());

                } else if (friendType == 2) {
                    String verifyStr = resultVerifyInputEt.getText().toString().trim();
                    if (TextUtils.isEmpty(verifyStr)) {
                        ToastUtil.showShort(getHostActivity(), R.string.please_input_verify_info);
                    } else {
                        applyAddFriend(backFriend.getId(), verifyStr, friendSubgroup.getId());
                    }
                }
                break;
        }
    }

    private void setPrompt(int resourceId) {
        promptTv.setVisibility(View.VISIBLE);
        isPromptVisible = true;
        promptTv.setText(getString(resourceId));
    }

    private void searchFriend(final String phoneNum) {
        HttpContact.searchFriend(1, "friend", phoneNum, new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    List<FriendInfo> dataList = JSON.parseArray(data.toString(), FriendInfo.class);
                    LogUtil.i("searchFriend", dataList.toString());
                    processSearchResult(dataList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void processSearchResult(List<FriendInfo> dataList) {
        if (!dataList.isEmpty()) {
            backFriend = dataList.get(0);
            resultContainerLl.setVisibility(View.VISIBLE);
            isResultContainerVisible = true;
            ImgUtil.load(getActivity(), backFriend.getAvatar(), resultAvatarIv);
            resultNameTv.setText(backFriend.getUsername());
            resultSignTv.setText(backFriend.getSign());

            if (backFriend.getId().equals(UserDao.user.getId())) {
                resultGroupingFl.setVisibility(View.GONE);
                verifyFl.setVisibility(View.GONE);
                resultApplyBtn.setText(getString(R.string.personal_info));
                resultApplyBtn.setVisibility(View.INVISIBLE);
                friendType = 0;

            } else {
                setFriendshipView();
            }
            LogUtil.i("friendType", friendType);

        } else {
            showPromptDialog();
        }
    }

    private void setFriendshipView() {
        List<FriendInfo> friendList = FriendDao.getFriendList();
        boolean isFriend = false;
        resultApplyBtn.setVisibility(View.VISIBLE);
        for (FriendInfo friend : friendList) {
            if (friend != null && backFriend != null && friend.getId().equals(backFriend.getId())
                    && !backFriend.getId().equals(UserDao.user.getId())) {

                resultGroupingFl.setVisibility(View.GONE);
                verifyFl.setVisibility(View.GONE);
                resultApplyBtn.setText(getString(R.string.send_message));
                resultApplyBtn.setEnabled(true);
                friendType = 1;
                isFriend = true;
                break;
            }
        }
        if (!isFriend) {
            resultGroupingFl.setVisibility(View.VISIBLE);
            verifyFl.setVisibility(View.VISIBLE);
            resultApplyBtn.setText(getString(R.string.send_apply));
            resultApplyBtn.setEnabled(true);
            friendType = 2;
        }
    }

    private void showPromptDialog() {
        if (promptDialog == null) {
            PromptDialog.PromptBuilder promptBuilder = new PromptDialog.PromptBuilder(getActivity());
            promptDialog = promptBuilder.setPromptContent(R.string.search_friend_null).create();
        }
        promptDialog.show();
    }

    private void applyAddFriend(String friendId, String verifyInfo, String groupingId) {
        HttpContact.applyAddFriend(friendId, verifyInfo, groupingId, UserDao.user.getId(),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        resultApplyBtn.setText(getString(R.string.applying));
                        resultApplyBtn.setEnabled(false);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("FriendSearchFragment onActivityResult");
        if (resultCode == RESULT_OK) {
            if (requestCode == LoginCons.INTENT_REQ_COUNTRY_SEARCH) {
                SortModel sortModel = (SortModel) data.getSerializableExtra(
                        LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
                if (sortModel != null) {
                    countryTv.setText(sortModel.getName());
                    codeTv.setText(sortModel.getCode());
                    selectCountryCode.setCountry(sortModel.getName());
                    selectCountryCode.setCode(sortModel.getCode());
                }
            } else if (requestCode == ContactCons.REQ_CONTACT_FRIEND_GROUPING_SELECT) {
                String subgroupId = data.getStringExtra(ContactCons.EXTRA_CONTACT_FRIEND_SUBGROUP_ID);
                String subgroupName = data.getStringExtra(ContactCons.EXTRA_CONTACT_FRIEND_SUBGROUP_NAME);
                friendSubgroup.setId(subgroupId);
                friendSubgroup.setGroupName(subgroupName);
                resultGroupingNameTv.setText(subgroupName);
            }
        }
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.CONTACT_ADD_FRIEND_PROCESS_REFRESH) {
            setFriendshipView();
        } else if (msg.getKey() == EventMsg.CONTACT_DELETE_BY_OTHER) {
//            ToastUtil.showShort(getHostActivity(), R.string.other_side_deleted_you);
//            getHostActivity().finish();
        }
    }

}