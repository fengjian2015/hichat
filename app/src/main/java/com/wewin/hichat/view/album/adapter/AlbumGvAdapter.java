package com.wewin.hichat.view.album.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wewin.hichat.R;
import com.wewin.hichat.model.db.entity.ImgUrl;

import java.util.List;

/**
 * Created by Darren on 2017/7/5.
 */

public class AlbumGvAdapter extends BaseAdapter {

    private Context context;
    private List<ImgUrl> imgList;
    private OnImgClickListener mListener;
    private OnCbClickListener cbListener;
    private int maxImgNum;
    private ViewHolder holder;
    private int albumOpenCode;

    public AlbumGvAdapter(Context context, List<ImgUrl> imgList, int maxImgNum, int albumOpenCode) {
        this.context = context;
        this.imgList = imgList;
        this.maxImgNum = maxImgNum;
        this.albumOpenCode = albumOpenCode;
    }

    public interface OnImgClickListener {
        void onClick(int position);
    }

    public void setOnImgClickListener(OnImgClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCbClickListener {
        void onCbClick(View v, int position);
    }

    public void setOnCbClickListener(OnCbClickListener cbListener) {
        this.cbListener = cbListener;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_album_multi_choice, null);
            holder = new ViewHolder();
            holder.itemImg = convertView.findViewById(R.id.id_item_image);
            holder.selectCb = convertView.findViewById(R.id.id_item_select);
            holder.alphaIv = convertView.findViewById(R.id.iv_alpha_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(context).load(imgList.get(position).getUrl()).into(holder.itemImg);

        if (albumOpenCode == -1) {
            holder.selectCb.setVisibility(View.VISIBLE);
        } else {
            holder.selectCb.setVisibility(View.GONE);
        }

        /*if (InfoCons.imgPathList.contains(imgList.get(position))) {
            holder.selectCb.setChecked(true);
        } else {
            holder.selectCb.setChecked(false);
        }*/

        holder.selectCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbListener.onCbClick(v, position);
            }
        });

        holder.itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(position);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView itemImg;
        CheckBox selectCb;
        ImageView alphaIv;
    }

}
