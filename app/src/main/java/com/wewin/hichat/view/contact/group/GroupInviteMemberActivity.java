package com.wewin.hichat.view.contact.group;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.adapter.SearchFriendLvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupMemberDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.BaseSearchEntity;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.view.search.CharacterParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 邀请新成员
 * Created by Darren on 2019/3/7
 */
public class GroupInviteMemberActivity extends BaseActivity {

    //汉字转换成拼音的类
    private CharacterParser characterParser = CharacterParser.getInstance();
    private SearchFriendLvAdapter lvAdapter;
    private ListView containerLv;
    private EditText inputEt;
    private ImageView clearIv;
    private LinearLayout cancelLl;
    private TextView rightConfirmTv, sumTv;
    private List<FriendInfo> memberList = new ArrayList<>();
    private List<FriendInfo> selectList = new ArrayList<>();
    private GroupInfo mGroupInfo;
    private String groupId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_group_invite_member;
    }

    @Override
    protected void initViews() {
        containerLv = findViewById(R.id.lv_contact_group_invite_search_list);
        inputEt = findViewById(R.id.et_contact_group_invite_search_input);
        clearIv = findViewById(R.id.iv_contact_group_invite_search_input_clear);
        sumTv = findViewById(R.id.tv_contact_group_invite_sum);
        cancelLl = findViewById(R.id.ll_contact_group_invite_left_cancel_container);
        rightConfirmTv = findViewById(R.id.tv_contact_group_invite_confirm);
    }

    @Override
    protected void getIntentData() {
        groupId = getIntent().getStringExtra(ContactCons.EXTRA_CONTACT_GROUP_ID);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(0);
        initListView();
        memberList.addAll(FriendDao.getFriendList());
        if (memberList != null && !memberList.isEmpty()) {
            parseSortLetter(memberList);

        }
        if (!TextUtils.isEmpty(groupId)) {
            getGroupInfo(groupId);
            getGroupMemberList(groupId);
        }
    }

    @Override
    protected void setListener() {
        //根据输入框输入值的改变来过滤搜索
        inputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
                if (s.length() == 0) {
                    clearIv.setVisibility(View.INVISIBLE);
                } else {
                    clearIv.setVisibility(View.VISIBLE);
                }
            }
        });
        clearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEt.setText("");
            }
        });

        cancelLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupInviteMemberActivity.this.finish();
            }
        });

        rightConfirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectList.isEmpty()) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (FriendInfo friendInfo : selectList) {
                    sb.append(friendInfo.getId()).append(",");
                }
                if (mGroupInfo != null) {
                    inviteGroupMember(sb.toString().substring(0, sb.toString().length() - 1),
                            mGroupInfo.getGroupNum());
                }
            }
        });
    }

    private void initListView() {
        lvAdapter = new SearchFriendLvAdapter(this, memberList);
        containerLv.setAdapter(lvAdapter);
        containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!lvAdapter.getList().get(position).isInvited()) {
                    lvAdapter.getList().get(position)
                            .setChecked(!memberList.get(position).isChecked());
                    lvAdapter.notifyDataSetChanged();
                }
                selectList.clear();
                for (FriendInfo friendInfo : lvAdapter.getList()) {
                    if (friendInfo.isChecked()) {
                        selectList.add(friendInfo);
                    }
                }
                sumTv.setText("(" + selectList.size() + "人" + ")");
                if (selectList.isEmpty()) {
                    rightConfirmTv.setEnabled(false);
                } else {
                    rightConfirmTv.setEnabled(true);
                }
            }
        });
    }

    private void updateLv() {
        if (lvAdapter != null) {
            lvAdapter.notifyDataSetChanged();
        }
    }

    //解析首字母
    private void parseSortLetter(List<FriendInfo> friendInfoList) {
        for (FriendInfo friendInfo : friendInfoList) {
            //汉字转换成拼音
            String name =friendInfo.getFriendNote();
            if (TextUtils.isEmpty(name)){
                name=friendInfo.getUsername();
            }
            String pinyin = characterParser.getSelling(name);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendInfo.setSortLetter(sortString.toUpperCase());
            } else {
                friendInfo.setSortLetter("#");
            }
        }
        Collections.sort(friendInfoList, new BaseSearchEntity.SortComparator());
        if (lvAdapter != null) {
            lvAdapter.updateListView(memberList);
        }
    }

    //根据输入框中的值来过滤数据并更新ListView
    private void filterData(String filterStr) {
        List<FriendInfo> filterDataList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = memberList;
        } else {
            filterDataList.clear();
            for (FriendInfo friendInfo : memberList) {
                String name = friendInfo.getUsername();
                if (name.contains(filterStr)
                        || characterParser.getSelling(name).startsWith(filterStr)) {
                    filterDataList.add(friendInfo);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDataList, new BaseSearchEntity.SortComparator());
        if (lvAdapter != null) {
            lvAdapter.updateListView(filterDataList);
        }
    }

    private void getGroupInfo(final String groupId) {
        HttpContact.getGroupInfo(groupId, new HttpCallBack(this, ClassUtil.classMethodName(),true) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    mGroupInfo = JSON.parseObject(data.toString(), GroupInfo.class);
                    LogUtil.i("getGroupInfo", mGroupInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getGroupMemberList(String groupId) {
        HttpContact.getGroupMemberList(groupId, -1,
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            List<FriendInfo> dataList = JSON.parseArray(data.toString(), FriendInfo.class);
                            for (FriendInfo friendInfo : memberList) {
                                for (FriendInfo info : dataList) {
                                    if (friendInfo.getId().equals(info.getId())) {
                                        friendInfo.setInvited(true);
                                    }
                                }
                            }
                            updateLv();
                            LogUtil.i("getGroupMemberList", memberList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void inviteGroupMember(String friendIdStr, String groupNum) {
        HttpContact.inviteGroupMember(friendIdStr, groupNum,
                new HttpCallBack(this, ClassUtil.classMethodName(),true) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getApplicationContext(), R.string.invite_success);
                        GroupMemberDao.addGroupMemberList(groupId, selectList);
                        ContactUserDao.addContactUserList(selectList);
                        GroupInviteMemberActivity.this.finish();
                        EventTrans.post(EventMsg.CONTACT_GROUP_INVITE_MEMBER_REFRESH);
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()){
            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupInfo1 = (GroupInfo) msg.getData();
                FriendInfo receiver1 = (FriendInfo) msg.getThirdData();
                if (groupInfo1 == null || TextUtils.isEmpty(groupInfo1.getId())
                        || receiver1 == null || TextUtils.isEmpty(receiver1.getId())) {
                    return;
                }
                if (groupInfo1.getId().equals(mGroupInfo.getId())) {
                    if (receiver1.getId().equals(SpCons.getUser(getAppContext()).getId())) {
                        ToastUtil.showShort(getAppContext(), R.string.you_are_moved_out_from_group);
                        getHostActivity().finish();
                    } else {
                        getGroupMemberList(mGroupInfo.getId());
                    }
                }
                break;

            case EventMsg.CONTACT_GROUP_DISBAND:
                String groupId1 = msg.getData().toString();
                if (!TextUtils.isEmpty(groupId1) && groupId1.equals(mGroupInfo.getId())){
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_QUIT:
                String groupId2 = msg.getData().toString();
                String friendId2 = msg.getSecondData().toString();
                if (!TextUtils.isEmpty(groupId2) && groupId2.equals(mGroupInfo.getId())
                        && !TextUtils.isEmpty(friendId2)
                        && !friendId2.equals(SpCons.getUser(getAppContext()).getId())){
                    getGroupMemberList(mGroupInfo.getId());
                }
                break;

            case EventMsg.CONTACT_GROUP_PERMISSION_REFRESH:
                GroupInfo groupInfo3 = (GroupInfo) msg.getData();
                if (groupInfo3 == null || TextUtils.isEmpty(groupInfo3.getId())) {
                    return;
                }
                if (groupInfo3.getId().equals(mGroupInfo.getId())){
                    int inviteMark = groupInfo3.getInviteFlag();
                    if (inviteMark == 0) {
                        ToastUtil.showShort(getAppContext(), R.string.group_invite_permission_closed);
                        getHostActivity().finish();
                    }
                }
                break;

            default:

                break;
        }
    }


}
