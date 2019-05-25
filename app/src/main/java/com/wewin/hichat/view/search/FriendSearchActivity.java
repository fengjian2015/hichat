package com.wewin.hichat.view.search;

import android.content.Intent;
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
import com.wewin.hichat.component.adapter.SearchFriendLvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.model.db.entity.BaseSearchEntity;
import com.wewin.hichat.model.db.entity.FriendInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 好友搜索
 * Created by Darren on 2018/12/31.
 */
public class FriendSearchActivity extends BaseActivity {

    //汉字转换成拼音的类
    private CharacterParser characterParser = CharacterParser.getInstance();
    private SearchFriendLvAdapter lvAdapter;
    private ListView containerLv;
    private EditText inputEt;
    private ImageView clearIv;
    private String title;
    private List<FriendInfo> mFriendList = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_search;
    }

    @Override
    protected void initViews() {
        containerLv = findViewById(R.id.lv_contact_search_list);
        inputEt = findViewById(R.id.et_contact_search_input);
        clearIv = findViewById(R.id.iv_contact_search_input_clear);
    }

    @Override
    protected void getIntentData() {
        title = getIntent().getStringExtra(ContactCons.EXTRA_CONTACT_FRIEND_SEARCH_TITLE);
        List<FriendInfo> totalList = (List<FriendInfo>) getIntent()
                .getSerializableExtra(ContactCons.EXTRA_CONTACT_FRIEND_LIST);
        mFriendList.clear();
        mFriendList.addAll(totalList);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(title);
        setLeftText(R.string.back);
        setRightTv(R.string.finish);
        initListView();
        if (mFriendList != null && !mFriendList.isEmpty()) {
            parseSortLetter(mFriendList);
            if (lvAdapter != null) {
                lvAdapter.updateListView(mFriendList);
            }
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
    }

    @Override
    protected void onRightTvClick() {
        List<FriendInfo> dataList = new ArrayList<>();
        for (FriendInfo friendInfo : lvAdapter.getList()) {
            if (friendInfo.isChecked()){
                dataList.add(friendInfo);
            }
        }
        Intent intent = new Intent();
        intent.putExtra(ContactCons.EXTRA_CONTACT_FRIEND_LIST, (Serializable) dataList);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    private void initListView() {
        lvAdapter = new SearchFriendLvAdapter(this, mFriendList);
        containerLv.setAdapter(lvAdapter);

        containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvAdapter.getList().get(position).setChecked(!lvAdapter.getList().get(position).isChecked());
                lvAdapter.notifyDataSetChanged();
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
        // 根据a-z进行排序
        Collections.sort(friendInfoList, new BaseSearchEntity.SortComparator());
        if (lvAdapter != null) {
            lvAdapter.updateListView(friendInfoList);
        }
    }

    //根据输入框中的值来过滤数据并更新ListView
    private void filterData(String filterStr) {
        List<FriendInfo> filterDataList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = mFriendList;
        } else {
            filterDataList.clear();
            for (FriendInfo friendInfo : mFriendList) {
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
