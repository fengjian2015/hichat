package com.wewin.hichat.view.contact.group;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.datamanager.DataCache;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.adapter.ContactGroupMemberLvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.SelectDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.GroupMemberDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.view.contact.friend.FriendInfoActivity;
import com.wewin.hichat.view.search.CharacterParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 群组成员
 * Created by Darren on 2019/3/2
 */
public class GroupMemberListActivity extends BaseActivity {

    //汉字转换成拼音的类
    private CharacterParser characterParser = CharacterParser.getInstance();
    private ContactGroupMemberLvAdapter lvAdapter;
    private ListView containerLv;
    private List<FriendInfo> memberList = new ArrayList<>();
    private SelectDialog selectDialog;
    private String[] selectStrArr;
    private int managerCount = 0;
    private TextView memberCountTv, memberLimitTv, managerCountTv, managerLimitTv;
    private int selfGrade = 0;//0普通成员；1管理员；2群主
    private GroupInfo mGroupInfo;
    private final static String CHECK_MEMBER_INFO = "查看该成员资料";
    private final static String SEND_MESSAGE = "发送消息";
    private final static String TEMPORARY_CHAT = "发起临时会话";
    private final static String SET_MANAGER = "设定为管理员";
    private final static String CANCEL_MANAGER = "取消管理员权限";
    private final static String ADD_FRIEND = "加好友";
    private final static String REMOVE_MEMBER = "移出群组";
    private final int TYPE_MANAGER_N = 0;//设置取消管理员
    private final int TYPE_MANAGER_Y = 1;//设置成为管理员

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_group_member_list;
    }

    @Override
    protected void getIntentData() {
        mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO);
    }

    @Override
    protected void initViews() {
        containerLv = findViewById(R.id.lv_contact_group_member_container);
        memberCountTv = findViewById(R.id.tv_contact_group_member_count);
        memberLimitTv = findViewById(R.id.tv_contact_group_member_limit);
        managerCountTv = findViewById(R.id.tv_contact_group_manager_count);
        managerLimitTv = findViewById(R.id.tv_contact_group_manager_limit);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.group_member);
        setLeftText(R.string.group_info);
        initListView();
        if (mGroupInfo != null) {
            getGroupMemberList(mGroupInfo.getId());
        }
    }

    private void initListView() {
        lvAdapter = new ContactGroupMemberLvAdapter(this, memberList);
        containerLv.setAdapter(lvAdapter);
        containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (memberList.get(position).getId().equals(SpCons.getUser(getAppContext()).getId())) {
                    return;
                }
                int clickMemberGrade = memberList.get(position).getGrade();
                int friendship = memberList.get(position).getFriendship();
                switch (selfGrade) {
                    case 0:
                        if (friendship == 1) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, SEND_MESSAGE};
                        } else if (friendship == 0) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, ADD_FRIEND, TEMPORARY_CHAT};
                        }
                        break;

                    case 1:
                        if (friendship == 1 && clickMemberGrade == 0) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, SEND_MESSAGE, REMOVE_MEMBER};
                        } else if (friendship == 1 && (clickMemberGrade == 1 || clickMemberGrade == 2)) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, SEND_MESSAGE};
                        } else if (friendship == 0 && clickMemberGrade == 0) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, TEMPORARY_CHAT, ADD_FRIEND, REMOVE_MEMBER};
                        } else if (friendship == 0 && (clickMemberGrade == 1 || clickMemberGrade == 2)) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, TEMPORARY_CHAT, ADD_FRIEND};
                        }
                        break;

                    case 2:
                        if (friendship == 1 && clickMemberGrade == 0) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, SEND_MESSAGE, SET_MANAGER, REMOVE_MEMBER};
                        } else if (friendship == 1 && clickMemberGrade == 1) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, SEND_MESSAGE, CANCEL_MANAGER, REMOVE_MEMBER};
                        } else if (clickMemberGrade == 2) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO};
                        } else if (friendship == 0 && clickMemberGrade == 0) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, TEMPORARY_CHAT, SET_MANAGER, ADD_FRIEND, REMOVE_MEMBER};
                        } else if (friendship == 0 && clickMemberGrade == 1) {
                            selectStrArr = new String[]{CHECK_MEMBER_INFO, TEMPORARY_CHAT, CANCEL_MANAGER, ADD_FRIEND, REMOVE_MEMBER};
                        }
                        break;

                    default:

                        break;
                }
                showSelectDialog(position, selectStrArr);
            }
        });
    }

    private void updateLv() {
        if (lvAdapter != null) {
            lvAdapter.notifyDataSetChanged();
        }
    }

    private void showSelectDialog(final int memberPosition, final String[] itemStrArr) {
        SelectDialog.SelectBuilder selectBuilder = new SelectDialog.SelectBuilder(this);
        SelectDialog selectDialog = selectBuilder
                .setTitleStr(memberList.get(memberPosition).getUsername())
                .setSelectStrArr(itemStrArr)
                .setTextColor(R.color.red_light2)
                .setTextColorPosition(selectStrArr.length - 1)
                .setOnLvItemClickListener(new SelectDialog.SelectBuilder.OnLvItemClickListener() {
                    @Override
                    public void itemClick(int lvItemPosition) {
                        String clickStr = itemStrArr[lvItemPosition];
                        switch (clickStr) {
                            case CHECK_MEMBER_INFO:
                                Intent intent = new Intent(getAppContext(), FriendInfoActivity.class);
                                ChatRoom chatRoom = ChatRoomManager.getChatRoom(memberList
                                        .get(memberPosition).getId(), ChatRoom.TYPE_SINGLE);
                                intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
                                startActivity(intent);
                                break;

                            case SEND_MESSAGE:
                            case TEMPORARY_CHAT:
                                EventTrans.post(EventMsg.CONVERSATION_CHAT_FINISH);
                                FriendInfo member = memberList.get(memberPosition);
                                ChatRoomManager.startSingleRoomActivity(getHostActivity(), member);
                                break;

                            case SET_MANAGER:
                                getGroupManagerCount(mGroupInfo.getId(), memberPosition, TYPE_MANAGER_Y);
                                break;

                            case CANCEL_MANAGER:
                                setGroupManager(memberPosition, 0);
                                break;

                            case ADD_FRIEND:
                                DataCache spCache = new SpCache(getAppContext());
                                Subgroup friendSubgroup = (Subgroup) spCache.getObject(SpCons.SP_KEY_FRIEND_SUBGROUP);
                                applyAddFriend(memberList.get(memberPosition).getId(),
                                        SpCons.getUser(getAppContext()).getUsername() +
                                        getString(R.string.apply_add_friend), friendSubgroup.getId());
                                break;

                            case REMOVE_MEMBER:
                                moveOutGroupMember(memberPosition, mGroupInfo.getId(),
                                        memberList.get(memberPosition));
                                break;

                            default:

                                break;
                        }
                    }
                }).create();
        selectDialog.show();
    }

    private void getGroupMemberList(String groupId) {
        HttpContact.getGroupMemberList(groupId, -1,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            List<FriendInfo> dataList = JSON.parseArray(data.toString(), FriendInfo.class);
                            LogUtil.i("getGroupMemberList", dataList);
                            memberList.clear();
                            memberList.addAll(dataList);
                            parseSortLetter(memberList);
                            updateLv();
                            managerCount = 0;
                            for (FriendInfo member : memberList) {
                                if (member.getGrade() == 1) {
                                    managerCount += 1;
                                }
                                if (member.getId().equals(SpCons.getUser(getAppContext()).getId())) {
                                    selfGrade = member.getGrade();
                                }
                            }
                            memberCountTv.setText(String.valueOf(memberList.size()));
                            memberLimitTv.setText("/" + mGroupInfo.getGroupMax() + "人，管理员");
                            managerCountTv.setText(String.valueOf(managerCount));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getGroupManagerCount(final String groupId, final int memberPosition, final int type) {
        HttpContact.getGroupMemberList(groupId, 1, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    List<FriendInfo> managerList = JSON.parseArray(data.toString(), FriendInfo.class);
                    if (managerCount != managerList.size() - 1) {
                        getGroupMemberList(groupId);
                    }
                    //管理员包括群主
                    managerCount = managerList.size() - 1;
                    if (managerCount < 0) {
                        managerCount = 0;
                    }
                    if (type == TYPE_MANAGER_N) {
                        setGroupManager(memberPosition, type);

                    } else if (type == TYPE_MANAGER_Y) {
                        if (managerCount < 5) {
                            setGroupManager(memberPosition, type);
                        } else {
                            ToastUtil.showShort(getAppContext(), R.string.manger_beyond_limit_prompt);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setGroupManager(final int position, final int managerType) {
        HttpContact.setGroupManager(memberList.get(position).getId(), mGroupInfo.getId(), managerType,
                new HttpCallBack(getApplicationContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        getGroupMemberList(mGroupInfo.getId());
                    }

                    @Override
                    public void failure(String desc) {
                        getGroupMemberList(mGroupInfo.getId());
                    }
                });
    }

    private void applyAddFriend(String friendId, String verifyInfo, String groupingId) {
        HttpContact.applyAddFriend(friendId, verifyInfo, groupingId, SpCons.getUser(getAppContext()).getId(),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if(data==null){
                            ToastUtil.showShort(getAppContext(), getString(R.string.apply_add_friend_sent));
                        }else {
                            ToastUtil.showShort(getAppContext(), getString(R.string.add_to_friend));
                        }
                    }
                });
    }

    private void moveOutGroupMember(final int position, final String groupId, final FriendInfo member) {
        HttpContact.moveOutGroupMember(memberList.get(position).getGrade(), groupId, member.getId(),
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        EventTrans.post(EventMsg.CONTACT_GROUP_REMOVE_MEMBER, mGroupInfo, null, member);
                        getGroupMemberList(groupId);
                    }
                });
    }

    //解析首字母
    private void parseSortLetter(List<FriendInfo> friendInfoList) {
        for (FriendInfo friendInfo : friendInfoList) {
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(friendInfo.getUsername());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendInfo.setSortLetter(sortString.toUpperCase());
            } else {
                friendInfo.setSortLetter("#");
            }
        }
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()) {
            case EventMsg.CONTACT_FRIEND_NAME_REFRESH:
            case EventMsg.CONTACT_FRIEND_AVATAR_REFRESH:
                String friendId1 = msg.getData().toString();
                FriendInfo member = GroupMemberDao.getGroupMember(friendId1, mGroupInfo.getId());
                if (member != null) {
                    getGroupMemberList(mGroupInfo.getId());
                }
                break;

            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupInfo3 = (GroupInfo) msg.getData();
                FriendInfo receiver3 = (FriendInfo) msg.getThirdData();
                if (groupInfo3 == null || TextUtils.isEmpty(groupInfo3.getId())
                        || receiver3 == null || TextUtils.isEmpty(receiver3.getId())) {
                    return;
                }
                if (groupInfo3.getId().equals(mGroupInfo.getId())) {
                    if (receiver3.getId().equals(SpCons.getUser(getAppContext()).getId())) {
                        ToastUtil.showShort(getAppContext(), R.string.you_are_moved_out_from_group);
                        getHostActivity().finish();
                    } else {
                        getGroupMemberList(mGroupInfo.getId());

                    }
                }
                break;

            case EventMsg.CONTACT_GROUP_DISBAND:
                String groupId1 = msg.getData().toString();
                if (!TextUtils.isEmpty(groupId1) && groupId1.equals(mGroupInfo.getId())) {
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_QUIT:
                String groupId2 = msg.getData().toString();
                String friendId2 = msg.getSecondData().toString();
                if (!TextUtils.isEmpty(groupId2) && groupId2.equals(mGroupInfo.getId())
                        && !TextUtils.isEmpty(friendId2)
                        && !friendId2.equals(SpCons.getUser(getAppContext()).getId())) {
                    getGroupMemberList(mGroupInfo.getId());
                }
                break;

            default:

                break;
        }
    }

}
