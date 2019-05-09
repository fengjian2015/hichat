package com.wewin.hichat.view.album.model;

import android.graphics.Bitmap;

import com.wewin.hichat.view.album.utils.Bimp;

import java.io.IOException;

/**
 * Created by Darren on 15/10/10.
 */
public class ImageBean {
    public String id;
    public String path;
    private Bitmap bitmap;
    public String thumbnailPath;
    private int sourceId;

    public ImageBean() {
    }

    public ImageBean(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ImageBean(int sourceId) {
        this.sourceId = sourceId;
    }

    public ImageBean(String id, String path, Bitmap bitmap, String thumbnailPath) {
        this.id = id;
        this.path = path;
        this.bitmap = bitmap;
        this.thumbnailPath = thumbnailPath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        if(bitmap == null){
            try {
                bitmap = Bimp.revitionImageSize(path);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
}
