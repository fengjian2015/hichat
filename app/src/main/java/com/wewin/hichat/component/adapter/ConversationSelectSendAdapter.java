package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.db.entity.SelectSubgroup;
import com.wewin.hichat.view.conversation.SelectSendActivity;

import java.util.List;

/**
 * @author jason
 */
public class ConversationSelectSendAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;
    private List<SelectSubgroup.DataBean> mList;
    private List<SelectSubgroup.DataBean> mSelectList;

    public ConversationSelectSendAdapter(Context context, List<SelectSubgroup.DataBean> list,List<SelectSubgroup.DataBean> selectList){
        this.context = context;
        this.mList = list;
        mSelectList=selectList;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < mList.size(); i++) {
            String sortStr = mList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetter().charAt(0);
    }

    @Override
    public int getCount() {
        if (mList == null){
            return 0;
        }else {
            return mList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_conversation_select_send_list, null);
            iHolder = new ItemViewHolder();
            iHolder.letterTv = convertView.findViewById(R.id.tv_contact_group_letter);
            iHolder.avatarIv = convertView.findViewById(R.id.iv_contact_friend_search_list_avatar);
            iHolder.nameTv = convertView.findViewById(R.id.tv_contact_friend_search_list_name);
            iHolder.checkIv = convertView.findViewById(R.id.iv_item_search_friend_check);
            convertView.setTag(iHolder);
        }else {
            iHolder = (ItemViewHolder)convertView.getTag();
        }
        final SelectSubgroup.DataBean dataBean = mList.get(position);
        ImgUtil.load(context, dataBean.getAvatar(), iHolder.avatarIv);
        iHolder.nameTv.setText(dataBean.getUsername());
        ImgUtil.load(context, R.drawable.unselected_hollow_big, iHolder.checkIv);

        boolean isSelect=false;
        for(SelectSubgroup.DataBean dataBean1:mSelectList){
            if (dataBean1.getRoomId().equals(dataBean.getRoomId())&&dataBean1.getRoomType().equals(dataBean.getRoomType())){
                ImgUtil.load(context, R.drawable.selected_blue_big, iHolder.checkIv);
                isSelect=true;
                break;
            }
        }
        final boolean finalIsSelect = isSelect;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataBean.getSendMark()==0) {
                    if (!finalIsSelect) {
                        if (context instanceof SelectSendActivity
                                && ((SelectSendActivity) context).updateSelect()) {
                            mSelectList.add(dataBean);
                            notifyDataSetChanged();
                        }
                    } else {
                        for (SelectSubgroup.DataBean dataBean1 : mList) {
                            if (dataBean1.getRoomId().equals(dataBean.getRoomId()) && dataBean1.getRoomType().equals(dataBean.getRoomType())) {
                                mSelectList.remove(dataBean1);
                                notifyDataSetChanged();
                                if (context instanceof SelectSendActivity) {
                                    ((SelectSendActivity) context).updateSelect();
                                }
                                break;
                            }
                        }
                    }
                }else{
                    ToastUtil.showShort(context,context.getString(R.string.been_banned_forwarding));
                }
            }
        });

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            iHolder.letterTv.setVisibility(View.VISIBLE);
            iHolder.letterTv.setText(dataBean.getSortLetter());
        } else {
            iHolder.letterTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ItemViewHolder {
        private TextView nameTv,letterTv;
        private ImageView avatarIv, checkIv;
    }

}
