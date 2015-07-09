package com.example.admin.gallerytest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 正方形のimageView(画像の高さを幅と同じにする)
 * ImageViewの拡張クラス
 * Created by admin on 2015/06/28.
 */
public class SquaredImageView extends ImageView {
    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // ビューの高さを横幅と同じにする
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }


}
