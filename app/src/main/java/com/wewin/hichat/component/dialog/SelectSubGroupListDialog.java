package com.wewin.hichat.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.Subgroup;

import java.util.List;

/**
 * @author Jason
 * date:2019/4/2715:15
 * 选择群成员
 */
public class SelectSubGroupListDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private View view;
    private ListView listView;
    private TextView tvTitle;
    private TextView tvCancel;
    private TextView confirm;

    private SubGroupOnClick mSubGroupOnClick;
    private List<Subgroup> menuNames;
    private MyAdapter mAdapter;

    public SelectSubGroupListDialog(@NonNull Context context, List<Subgroup> menuNames) {
        super(context, R.style.dialog_common);
        this.context = context;
        this.menuNames = menuNames;
        init();
    }

    private void init() {
        view = View.inflate(context, R.layout.pop_select_sub_group, null);
        tvTitle=view.findViewById(R.id.tv_dialog_select_sub_group_title);
        listView=view.findViewById(R.id.list_dialog_select_sub_group);
        tvCancel=view.findViewById(R.id.tv_dialog_select_sub_group_cancel);
        confirm=view.findViewById(R.id.tv_dialog_select_sub_group_confirm);

        if(menuNames.size()>4){
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) listView.getLayoutParams();
            layoutParams.height= DensityUtil.dp2px(44)*4;
            listView.setLayoutParams(layoutParams);
        }
        tvCancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

        mAdapter = new MyAdapter();
        listView.setAdapter(mAdapter);


        if (!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        setContentView(view);

    }


    public void showDialog() {
        if (!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dialog_select_sub_group_cancel:
                dismiss();
                break;
            case R.id.tv_dialog_select_sub_group_confirm:
                if (mSubGroupOnClick!=null){
                    mSubGroupOnClick.onOkClick(select);
                }
                dismiss();
                break;

            default:
                break;
        }
    }

    private int select=0;
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menuNames.size();
        }

        @Override
        public Object getItem(int position) {
            return menuNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.pop_select_sub_group_item, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.tvName = convertView.findViewById(R.id.tv_name);
                holder.ivSelect = convertView.findViewById(R.id.iv_select);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(select==position){
                holder.ivSelect.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelect.setVisibility(View.GONE);
            }
            holder.tvName.setText(menuNames.get(position).getGroupName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select=position;
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
        class ViewHolder {
            TextView tvName;
            ImageView ivSelect;
        }
    }

    public SelectSubGroupListDialog setSubGroupOnClick(SubGroupOnClick subGroupOnClick) {
        this.mSubGroupOnClick = subGroupOnClick;
        return this;
    }

    public interface SubGroupOnClick {
        void onOkClick(int position);
    }
}
