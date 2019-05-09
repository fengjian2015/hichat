package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.SystemUtil;

import java.util.List;

/**
 * Created by Darren on 2019/2/6
 */
public class LoginRegisterVerifyCodeRcvAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> numList;

    public LoginRegisterVerifyCodeRcvAdapter(Context context, List<String> numList) {
        this.context = context;
        this.numList = numList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_login_register_verify_code, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder && numList != null) {
            ItemViewHolder iHolder = (ItemViewHolder) holder;
            if (position < numList.size()) {
                iHolder.divideTv.setVisibility(View.INVISIBLE);
                iHolder.numTv.setVisibility(View.VISIBLE);
                iHolder.numTv.setText(numList.get(position));
            } else {
                iHolder.divideTv.setVisibility(View.VISIBLE);
                iHolder.numTv.setVisibility(View.INVISIBLE);
            }

            if (position == -1) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iHolder.divideTv.getLayoutParams();
                params.rightMargin = SystemUtil.dp2px(context, 11);
                iHolder.divideTv.setLayoutParams(params);
            } else if (position == 5) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iHolder.divideTv.getLayoutParams();
                params.rightMargin = 0;
                iHolder.divideTv.setLayoutParams(params);
            } else {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iHolder.divideTv.getLayoutParams();
                params.rightMargin = SystemUtil.dp2px(context, 5);
                iHolder.divideTv.setLayoutParams(params);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView divideTv, numTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            divideTv = itemView.findViewById(R.id.tv_login_register_verify_code_divide);
            numTv = itemView.findViewById(R.id.tv_login_register_verify_code_num);
        }
    }
}
