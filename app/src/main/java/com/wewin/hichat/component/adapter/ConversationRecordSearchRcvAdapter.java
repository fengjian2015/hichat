package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.model.db.entity.ChatRecordSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/3/14
 */
public class ConversationRecordSearchRcvAdapter extends BaseRcvAdapter {

    private Context context;
    private List<ChatRecordSearch> recordSearchList;
    private OnRecordItemClickListener itemClickListener;
    private boolean contentLvExpand = false;
    private String searchStr = "";

    public ConversationRecordSearchRcvAdapter(Context context, List<ChatRecordSearch> recordSearchList) {
        this.context = context;
        this.recordSearchList = recordSearchList;
    }

    public interface OnRecordItemClickListener {
        void checkMore(int rcvPosition);

        void clickFirstItem(int rcvPosition);

        void clickLvItem(int rcvPosition, int lvPosition);
    }

    public void setOnRecordItemClickListener(OnRecordItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public void setContentLvExpand(boolean state) {
        this.contentLvExpand = state;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return recordSearchList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_conversation_record_search, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int rcvPosition) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder iHolder = (ItemViewHolder) holder;
            ImgUtil.load(context, recordSearchList.get(rcvPosition).getChatAvatar(), iHolder.avatarIv);
            iHolder.nameTv.setText(recordSearchList.get(rcvPosition).getChatName());
            final List<ChatRecordSearch.ContentItem> contentItemList = new ArrayList<>(recordSearchList.get(rcvPosition).getContentItemList());
            if (!contentItemList.isEmpty()) {
                String content = contentItemList.get(0).getContent();
                if (!TextUtils.isEmpty(searchStr) && !TextUtils.isEmpty(content)) {
                    int contentStartIndex = content.toLowerCase().indexOf(searchStr);
                    String subContent;
                    if (contentStartIndex > 12) {
                        subContent = "..." + content.substring(contentStartIndex - 3, content.length());
                    } else {
                        subContent = content;
                    }
                    int startIndex = subContent.toLowerCase().indexOf(searchStr);
                    int endIndex = startIndex + searchStr.length();
                    Spannable spannable = new SpannableString(subContent);
                    if (startIndex >= 0 && endIndex <= subContent.length()){
                        spannable.setSpan(new ForegroundColorSpan(0xff4285f4), startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    iHolder.contentTv.setText(spannable);
                }

                iHolder.timeTv.setText(TimeUtil.timestampToStr(contentItemList.get(0).getCreateTimestamp(), "HH:mm"));

                if (contentItemList.size() > 1) {
                    contentItemList.remove(0);
                    final ConversationRecordContentItemLvAdapter lvAdapter = new ConversationRecordContentItemLvAdapter(context, contentItemList);
                    lvAdapter.setLvExpand(contentLvExpand);
                    lvAdapter.setSearchStr(searchStr);
                    iHolder.moreRecordLv.setAdapter(lvAdapter);
                }
                if (contentItemList.size() > 3) {
                    iHolder.promptTv.setVisibility(View.VISIBLE);
                    if (!contentLvExpand) {
                        iHolder.promptTv.setText(context.getString(R.string.check_more_record));
                        iHolder.promptTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (itemClickListener != null) {
                                    itemClickListener.checkMore(rcvPosition);
                                }
                            }
                        });
                    } else {
                        iHolder.promptTv.setText("共" + recordSearchList.get(rcvPosition).getContentItemList().size() + "条记录");
                    }
                } else {
                    iHolder.promptTv.setVisibility(View.GONE);
                }

                iHolder.firstItemRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null){
                            itemClickListener.clickFirstItem(rcvPosition);
                        }
                    }
                });

                iHolder.moreRecordLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (itemClickListener != null){
                            itemClickListener.clickLvItem(rcvPosition, position);
                        }
                    }
                });

            }
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarIv;
        private TextView nameTv, contentTv, timeTv, promptTv;
        private ListView moreRecordLv;
        private RelativeLayout firstItemRl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.iv_item_conversation_record_search_avatar);
            nameTv = itemView.findViewById(R.id.tv_item_conversation_record_search_name);
            contentTv = itemView.findViewById(R.id.tv_item_conversation_record_search_content);
            timeTv = itemView.findViewById(R.id.tv_item_conversation_record_search_time);
            moreRecordLv = itemView.findViewById(R.id.lv_item_conversation_record_search_more);
            promptTv = itemView.findViewById(R.id.tv_item_conversation_record_item_prompt);
            firstItemRl = itemView.findViewById(R.id.rl_item_conversation_record_search_first_item);
        }
    }

}
