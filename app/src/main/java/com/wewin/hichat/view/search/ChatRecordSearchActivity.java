package com.wewin.hichat.view.search;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.adapter.ConversationRecordSearchRcvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRecordSearch;
import com.wewin.hichat.model.db.entity.ChatRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/3/14
 */
public class ChatRecordSearchActivity extends BaseActivity {

    private List<ChatRecordSearch> recordSearchList = new ArrayList<>();
    private RecyclerView containerRcv;
    private ConversationRecordSearchRcvAdapter rcvAdapter;
    private EditText inputEt;
    private ImageView clearIv;
    private ChatRecordSearch selectRecord;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conversation_record_search;
    }

    @Override
    protected void initViews() {
        setCenterTitle(R.string.search_conversation);
        setLeftText(R.string.conversation);
        containerRcv = findViewById(R.id.rcv_conversation_record_search_container);
        inputEt = findViewById(R.id.et_conversation_record_search_input);
        clearIv = findViewById(R.id.iv_conversation_record_search_clear);
    }

    @Override
    protected void initViewsData() {
        initRecyclerView();
    }

    @Override
    protected void setListener() {
        inputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String inputStr = inputEt.getText().toString().trim();
                rcvAdapter.setContentLvExpand(false);
                rcvAdapter.setSearchStr(inputStr);
                recordSearchList.clear();
                if (!TextUtils.isEmpty(inputStr)) {
                    List<ChatMsg> searchResultChatMsgList = MessageDao.getMessageListBySearch(inputStr);
                    if (searchResultChatMsgList != null) {
                        recordSearchList.addAll(transToRecordSearchList(searchResultChatMsgList));
                    }
                    clearIv.setVisibility(View.VISIBLE);
                } else {
                    clearIv.setVisibility(View.INVISIBLE);
                }
                updateRcv();
            }
        });
        clearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEt.setText("");
            }
        });
    }

    private void initRecyclerView() {
        rcvAdapter = new ConversationRecordSearchRcvAdapter(this, recordSearchList);
        containerRcv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        containerRcv.setAdapter(rcvAdapter);
        rcvAdapter.setOnRecordItemClickListener(new ConversationRecordSearchRcvAdapter.OnRecordItemClickListener() {
            @Override
            public void checkMore(int rcvPosition) {
                rcvAdapter.setContentLvExpand(true);
                selectRecord = recordSearchList.get(rcvPosition);
                recordSearchList.clear();
                recordSearchList.add(selectRecord);
                updateRcv();
            }

            @Override
            public void clickFirstItem(int rcvPosition) {
                ChatRecordSearch recordSearch = recordSearchList.get(rcvPosition);
                if (recordSearch.getContentItemList() != null && !recordSearch.getContentItemList().isEmpty()) {
                    long startTimestamp = recordSearch.getContentItemList().get(0).getCreateTimestamp();
                    startChatActivity(rcvPosition, startTimestamp);
                }
            }

            @Override
            public void clickLvItem(int rcvPosition, int lvPosition) {
                ChatRecordSearch recordSearch = recordSearchList.get(rcvPosition);
                List<ChatRecordSearch.ContentItem> contentItemList = new ArrayList<>(recordSearch.getContentItemList());
                if (!contentItemList.isEmpty()) {
                    contentItemList.remove(0);
                    if (lvPosition < contentItemList.size()) {
                        long startTimestamp = contentItemList.get(lvPosition).getCreateTimestamp();
                        LogUtil.i("lvPosition", lvPosition);
                        LogUtil.i("startTimestamp", startTimestamp);
                        startChatActivity(rcvPosition, startTimestamp);
                    }
                }
            }
        });
    }

    private void updateRcv() {
        if (rcvAdapter != null) {
            rcvAdapter.notifyDataSetChanged();
        }
    }

    private void startChatActivity(int rcvPosition, long startTimestamp) {
        ChatRecordSearch recordSearch = recordSearchList.get(rcvPosition);
        if (ChatRoom.TYPE_SINGLE.equals(recordSearch.getMsgType())) {
            ChatRoomManager.startSingleRoomActivity(getHostActivity(),
                    recordSearch.getChatId(), startTimestamp);

        } else {
            ChatRoomManager.startGroupRoomActivity(getHostActivity(),
                    recordSearch.getChatId(), startTimestamp);
        }
    }

    private List<ChatRecordSearch> transToRecordSearchList(List<ChatMsg> chatMsgList) {
        List<ChatRecordSearch> dataList = new ArrayList<>();
        String lastChatId = "";
        ChatRecordSearch recordSearch = null;
        for (ChatMsg chatMsg : chatMsgList) {
            if (!lastChatId.equals(chatMsg.getRoomId())) {
                recordSearch = new ChatRecordSearch();
                recordSearch.setChatId(chatMsg.getRoomId());
                recordSearch.setMsgType(chatMsg.getRoomType());
                List<ChatRecordSearch.ContentItem> contentItemList = new ArrayList<>();
                contentItemList.add(new ChatRecordSearch.ContentItem(chatMsg.getContent(),
                        chatMsg.getCreateTimestamp()));
                recordSearch.setContentItemList(contentItemList);

            } else if (recordSearch != null && recordSearch.getContentItemList() != null) {
                recordSearch.getContentItemList()
                        .add(new ChatRecordSearch.ContentItem(chatMsg.getContent(),
                                chatMsg.getCreateTimestamp()));
            }
            if (recordSearch != null && !lastChatId.equals(recordSearch.getChatId())) {
                dataList.add(recordSearch);
            }
            lastChatId = chatMsg.getRoomId();
        }
        return dataList;
    }

}
