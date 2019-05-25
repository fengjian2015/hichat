package com.wewin.hichat.androidlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import com.wewin.hichat.R;
import java.lang.reflect.Field;

/**
 * 支持gif的圆角ImageView
 * Created by
 * Description
 */
public class CornerGifView extends AppCompatImageView {

    private Path mPath;
    private Paint mPaint;
    private int mCornerSize;
    private int mLeftBottomCorner;
    private int mLeftTopCorner;
    private int mRightBottomCorner;
    private int mRightTopCorner;
    private float[] mCornerArr;

    public CornerGifView(Context context) {
        this(context, null);
    }

    public CornerGifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerGifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CornerGifView);
        //切圆角用的背景色
        int frameColor = ta.getColor(R.styleable.CornerGifView_frameColor, 0xfff1f1f1);
        mCornerSize = (int) ta.getDimension(R.styleable.CornerGifView_cornerSize, 0);
        mLeftBottomCorner = (int) ta.getDimension(R.styleable.CornerGifView_leftBottomCorner, 0);
        mLeftTopCorner = (int) ta.getDimension(R.styleable.CornerGifView_leftTopCorner, 0);
        mRightBottomCorner = (int) ta.getDimension(R.styleable.CornerGifView_rightBottomCorner, 0);
        mRightTopCorner = (int) ta.getDimension(R.styleable.CornerGifView_rightTopCorner, 0);
        ta.recycle();

        if (mCornerSize == 0) {
            mCornerArr = new float[]{
                    dp2px(mLeftTopCorner), dp2px(mLeftTopCorner),
                    dp2px(mRightTopCorner), dp2px(mRightTopCorner),
                    dp2px(mRightBottomCorner), dp2px(mRightBottomCorner),
                    dp2px(mLeftBottomCorner), dp2px(mLeftBottomCorner)};
        } else {
            mCornerArr = new float[]{
                    dp2px(mCornerSize), dp2px(mCornerSize),
                    dp2px(mCornerSize), dp2px(mCornerSize),
                    dp2px(mCornerSize), dp2px(mCornerSize),
                    dp2px(mCornerSize), dp2px(mCornerSize)};
        }

        mPath = new Path();
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(frameColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        addRoundRectPath(w, h);
    }

    private void addRoundRectPath(int w, int h) {
        mPath.reset();
        mPath.addRoundRect(new RectF(0, 0, w, h), mCornerArr, Path.Direction.CCW);
    }

    private void setCornerSize(int leftTop, int leftBottom, int rightTop, int rightBottom) {
        mCornerArr = new float[]{
                leftTop, leftTop,
                rightTop, rightTop,
                rightBottom, rightBottom,
                leftBottom, leftBottom};
        addRoundRectPath(getWidth(), getHeight());
        invalidate();
    }

    public int getCornerSize() {
        return mCornerSize;
    }

    public void setCornerSize(int cornerSize) {
        mCornerSize = cornerSize;
        setCornerSize(cornerSize, cornerSize, cornerSize, cornerSize);
    }

    public int getLeftBottomCorner() {
        return mLeftBottomCorner;
    }

    public void setLeftBottomCorner(int leftBottomCorner) {
        mLeftBottomCorner = leftBottomCorner;
        setCornerSize(mLeftTopCorner, mLeftBottomCorner, mRightTopCorner, mRightBottomCorner);
    }

    public int getLeftTopCorner() {
        return mLeftTopCorner;
    }

    public void setLeftTopCorner(int leftTopCorner) {
        mLeftTopCorner = leftTopCorner;
        setCornerSize(mLeftTopCorner, mLeftBottomCorner, mRightTopCorner, mRightBottomCorner);
    }

    public int getRightBottomCorner() {
        return mRightBottomCorner;
    }

    public void setRightBottomCorner(int rightBottomCorner) {
        mRightBottomCorner = rightBottomCorner;
        setCornerSize(mLeftTopCorner, mLeftBottomCorner, mRightTopCorner, mRightBottomCorner);
    }

    public int getRightTopCorner() {
        return mRightTopCorner;
    }

    public void setRightTopCorner(int rightTopCorner) {
        mRightTopCorner = rightTopCorner;
        setCornerSize(mLeftTopCorner, mLeftBottomCorner, mRightTopCorner, mRightBottomCorner);
    }

    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
