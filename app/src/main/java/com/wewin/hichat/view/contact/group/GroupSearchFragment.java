package com.wewin.hichat.view.contact.group;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.db.entity.Notify;
import com.wewin.hichat.model.db.entity.SocketServer;
import com.wewin.hichat.model.http.HttpContact;

import java.util.List;

/**
 * 添加群
 * Created by Darren on 2018/12/24.
 */
public class GroupSearchFragment extends BaseFragment {

    private ImageView groupSearchIv, avatarIv;
    private EditText searchInputEt, verifyInputEt;
    private PromptDialog promptDialog;
    private TextView promptTv, nameTv, signTv, verifyCountTv;
    private LinearLayout resultContainerLl;
    private FrameLayout verifyContainerFl;
    private Button applyBtn;
    private boolean isResultContainerVisible = false;
    private boolean isPromptVisible = false;
    private int groupType = -1;//0未加入；1已加入
    private GroupInfo mGroupInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_group_search;
    }

    @Override
    protected void initViews() {
        groupSearchIv = parentView.findViewById(R.id.iv_contact_group_search_icon);
        searchInputEt = parentView.findViewById(R.id.et_contact_group_search_num_input);
        promptTv = parentView.findViewById(R.id.tv_contact_group_search_prompt);
        avatarIv = parentView.findViewById(R.id.iv_contact_group_search_result_avatar);
        verifyInputEt = parentView.findViewById(R.id.et_contact_group_search_result_verify_input);
        nameTv = parentView.findViewById(R.id.tv_contact_group_search_result_name);
        signTv = parentView.findViewById(R.id.tv_contact_group_search_result_sign);
        verifyCountTv = parentView.findViewById(R.id.tv_contact_group_search_verify_input_count);
        applyBtn = parentView.findViewById(R.id.btn_contact_group_search_result_apply);
        resultContainerLl = parentView.findViewById(R.id.ll_contact_group_search_result_container);
        verifyContainerFl = parentView.findViewById(R.id.fl_contact_group_search_result_verify_container);
    }

    @Override
    protected void setListener() {
        groupSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNum = searchInputEt.getText().toString().trim();
                if (groupNum.isEmpty()) {
                    setPrompt(R.string.please_input_group_id);
                } else {
                    searchGroup(1, groupNum);
                }
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupType == 0) {
                    String verifyInfoStr = verifyInputEt.getText().toString().trim();
                    if (TextUtils.isEmpty(verifyInfoStr)) {
                        ToastUtil.showShort(getHostActivity(), R.string.please_input_verify_info);
                    } else {
                        applyJoinGroup(mGroupInfo.getGroupNum(), verifyInfoStr);
                    }
                } else if (groupType == 1) {
                    ChatRoomManager.startGroupRoomActivity(getHostActivity(), mGroupInfo.getId());
                }
            }
        });

        searchInputEt.addTextChangedListener(new CustomTextWatcher() {
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

        verifyInputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String verifyStr = verifyInputEt.getText().toString().trim();
                verifyCountTv.setText(verifyStr.length() + "/50");
            }
        });
    }

    private void setPrompt(int resourceId) {
        promptTv.setVisibility(View.VISIBLE);
        isPromptVisible = true;
        promptTv.setText(getString(resourceId));
    }

    private void searchGroup(int page, String groupNum) {
        HttpContact.searchGroup(page, groupNum,
                new HttpCallBack(getActivity(), ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            if (!TextUtils.isEmpty(data.toString())) {
                                List<GroupInfo> dataList = JSON.parseArray(data.toString(), GroupInfo.class);
                                LogUtil.i("searchGroup", dataList);
                                processSearchResult(dataList);
                            } else {
                                showPromptDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void processSearchResult(List<GroupInfo> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
            mGroupInfo = dataList.get(0);
            resultContainerLl.setVisibility(View.VISIBLE);
            isResultContainerVisible = true;
            ImgUtil.load(getActivity(), mGroupInfo.getGroupAvatar(), avatarIv);
            nameTv.setText(mGroupInfo.getGroupName());
            signTv.setText(mGroupInfo.getDescription());

            List<GroupInfo> groupInfoList = GroupDao.getGroupList();
            boolean isMatch = false;
            for (GroupInfo groupInfo : groupInfoList) {
                if (groupInfo.getId().equals(mGroupInfo.getId())) {
                    isMatch = true;
                    groupType = 1;
                    verifyContainerFl.setVisibility(View.GONE);
                    applyBtn.setText(getString(R.string.send_message));
                    break;
                }
            }
            if (!isMatch) {
                groupType = 0;
                verifyContainerFl.setVisibility(View.VISIBLE);
                applyBtn.setText(getString(R.string.send_apply));
            }

        } else {
            showPromptDialog();
        }
    }

    private void applyJoinGroup(String groupNum, String verifyStr) {
        HttpContact.applyJoinGroup(groupNum, verifyStr, SpCons.getUser(getHostActivity()).getId(),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            JSONObject object = JSON.parseObject(data.toString());
                            int needVerifyFlag = object.getInteger("groupValid");
                            if (needVerifyFlag == 0) {
                                ToastUtil.showShort(getHostActivity(), R.string.success_to_join_group);
                                getHostActivity().finish();
                            } else {
                                applyBtn.setText(getString(R.string.applying));
                                applyBtn.setEnabled(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showPromptDialog() {
        if (promptDialog == null) {
            PromptDialog.PromptBuilder promptBuilder = new PromptDialog.PromptBuilder(getActivity());
            promptDialog = promptBuilder.setPromptContent(R.string.search_group_null).create();
        }
        promptDialog.show();
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()){
            case EventMsg.CONTACT_GROUP_AGREE_JOIN:
                GroupInfo groupData = (GroupInfo) msg.getData();
                if (groupData != null && mGroupInfo != null && mGroupInfo.getId().equals(groupData.getId())) {
                    groupType = 1;
                    applyBtn.setText(getString(R.string.send_message));
                    applyBtn.setEnabled(true);
                }
                break;

            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupData1 = (GroupInfo) msg.getData();
                if (groupData1 != null && mGroupInfo != null && mGroupInfo.getId().equals(groupData1.getId())) {
                    getHostActivity().finish();
                }
                break;
            case EventMsg.CONTACT_NOTIFY_REFRESH:
                if (msg.getData()!=null){
                    Notify notifyInfo = (Notify) msg.getData();
                    if("group".equals(notifyInfo.getNoticeType())&&notifyInfo.getStatus()==-1){
                        groupType = 0;
                        verifyContainerFl.post(new Runnable() {
                            @Override
                            public void run() {
                                verifyContainerFl.setVisibility(View.VISIBLE);
                                applyBtn.setText(getString(R.string.send_apply));
                                applyBtn.setEnabled(true);
                            }
                        });
                    }
                }
                break;
            default:

                break;
        }
    }

}
