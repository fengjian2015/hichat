package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.model.db.entity.SortModel;

import java.util.List;

/**
 * Created by Darren on 2019/2/3
 */
public class SearchCountryLvAdapter extends BaseAdapter {

    private List<SortModel> modelList;
    private Context context;

    public SearchCountryLvAdapter(Context context, List<SortModel> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    public List<SortModel> getDataList() {
        return modelList;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<SortModel> list) {
        this.modelList = list;
        notifyDataSetChanged();
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return modelList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < modelList.size(); i++) {
            String sortStr = modelList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getCount() {
        if (modelList == null){
            return 0;
        }else {
            return modelList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder iHolder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_search_country, null);
            iHolder = new ItemViewHolder();
            iHolder.letterTv = convertView.findViewById(R.id.tv_search_country_letter);
            iHolder.nameTv = convertView.findViewById(R.id.tv_search_country_name);
            iHolder.codeTv = convertView.findViewById(R.id.tv_search_country_code);
            iHolder.hookIv = convertView.findViewById(R.id.iv_search_country_hook);
            convertView.setTag(iHolder);
        }else {
            iHolder = (ItemViewHolder)convertView.getTag();
        }

        final SortModel mContent = modelList.get(position);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            iHolder.letterTv.setVisibility(View.VISIBLE);
            iHolder.letterTv.setText(mContent.getSortLetters());
        } else {
            iHolder.letterTv.setVisibility(View.GONE);
        }

        iHolder.nameTv.setText(modelList.get(position).getName());
        iHolder.codeTv.setText(modelList.get(position).getCode());

        if (modelList.get(position).isChecked()){
            iHolder.hookIv.setVisibility(View.VISIBLE);
        }else {
            iHolder.hookIv.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ItemViewHolder{
        TextView letterTv, nameTv, codeTv;
        ImageView hookIv;
    }
}
