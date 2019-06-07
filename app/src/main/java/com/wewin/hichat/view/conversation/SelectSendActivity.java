package com.wewin.hichat.view.conversation;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.datamanager.DataCache;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.component.adapter.ConversationSelectAdapter;
import com.wewin.hichat.component.adapter.ConversationSelectSendAdapter;
import com.wewin.hichat.component.adapter.ConversationSelectSendElvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.entity.BaseSearchEntity;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.db.entity.SelectSubgroup;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.model.socket.ChatSocket;
import com.wewin.hichat.view.search.CharacterParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * @author jason
 * 转发消息
 */
public class SelectSendActivity extends BaseActivity {
    public static final String DATA = "chat_msg";
    private ExpandableListView expandableListView;
    private ListView listView;
    private RecyclerView rcvView;
    private EditText inputEt;
    private ImageView clearIv;
    private ImageView backIv;
    private TextView contentTv;
    private TextView rightTv;
    private TextView leftTv;
    private ChatMsg mChatMsg;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser = CharacterParser.getInstance();
    /**
     * 选中的人或群
     */
    private List<SelectSubgroup.DataBean> selectList = new ArrayList<>();
    private ConversationSelectAdapter selectAdapter;
    /**
     * 展示会话列表界面的数据
     */
    private List<SelectSubgroup> elvConversationList = new ArrayList<>();
    private ConversationSelectSendElvAdapter elvConversationAdapter;

    /**
     * 展示好友列表的界面的数据
     */
    private List<SelectSubgroup> elvFriendList = new ArrayList<>();
    private ConversationSelectSendElvAdapter elvFriendAdapter;

    /**
     * 展示群聊界面的数据
     */
    private List<SelectSubgroup.DataBean> listGroupList = new ArrayList<>();
    private ConversationSelectSendAdapter listGroupAdapter;

    /**
     * 展示搜索界面的数据
     */
    private List<SelectSubgroup.DataBean> searchList = new ArrayList<>();
    private ConversationSelectSendAdapter searchAdapter;

    /**
     * 0 临时会话界面，1好友，2群聊 3搜索
     */
    private int type;

    private final int MAX_NUMBER = 6;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_send;
    }

    @Override
    protected void getIntentData() {
        mChatMsg = (ChatMsg) getIntent().getSerializableExtra(DATA);
        if (mChatMsg == null) {
            finish();
        }
    }

    @Override
    protected void initViews() {
        expandableListView = findViewById(R.id.elv_contact_select_send_list);
        listView = findViewById(R.id.lv_contact_select_send_list);
        rcvView = findViewById(R.id.rcv_contact_select_send_list);
        inputEt = findViewById(R.id.et_contact_search_input);
        clearIv = findViewById(R.id.iv_contact_search_input_clear);
        backIv = findViewById(R.id.iv_left_back);
        leftTv = findViewById(R.id.tv_left_text);
        contentTv = findViewById(R.id.tv_center_title);
        rightTv = findViewById(R.id.tv_right_text);
        contentTv.setText(getString(R.string.forward));
        rightTv.setText(getString(R.string.determine));
        leftTv.setText(getString(R.string.back));

    }

    @Override
    protected void initViewsData() {
        getSubgroupFriendList();
        initConversation();
        initSelect();
    }

    /**
     * 初始化选择项
     */
    private void initSelect() {
        rcvView.setLayoutManager(new GridLayoutManager(this, 6));
        selectAdapter = new ConversationSelectAdapter(this, selectList);
        rcvView.setAdapter(selectAdapter);
    }

    /**
     * 更新选中的数据
     */
    public boolean updateSelect() {
        if (selectList.size() >= MAX_NUMBER) {
            new PromptDialog.PromptBuilder(this)
                    .setPromptContent(String.format(getString(R.string.forward_max_prompt), MAX_NUMBER + ""))
                    .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                        @Override
                        public void confirmClick() {

                        }
                    })
                    .setCancelVisible(false)
                    .create()
                    .show();
            return false;
        } else {
            selectAdapter.notifyDataSetChanged();
        }
        return true;
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
        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    finish();
                    return;
                }
                changeView();
            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    finish();
                    return;
                }
                changeView();
            }
        });
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送消息
                for (SelectSubgroup.DataBean dataBean : selectList) {
                    //这里保存消息是因为发送文件消息保存消息规则不一样导致无法保存
                    ChatMsg chatMsg = ChatRoomManager.packForwardMsg(mChatMsg, dataBean.getRoomType(), dataBean.getRoomId());
                    chatMsg.setMsgId(System.currentTimeMillis() + "");
                    ChatSocket.getInstance().send(chatMsg);
                    ChatRoomManager.saveChatMsg(SelectSendActivity.this, chatMsg, true);
                }
                finish();
            }
        });
    }

    /**
     * 初始化第一个界面，需要展示会话
     */
    private void initConversation() {
        type = 0;
        if (elvConversationList.size() <= 0) {
            SelectSubgroup selectFriend = new SelectSubgroup();
            selectFriend.setGroupName(getString(R.string.select_friend));
            elvConversationList.add(selectFriend);

            SelectSubgroup selectGroup = new SelectSubgroup();
            selectGroup.setGroupName(getString(R.string.select_group));
            elvConversationList.add(selectGroup);

            SelectSubgroup selectConversation = new SelectSubgroup();
            selectConversation.setGroupName(getString(R.string.recent_contacts));
            selectConversation.setData(new ArrayList<SelectSubgroup.DataBean>());
            List<ChatRoom> roomList = ChatRoomDao.getRoomList();
            Collections.sort(roomList, new ChatRoom.TopComparator());
            for (ChatRoom chatRoom : roomList) {
                SelectSubgroup.DataBean dataBean = new SelectSubgroup.DataBean();
                dataBean.setRoomId(chatRoom.getRoomId());
                dataBean.setRoomType(chatRoom.getRoomType());
                if (ChatRoom.TYPE_GROUP.equals(chatRoom.getRoomType())) {
                    GroupInfo groupInfo = GroupDao.getGroup(chatRoom.getRoomId());
                    if (groupInfo != null) {
                        dataBean.setUsername(groupInfo.getGroupName());
                        dataBean.setAvatar(groupInfo.getGroupAvatar());
                        if (groupInfo.getGrade()== GroupInfo.TYPE_GRADE_NORMAL
                                && groupInfo.getGroupSpeak() == 0) {
                            dataBean.setSendMark(1);
                        } else {
                            dataBean.setSendMark(0);
                        }
                    }

                } else {
                    FriendInfo contactUser = ContactUserDao.getContactUser(chatRoom.getRoomId());
                    if (contactUser != null) {
                        if (!TextUtils.isEmpty(contactUser.getFriendNote())) {
                            dataBean.setUsername(contactUser.getFriendNote());
                        } else {
                            dataBean.setUsername(contactUser.getUsername());
                        }
                        dataBean.setAvatar(contactUser.getAvatar());
                    }
                }
                selectConversation.getData().add(dataBean);
            }

            elvConversationList.add(selectConversation);
        }
        initConversationEL();
    }

    private void initConversationEL() {
        listView.setVisibility(View.GONE);
        expandableListView.setVisibility(View.VISIBLE);

        elvConversationAdapter = new ConversationSelectSendElvAdapter(this, elvConversationList, selectList);
        expandableListView.setAdapter(elvConversationAdapter);
        expandableListView.expandGroup(2);
        elvConversationAdapter.setOnGroupItemClickListener(new ConversationSelectSendElvAdapter.OnGroupItemClickListener() {
            @Override
            public void itemClick(int groupPosition) {
                if (groupPosition == 0) {
                    //选择好友
                    initFriend();
                    return;
                } else if (groupPosition == 1) {
                    //选择群聊
                    initGroup();
                    return;
                }

                if (!expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.expandGroup(groupPosition);
                } else {
                    expandableListView.collapseGroup(groupPosition);
                }
            }
        });
    }


    /**
     * 初始化朋友，展示的时候调用
     */
    private void initFriend() {
        type = 1;
        listView.setVisibility(View.GONE);
        expandableListView.setVisibility(View.VISIBLE);

        elvFriendAdapter = new ConversationSelectSendElvAdapter(this, elvFriendList, selectList);
        expandableListView.setAdapter(elvFriendAdapter);
        expandableListView.expandGroup(0);
        elvFriendAdapter.setOnGroupItemClickListener(new ConversationSelectSendElvAdapter.OnGroupItemClickListener() {
            @Override
            public void itemClick(int groupPosition) {
                if (!expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.expandGroup(groupPosition);
                } else {
                    expandableListView.collapseGroup(groupPosition);
                }
            }
        });
    }

    private void updateElv() {
        Collections.sort(elvFriendList, new SelectSubgroup.SubgroupComparator());
        if (elvFriendAdapter != null) {
            elvFriendAdapter.notifyDataSetChanged();
        }
    }

    private void getSubgroupFriendList() {
        HttpContact.getSubgroupFriendList(new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    final List<Subgroup> dataList = JSON.parseArray(data.toString(), Subgroup.class);
                    elvFriendList.clear();
                    for (Subgroup subgroup : dataList) {
                        if (subgroup.getIsDefault() != Subgroup.TYPE_TEMPORARY) {
                            //临时会话不显示
                            SelectSubgroup selectSubgroup = new SelectSubgroup();
                            selectSubgroup.setGroupName(subgroup.getGroupName());
                            selectSubgroup.setData(new ArrayList<SelectSubgroup.DataBean>());
                            selectSubgroup.setBuildTime(subgroup.getBuildTime());
                            selectSubgroup.setIsDefault(subgroup.getIsDefault());
                            for (FriendInfo friendInfo : subgroup.getList()) {
                                SelectSubgroup.DataBean dataBean = new SelectSubgroup.DataBean();
                                dataBean.setAvatar(friendInfo.getAvatar());
                                dataBean.setRoomType(ChatRoom.TYPE_SINGLE);
                                dataBean.setRoomId(friendInfo.getId());
                                if (friendInfo.getFriendNote() == null || friendInfo.getFriendNote().isEmpty()) {
                                    dataBean.setUsername(friendInfo.getUsername());
                                } else {
                                    dataBean.setUsername(friendInfo.getFriendNote());
                                }

                                selectSubgroup.getData().add(dataBean);
                            }
                            elvFriendList.add(selectSubgroup);
                        }
                    }
                    updateElv();
                    com.wewin.hichat.androidlib.rxjava.RxJavaScheduler.execute(new com.wewin.hichat.androidlib.rxjava.OnRxJavaProcessListener() {
                        @Override
                        public void process(ObservableEmitter<Object> emitter) {
                            for (Subgroup subgroup : dataList) {
                                if (subgroup.getIsDefault() == Subgroup.TYPE_FRIEND) {
                                    DataCache spCache = new SpCache(SelectSendActivity.this);
                                    spCache.setObject(SpCons.SP_KEY_FRIEND_SUBGROUP, subgroup);
                                }
                                FriendDao.addFriendList(subgroup.getList(), subgroup);
                                ContactUserDao.addContactUserList(subgroup.getList());
                            }
                            ChatRoomDao.updateTopShieldByFriendList(FriendDao.getFriendList());
                        }
                    }, new com.wewin.hichat.androidlib.rxjava.RxJavaObserver<Object>() {
                        @Override
                        public void onComplete() {
                            EventTrans.post(EventMsg.CONTACT_FRIEND_LIST_GET_REFRESH);
                            EventTrans.post(EventMsg.CONTACT_LIST_GET_REFRESH);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 初始化群聊，展示的时候调用
     */
    private void initGroup() {
        type = 2;
        listView.setVisibility(View.VISIBLE);
        expandableListView.setVisibility(View.GONE);
        if (listGroupList.size() <= 0) {
            List<GroupInfo> groupList = GroupDao.getGroupList();
            for (GroupInfo group : groupList) {
                SelectSubgroup.DataBean dataBean = new SelectSubgroup.DataBean();
                dataBean.setRoomId(group.getId());
                dataBean.setRoomType(ChatRoom.TYPE_GROUP);
                dataBean.setUsername(group.getGroupName());
                dataBean.setAvatar(group.getGroupAvatar());
                if (group.getGrade() == GroupInfo.TYPE_GRADE_NORMAL
                        && group.getGroupSpeak() == 0) {
                    dataBean.setSendMark(1);
                } else {
                    dataBean.setSendMark(0);
                }
                listGroupList.add(dataBean);
            }
        }
        rebuildData(listGroupList, listGroupAdapter);
        listGroupAdapter = new ConversationSelectSendAdapter(this, listGroupList, selectList);
        listView.setAdapter(listGroupAdapter);
    }

    /**
     * 为ListView填充数据
     */
    private void rebuildData(List<SelectSubgroup.DataBean> dataList, ConversationSelectSendAdapter adapter) {
        for (SelectSubgroup.DataBean dataBean : dataList) {
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(dataBean.getUsername());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                dataBean.setSortLetter(sortString.toUpperCase());
            } else {
                dataBean.setSortLetter("#");
            }
        }
        Collections.sort(dataList, new BaseSearchEntity.SortComparator());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    private void initSearchAdapter() {
        listView.setVisibility(View.VISIBLE);
        expandableListView.setVisibility(View.GONE);
        if (searchList.size() <= 0) {
            rebuildData(searchList, searchAdapter);
            searchAdapter = new ConversationSelectSendAdapter(this, searchList, selectList);
            listView.setAdapter(searchAdapter);
        }
        if (listGroupList.size() <= 0) {
            List<GroupInfo> groupList = GroupDao.getGroupList();
            for (GroupInfo group : groupList) {
                SelectSubgroup.DataBean dataBean = new SelectSubgroup.DataBean();
                dataBean.setRoomId(group.getId());
                dataBean.setRoomType(ChatRoom.TYPE_GROUP);
                dataBean.setUsername(group.getGroupName());
                dataBean.setAvatar(group.getGroupAvatar());
                if (group.getGrade() == GroupInfo.TYPE_GRADE_NORMAL
                        && group.getGroupSpeak() == 0) {
                    dataBean.setSendMark(1);
                } else {
                    dataBean.setSendMark(0);
                }
                listGroupList.add(dataBean);
            }
        }
    }

    /**
     * 搜索数据
     */
    private void filterData(String filterStr) {
        initSearchAdapter();
        if (TextUtils.isEmpty(filterStr)) {
            changeView();
        } else {
            type = 3;
            searchList.clear();
            for (SelectSubgroup.DataBean dataBean : listGroupList) {
                String name = dataBean.getUsername();
                if (name.contains(filterStr)
                        || characterParser.getSelling(name).startsWith(filterStr)) {
                    searchList.add(dataBean);
                }
            }
            for (SelectSubgroup selectSubgroup : elvFriendList) {
                for (SelectSubgroup.DataBean dataBean : selectSubgroup.getData()) {
                    String name = dataBean.getUsername();
                    if (name.contains(filterStr)
                            || characterParser.getSelling(name).startsWith(filterStr)) {
                        searchList.add(dataBean);
                    }
                }
            }
        }
        rebuildData(searchList, searchAdapter);
    }

    /**
     * 切换
     */
    private void changeView() {
        initConversation();
    }

    public void notifyData() {
        switch (type) {
            case 0:
                if (elvConversationAdapter != null) {
                    elvConversationAdapter.notifyDataSetChanged();
                }
                break;
            case 1:
                if (elvFriendAdapter != null) {
                    elvFriendAdapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (listGroupAdapter != null) {
                    listGroupAdapter.notifyDataSetChanged();
                }
                break;
            case 3:
                if (searchAdapter != null) {
                    searchAdapter.notifyDataSetChanged();
                }
                break;
            default:
                if (elvConversationAdapter != null) {
                    elvConversationAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
