package com.example.admin.gallerytest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 2015/06/28.
 */
public class GridViewAdapter extends BaseAdapter {
    /**
     * 画像情報のリスト
     */
    private List<ImageInfo> urls;
    private Context context;

    public GridViewAdapter(Context context, List<ImageInfo> urls) {
        this.urls = urls;
        this.context = context;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        // サムネイル画像のURLを返す
        if(urls.size() == 0){
            return null;
        }else{
            return urls.get(position).getThumbnail();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;
        // 初回
        if (view == null) {
            view = new SquaredImageView(context);
        }
        // サムネイル画像のURLを取得
        String url = (String)getItem(position);

        // ビューに表示する画像のURLを設定
        Picasso.with(this.context).load(url).placeholder(R.drawable.loadingimage).into(view);
        return view;
    }

    /**
     * 標準サイズの画像を設定する
     * @param position
     * @param view
     */
    public void setStandardImage(int position, ImageView view) {
        if (urls.size() > 0){
            String url =urls.get(position).getStandard();
            Picasso.with(this.context).load(url).placeholder(R.drawable.loadingimage).into(view);
        }
    }
}

