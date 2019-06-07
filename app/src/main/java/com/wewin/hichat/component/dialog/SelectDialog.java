package com.wewin.hichat.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wewin.hichat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by Darren on 2019/3/2
 */
public class SelectDialog extends Dialog {

    private SelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class SelectBuilder {
        private TextView titleTv, cancelTv;
        private ListView containerLv;
        private final SelectDialog selectDialog;
        private OnLvItemClickListener itemClickListener;
        private Activity activity;
        private List<String> strList = new ArrayList<>();
        private int textColorPosition = -1;
        private int resourceId = -1;
        private int allResourceId = -1;
        public SelectBuilder(Activity activity) {
            this.activity = activity;
            selectDialog = new SelectDialog(activity, R.style.dialog_common);
            View dialogView = View.inflate(activity, R.layout.dialog_bot_select, null);
            selectDialog.setContentView(dialogView);
            titleTv = dialogView.findViewById(R.id.tv_dialog_bot_select_title);
            cancelTv = dialogView.findViewById(R.id.tv_dialog_bot_select_cancel);
            containerLv = dialogView.findViewById(R.id.lv_dialog_bot_select_container);
        }

        public SelectDialog create() {
            initListView(strList);
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectDialog != null){
                        selectDialog.dismiss();
                    }
                }
            });
            containerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClick(position);
                    }
                    if (selectDialog != null){
                        selectDialog.dismiss();
                    }
                }
            });
            selectDialog.setCancelable(true);
            selectDialog.setCanceledOnTouchOutside(true);
            if (selectDialog.getWindow() != null) {
                selectDialog.getWindow().setGravity(Gravity.BOTTOM);
            }
            return selectDialog;
        }

        public interface OnLvItemClickListener {
            void itemClick(int lvItemPosition);
        }

        public SelectBuilder setOnLvItemClickListener(OnLvItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        public SelectBuilder setTitleStr(String titleStr) {
            if (!TextUtils.isEmpty(titleStr)) {
                titleTv.setText(titleStr);
                titleTv.setVisibility(View.VISIBLE);
            } else {
                titleTv.setVisibility(View.GONE);
            }
            return this;
        }

        public SelectBuilder setSelectStrArr(String[] strArr) {
            setSelectStrList(Arrays.asList(strArr));
            return this;
        }

        public SelectBuilder setSelectStrList(List<String> strList) {
            this.strList.clear();
            this.strList.addAll(strList);
            return this;
        }

        public SelectBuilder setTextColorPosition(int position) {
            this.textColorPosition = position;
            return this;
        }

        public SelectBuilder setTextColor(int resourceId) {
            this.resourceId = resourceId;
            return this;
        }


        public SelectBuilder setAllTextColor(int resourceId) {
            this.allResourceId = resourceId;
            return this;
        }

        private void initListView(List<String> strList) {
            SelectLvAdapter lvAdapter = new SelectLvAdapter(strList);
            containerLv.setAdapter(lvAdapter);
        }

        private class SelectLvAdapter extends BaseAdapter {

            private List<String> strList;

            private SelectLvAdapter(List<String> strList) {
                this.strList = strList;
            }

            @Override
            public int getCount() {
                if (strList == null) {
                    return 0;
                } else {
                    return strList.size();
                }
            }

            @Override
            public Object getItem(int position) {
                return strList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ItemViewHolder iHolder;
                if (convertView == null) {
                    convertView = View.inflate(activity, R.layout.item_dialog_select, null);
                    iHolder = new ItemViewHolder();
                    iHolder.itemTv = convertView.findViewById(R.id.tv_item_dialog_bot_select);
                    convertView.setTag(iHolder);
                } else {
                    iHolder = (ItemViewHolder) convertView.getTag();
                }

                iHolder.itemTv.setText(strList.get(position));
                if (allResourceId!=-1){
                    iHolder.itemTv.setTextColor(activity.getResources().getColor(allResourceId));
                }else {
                    if (position == textColorPosition) {
                        iHolder.itemTv.setTextColor(activity.getResources().getColor(resourceId));
                    } else {
                        iHolder.itemTv.setTextColor(activity.getResources().getColor(R.color.black_00));
                    }
                }

                return convertView;
            }

            private class ItemViewHolder {
                private TextView itemTv;
            }

        }

    }

}
