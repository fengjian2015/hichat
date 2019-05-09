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
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.model.db.entity.FriendInfo;

import java.util.List;

/*
 *   author:jason
 *   date:2019/4/2715:15
 *   选择群成员
 */
public class SelectPersonListDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private View view;
    private ListView lv_menu;
    private TextView mTvAllMembers;
    private ImageView mIvcancel;

    private ListOnClick listOnClick;
    private List<FriendInfo> menunames;
    private MyAdapter mAdapter;

    public SelectPersonListDialog(@NonNull Context context, List<FriendInfo> menunames){
        super(context, R.style.dialog_common);
        this.context = context;
        this.menunames=menunames;
        init();
    }

    private void init() {
        view = View.inflate(context, R.layout.pop_select_person, null);
        lv_menu = view.findViewById(R.id.list);
        mTvAllMembers = view.findViewById(R.id.tv_all_members);
        mIvcancel = view.findViewById(R.id.tv_cancel);
        mIvcancel.setOnClickListener(this);
        mTvAllMembers.setOnClickListener(this);
        mAdapter = new MyAdapter();
        lv_menu.setAdapter(mAdapter);

        Window window = getWindow();
        window.setDimAmount(0f);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.y=DensityUtil.dp2px(40);
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        if (!ActivityUtil.isActivityOnTop(context)) return;
        window.setWindowAnimations(R.style.dialog_common);
        setContentView(view);

    }


    public void showDialog() {
        if (!ActivityUtil.isActivityOnTop(context)) return;
        show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_all_members:
                //选择全部
                if (listOnClick != null) {
                    listOnClick.allMember();
                }
                dismiss();
                break;
        }
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menunames.size();
        }

        @Override
        public Object getItem(int position) {
            return menunames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.pop_select_person_item, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.textView = convertView.findViewById(R.id.tv_name);
                holder.imageView=convertView.findViewById(R.id.iv_avatar);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImgUtil.load(context, menunames.get(position).getAvatar(), holder.imageView);
            holder.textView.setText(menunames.get(position).getUsername());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listOnClick != null) {
                        listOnClick.onClickItem(position);
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView textView;
            ImageView imageView;
        }
    }

    public SelectPersonListDialog setListOnClick(ListOnClick listOnClick) {
        this.listOnClick = listOnClick;
        return this;
    }

    public interface ListOnClick {
        void onClickItem(int position);
        void allMember();
    }
}
