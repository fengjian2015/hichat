package com.wewin.hichat.view.search;

import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.PhoneUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.widget.SideBarView;
import com.wewin.hichat.component.adapter.SearchPhoneContactLvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.HttpCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.PhoneContactDao;
import com.wewin.hichat.model.db.entity.PhoneContact;
import com.wewin.hichat.model.db.entity.SortModel;
import com.wewin.hichat.model.http.HttpContact;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * 通讯录
 * Created by Darren on 2019/2/14
 */
public class PhoneContactSearchActivity extends BaseActivity {

    private ListView containerLv;
    private TextView dialogTv;
    private EditText inputEt;
    private SideBarView sideBarView;
    //实例化汉字转拼音类
    private PinyinComparator pinyinComparator = new PinyinComparator();
    //汉字转换成拼音的类
    private CharacterParser characterParser = CharacterParser.getInstance();
    private List<SortModel> sortModelList = new ArrayList<>();
    private List<PhoneContact> mainPhoneContactList = new ArrayList<>();
    private SearchPhoneContactLvAdapter lvAdapter;
    //    private ImageView clearIv;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_friend_phone_contact;
    }

    @Override
    protected void initViews() {
        sideBarView = findViewById(R.id.sbv_contact_friend_phone_contact_side);
        containerLv = findViewById(R.id.lv_search_contact_friend_phone_contact_container);
        dialogTv = findViewById(R.id.tv_contact_friend_phone_contact_dialog);
        inputEt = findViewById(R.id.et_search_contact_friend_phone_contact_search_input);
//        clearIv = findViewById(R.id.iv_contact_friend_search_clear);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.phone_contact);
        setLeftText(R.string.back);
        sideBarView.setTextView(dialogTv);
        initListView();

        //获取手机通讯录
        List<PhoneContact> cacheContactList = PhoneUtil.getPhoneContactList(getAppContext());
        //与数据库数据对比，24小时内的邀请显示已邀请
        List<PhoneContact> dbContactList = PhoneContactDao.getPhoneContactList();
        for (PhoneContact phoneContact : cacheContactList) {
            for (PhoneContact dbContact : dbContactList) {
                if (dbContact.getPhone().equals(phoneContact.getPhone())) {
                    phoneContact.setInviteTime(dbContact.getInviteTime());
                }
            }
        }
        mainPhoneContactList.clear();
        mainPhoneContactList.addAll(cacheContactList);
        if (!mainPhoneContactList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (PhoneContact phoneContact : mainPhoneContactList) {
                sb.append(phoneContact.getPhone()).append(",");
            }
            String phoneStr = sb.toString().substring(0, sb.toString().length() - 1);
            LogUtil.i("phoneStr", phoneStr);
            matchPhoneContact(phoneStr);
        }
        getInviteTemplate();
    }

    /**
     * 获取通讯录文案
     */
    private void getInviteTemplate(){
        HttpContact.getInviteTemplate(new HttpCallBack(getAppContext(), ClassUtil.classMethodName()){
            @Override
            public void success(Object data, int count) {
                try {
                    if (data==null){
                        return;
                    }
                    Map map= JSON.parseObject(data+"");
                    SpCons.setString(PhoneContactSearchActivity.this,SpCons.INVITE_TEMPLATE,map.get("template")+"");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void setListener() {
        //设置右侧触摸监听
        sideBarView.setOnTouchingLetterChangedListener(new SideBarView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if (!sortModelList.isEmpty()) {
                    int position = lvAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        containerLv.setSelection(position);
                    }
                }
            }
        });

        //根据输入框输入值的改变来过滤搜索
        inputEt.addTextChangedListener(new CustomTextWatcher() {
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

        /*clearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEt.setText("");
            }
        });*/
    }

    private void initListView() {
        lvAdapter = new SearchPhoneContactLvAdapter(this, sortModelList);
        containerLv.setAdapter(lvAdapter);
        lvAdapter.setOnInviteClickListener(new SearchPhoneContactLvAdapter.OnInviteClickListener() {
            @Override
            public void inviteClick(int position) {
                doSendSMSTo(sortModelList.get(position).getCode(),position);

            }
        });
    }

    /**
     * 调起系统发短信功能
     * @param phoneNumber
     * @param position
     */
    public void doSendSMSTo(final String phoneNumber, final int position){
        final String message=SpCons.getString(this,SpCons.INVITE_TEMPLATE);
        if(TextUtils.isEmpty(message)){
            getInviteTemplate();
            return;
        }
        Rigger.on(this).permissions(Permission.SEND_SMS)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
                            intent.putExtra("sms_body", message);
                            startActivity(intent);
                        }
//                        String phone = sortModelList.get(position).getCode();
//                        if (TextUtils.isEmpty(phone)){
//                            return;
//                        }
//                        for (PhoneContact phoneContact : mainPhoneContactList) {
//                            if (phone.equals(phoneContact.getPhone())) {
//                                phoneContact.setInviteTime(TimeUtil.getServerTimestamp());
//                                sortModelList.get(position).setState(PhoneContact.INVITED);
//                                lvAdapter.updateListView(sortModelList);
//                                PhoneContactDao.updatePhoneContact(phoneContact);
//                                break;
//                            }
//                        }
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });

    }

    private void matchPhoneContact(String phoneStr) {
        HttpContact.matchPhoneContact(phoneStr, new HttpCallBack(this, ClassUtil.classMethodName(),true) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    JSONObject object = JSON.parseObject(data.toString());
                    String backPhoneStr = object.getString("phone");
                    LogUtil.i("backPhoneStr", backPhoneStr);
                    String[] phoneArr = backPhoneStr.split(",");

                    for (PhoneContact phoneContact : mainPhoneContactList) {
                        boolean isMatch = false;
                        for (String phone : phoneArr) {
                            //匹配上则对方已注册
                            if (phone.equals(phoneContact.getPhone())) {
                                isMatch = true;
                                phoneContact.setLocalState(PhoneContact.ADDED);
                                break;
                            }
                        }
                        //未匹配上则对方未注册
                        if (!isMatch && TimeUtil.calDaysDiff(phoneContact.getInviteTime(),
                                System.currentTimeMillis()) < 1) {
                            phoneContact.setLocalState(PhoneContact.INVITED);
                        } else if (!isMatch) {
                            phoneContact.setLocalState(PhoneContact.NOT_INVITE);
                        }
                    }
                    if (!mainPhoneContactList.isEmpty()) {
                        sortModelList.clear();
                        sortModelList.addAll(filledData(mainPhoneContactList));
                        // 根据a-z进行排序源数据
                        Collections.sort(sortModelList, pinyinComparator);
                        if (lvAdapter != null) {
                            lvAdapter.updateListView(sortModelList);
                        }
                        EventTrans.post(EventMsg.CONTACT_PHONE_CONTACT_REFRESH);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 为ListView填充数据
     */
    private List<SortModel> filledData(List<PhoneContact> phoneContactList) {
        List<SortModel> mSortList = new ArrayList<>();
        Random random = new Random();
        for (PhoneContact phoneContact : phoneContactList) {
            SortModel sortModel = new SortModel();
            sortModel.setName(phoneContact.getName());
            sortModel.setCode(phoneContact.getPhone());
            sortModel.setAvatar(random.nextInt(5) + "");
            sortModel.setState(phoneContact.getLocalState());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(phoneContact.getName());
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
        if (lvAdapter != null) {
            lvAdapter.updateListView(filterDataList);
        }
    }

}
