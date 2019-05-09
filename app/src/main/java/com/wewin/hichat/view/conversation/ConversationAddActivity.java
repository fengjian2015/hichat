package com.wewin.hichat.view.conversation;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.adapter.ConversationAddListLvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.entity.BaseSearchEntity;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.view.search.CharacterParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 新建会话
 * Created by Darren on 2019/2/26
 */
public class ConversationAddActivity extends BaseActivity {

    private EditText inputEt;
    private LinearLayout addGroupConversationLl;
    private ListView containerLv;
    //汉字转换成拼音的类
    private CharacterParser characterParser = CharacterParser.getInstance();
    private List<FriendInfo> friendInfoList = new ArrayList<>();
    private ConversationAddListLvAdapter lvAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conversation_add_new;
    }

    @Override
    protected void initViews() {
        inputEt = findViewById(R.id.et_conversation_add_new_search_input);
        addGroupConversationLl = findViewById(R.id.ll_conversation_add_group_conversation);
        containerLv = findViewById(R.id.lv_conversation_add_container);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.add_conversation);
        initListView();
        friendInfoList.clear();
        friendInfoList.addAll(FriendDao.getFriendList());
        parseSortLetter(friendInfoList);
        Collections.sort(friendInfoList, new BaseSearchEntity.SortComparator());
        if (lvAdapter != null) {
            lvAdapter.updateListView(friendInfoList);
        }
        LogUtil.i("db friendList", FriendDao.getFriendList());
    }

    @Override
    protected void setListener() {
        addGroupConversationLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        inputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterData(s.toString());
            }
        });
    }

    private void initListView() {
        lvAdapter = new ConversationAddListLvAdapter(getApplicationContext(), friendInfoList);
        containerLv.setAdapter(lvAdapter);
        containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoomManager.startSingleRoomActivity(getHostActivity(),
                        friendInfoList.get(position).getId());
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

    //根据输入框中的值来过滤数据并更新ListView
    private void filterData(String filterStr) {
        List<FriendInfo> filterDataList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = friendInfoList;
        } else {
            filterDataList.clear();
            for (FriendInfo friendInfo : friendInfoList) {
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

}
