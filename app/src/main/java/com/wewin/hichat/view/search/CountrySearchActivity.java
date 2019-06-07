package com.wewin.hichat.view.search;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.widget.SideBarView;
import com.wewin.hichat.component.adapter.SearchCountryLvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.model.db.entity.CountryInfo;
import com.wewin.hichat.model.db.entity.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 选择国家
 * Created by Darren on 2019/2/3
 */
public class CountrySearchActivity extends BaseActivity {

    //汉字转换成拼音的类
    private CharacterParser characterParser;
    private List<SortModel> sortModelList;
    //根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;
    private SideBarView sideBarView;
    private ListView containerLv;
    private TextView dialogTv;
    private EditText inputEt;
    private ImageView clearIv;
    private SearchCountryLvAdapter lvAdapter;
    private CountryInfo countryInfo;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_country_code;
    }

    @Override
    protected void initViews() {
        inputEt = findViewById(R.id.et_search_country_input);
        containerLv = findViewById(R.id.lv_search_country_list);
        sideBarView = findViewById(R.id.sbv_search_country_side);
        dialogTv = findViewById(R.id.tv_search_country_dialog);
    }

    @Override
    protected void getIntentData() {
        countryInfo = (CountryInfo)getIntent()
                .getSerializableExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.your_country);
        sideBarView.setTextView(dialogTv);

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        List<CountryInfo> countryInfoList = getCountryCode(getApplicationContext());

        if (countryInfoList != null && !countryInfoList.isEmpty()){
            sortModelList = filledData(countryInfoList);
            // 根据a-z进行排序源数据
            Collections.sort(sortModelList, pinyinComparator);
            initListView();
        }
    }

    @Override
    protected void setListener() {
        //设置右侧触摸监听
        sideBarView.setOnTouchingLetterChangedListener(new SideBarView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = lvAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    containerLv.setSelection(position);
                }

            }
        });

        //根据输入框输入值的改变来过滤搜索
        inputEt.addTextChangedListener(new CustomTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
                /*if (s.length() == 0) {
                    clearIv.setVisibility(View.INVISIBLE);
                } else {
                    clearIv.setVisibility(View.VISIBLE);
                }*/
            }
        });
    }

    private void initListView(){
        lvAdapter = new SearchCountryLvAdapter(getApplicationContext(), sortModelList);
        containerLv.setAdapter(lvAdapter);

        containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (SortModel sortModel : sortModelList) {
                    sortModel.setChecked(false);
                }
                sortModelList.get(position).setChecked(true);
                lvAdapter.notifyDataSetChanged();

                Intent intent = new Intent();
                intent.putExtra(LoginCons.EXTRA_LOGIN_COUNTRY_CODE, lvAdapter.getDataList().get(position));
                setResult(RESULT_OK, intent);
                CountrySearchActivity.this.finish();
            }
        });
    }

    private static List<CountryInfo> getCountryCode(Context context){
        List<CountryInfo> countryInfoList = new ArrayList<>();
        countryInfoList.add(new CountryInfo(context.getString(R.string.china), "+86"));
        countryInfoList.add(new CountryInfo(context.getString(R.string.taiwan), "+886"));
        countryInfoList.add(new CountryInfo(context.getString(R.string.hongkong), "+852"));
        countryInfoList.add(new CountryInfo(context.getString(R.string.philippines), "+63"));
        countryInfoList.add(new CountryInfo(context.getString(R.string.malaysia), "+60"));
        countryInfoList.add(new CountryInfo(context.getString(R.string.cambodia), "+855"));
        return countryInfoList;
    }

    /**
     * 为ListView填充数据
     */
    private List<SortModel> filledData(List<CountryInfo> countryInfoList) {
        List<SortModel> mSortList = new ArrayList<>();
        for (CountryInfo country : countryInfoList) {
            SortModel sortModel = new SortModel();
            sortModel.setName(country.getCountry());
            sortModel.setCode(country.getCode());
            if (this.countryInfo != null
                    && this.countryInfo.getCountry().equals(country.getCountry())){
                sortModel.setChecked(true);
            }

            //汉字转换成拼音
            String pinyin = characterParser.getSelling(country.getCountry());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDataList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = sortModelList;
        } else {
            filterDataList.clear();
            for (SortModel sortModel : sortModelList) {
                String name = sortModel.getName();
                if (name.contains(filterStr)
                        || characterParser.getSelling(name).startsWith(filterStr)) {
                    filterDataList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDataList, pinyinComparator);
        if (lvAdapter != null){
            lvAdapter.updateListView(filterDataList);
        }
    }

}
