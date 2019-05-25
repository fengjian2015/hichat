package com.wewin.hichat.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.model.db.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/1/7.
 */
public class MessageFileRcvAdapter extends BaseRcvAdapter {

    private Context context;
    private List<FileInfo> fileInfoList;
    private List<FileInfo> selectList = new ArrayList<>();
    private OnSelectListener selectListener;

    public MessageFileRcvAdapter(Context context, List<FileInfo> fileInfoList) {
        this.context = context;
        this.fileInfoList = fileInfoList;
    }

    public interface OnSelectListener {
        void select(List<FileInfo> fileList);
    }

    public void setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return fileInfoList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        return new ItemViewHolder(View.inflate(context, R.layout.item_message_chat_file_document, null));
    }

    @Override
    protected void bindViewData(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder && !fileInfoList.isEmpty()) {
            final ItemViewHolder iHolder = (ItemViewHolder) holder;
            iHolder.filenameTv.setText(fileInfoList.get(position).getFileName());
            iHolder.detailTv.setText(FileUtil.formatFileLength(fileInfoList.get(position).getFileLength())
                    + " " + TimeUtil.timestampToStr(fileInfoList.get(position).getCreateTime()));

            String fileName = fileInfoList.get(position).getFileName();

            if (!TextUtils.isEmpty(fileName)) {
                if (fileName.endsWith(".txt")) {
                    iHolder.iconIv.setImageResource(R.drawable.icon_txt);
                } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                    iHolder.iconIv.setImageResource(R.drawable.icon_word);
                } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    iHolder.iconIv.setImageResource(R.drawable.icon_excel);
                } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
                    iHolder.iconIv.setImageResource(R.drawable.icon_ppt);
                } else if (fileName.endsWith(".pdf")) {
                    iHolder.iconIv.setImageResource(R.drawable.icon_pdf);
                } else if (fileName.endsWith(".mp3") || fileName.endsWith(".wma")) {
                    iHolder.iconIv.setImageResource(R.drawable.icon_music);
                } else if (fileName.endsWith(".mp4")) {
                    ImgUtil.load(context, fileInfoList.get(position).getOriginPath(), iHolder.iconIv);
                }
            }

            iHolder.checkIv.setSelected(fileInfoList.get(position).isChecked());

            iHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iHolder.checkIv.setSelected(!iHolder.checkIv.isSelected());
                    if (iHolder.checkIv.isSelected()) {
                        if (selectList.size() < 9) {
                            selectList.add(fileInfoList.get(position));
                        } else {
                            iHolder.checkIv.setSelected(false);
                            ToastUtil.showShort(context, R.string.max_select_size);
                        }
                    } else {
                        selectList.remove(fileInfoList.get(position));
                    }
                    if (selectList != null){
                        selectListener.select(selectList);
                    }
                }
            });
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView filenameTv, detailTv;
        private ImageView iconIv, checkIv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            checkIv = itemView.findViewById(R.id.iv_message_chat_file_document_item_check);
            filenameTv = itemView.findViewById(R.id.tv_message_chat_file_document_item_name);
            detailTv = itemView.findViewById(R.id.tv_message_chat_file_document_item_detail);
            iconIv = itemView.findViewById(R.id.iv_message_chat_file_document_item_icon);
        }
    }


}
