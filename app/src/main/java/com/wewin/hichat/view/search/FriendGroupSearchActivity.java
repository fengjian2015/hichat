package com.wewin.hichat.view.search;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.component.adapter.SearchFriendLvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.entity.BaseSearchEntity;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Darren on 2019/3/16
 */
public class FriendGroupSearchActivity extends BaseActivity {

    private CharacterParser characterParser = CharacterParser.getInstance();
    private EditText inputEt;
    private ImageView clearIv;
    private ListView containerLv;
    private List<FriendInfo> friendInfoList = new ArrayList<>();
    private List<FriendInfo> cacheFriendList = new ArrayList<>();
    private List<FriendInfo> cacheGroupList = new ArrayList<>();
    private SearchFriendLvAdapter lvAdapter;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (inputEt == null){
                return;
            }
            inputEt.requestFocus();
            SystemUtil.showKeyboard(getHostActivity(), inputEt);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_friend_group_search;
    }

    @Override
    protected void initViews() {
        inputEt = findViewById(R.id.et_contact_friend_group_search_input);
        clearIv = findViewById(R.id.iv_contact_friend_group_search_input_clear);
        containerLv = findViewById(R.id.lv_contact_friend_group_search_list);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.search);
        setLeftText(R.string.back);
        initListView();
        getDataList();
        handler.postDelayed(runnable, 300);
    }

    @Override
    protected void setListener() {
        //根据输入框输入值的改变来过滤搜索
        inputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                friendInfoList.clear();
                friendInfoList.addAll(filterData(s.toString().trim(), cacheFriendList));
                friendInfoList.addAll(filterData(s.toString().trim(), cacheGroupList));
                if (lvAdapter != null) {
                    lvAdapter.notifyDataSetChanged();
                }

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
    }

    private void initListView() {
        lvAdapter = new SearchFriendLvAdapter(this, friendInfoList);
        lvAdapter.setCheckVisible(false);
        containerLv.setAdapter(lvAdapter);
        containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo clickFriend = friendInfoList.get(position);
                if (ChatRoom.TYPE_GROUP.equals(clickFriend.getType())) {
                    ChatRoomManager.startGroupRoomActivity(getHostActivity(), clickFriend.getId());
                } else {
                    ChatRoomManager.startSingleRoomActivity(getHostActivity(), clickFriend.getId());
                }
            }
        });
    }

    private void getDataList() {
        cacheFriendList.clear();
        cacheFriendList.addAll(FriendDao.getFriendList());
        parseSortLetter(cacheFriendList);
        cacheGroupList.clear();
        List<GroupInfo> groupList = GroupDao.getGroupList();
        for (GroupInfo groupInfo : groupList) {
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.setId(groupInfo.getId());
            friendInfo.setUsername(groupInfo.getGroupName());
            friendInfo.setAvatar(groupInfo.getGroupAvatar());
            friendInfo.setSign(groupInfo.getDescription());
            friendInfo.setType(ChatRoom.TYPE_GROUP);
            cacheGroupList.add(friendInfo);
        }
        parseSortLetter(cacheGroupList);
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
    private List<FriendInfo> filterData(String searchInputStr, List<FriendInfo> dataList) {
        List<FriendInfo> resultList = new ArrayList<>();
        if (TextUtils.isEmpty(searchInputStr)) {
            return resultList;
        } else {
            for (FriendInfo friendInfo : dataList) {
                String name = friendInfo.getUsername();
                String phone = friendInfo.getPhone();
                String friendNote = friendInfo.getFriendNote();
                if (name.contains(searchInputStr)
                        || (phone != null && phone.contains(searchInputStr))
                        || (friendNote != null && friendNote.contains(searchInputStr))
                        || characterParser.getSelling(name).startsWith(searchInputStr)) {
                    resultList.add(friendInfo);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(resultList, new BaseSearchEntity.SortComparator());
        return resultList;
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
        super.onDestroy();
    }

}
