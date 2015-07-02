package com.example.admin.gallerytest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 2015/07/02.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    /**
     * 画像情報のリスト
     */
    private List<ImageInfo> urls;
    /**
     * 拡大画像ImageView
     */
    private ImageView standardImageView;

    private Context context;

    public RecyclerViewAdapter(Context context, List<ImageInfo> urls, ImageView iv) {
        super();
        this.context = context;
        this.urls = urls;
        this.standardImageView = iv;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_grid, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (urls.size() > 0) {
            final int position = i;
            // ビューに表示する画像のURLを設定し表示
            Picasso.with(this.context).load(urls.get(i).getThumbnail()).placeholder(R.drawable.loadingimage).into(viewHolder.squaredImageView);

            // 画像がタッチされた時の処理
            // タッチされた画像を拡大し、表示する
            // (すでに用意してある拡大画像用imageViewに画像をセットし表示をVisibleに変更する)
            // ここに書くのはよくないかも…
            viewHolder.squaredImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStandardImage(position,standardImageView);
                    standardImageView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    /**
     * 標準サイズの画像を設定し表示する
     *
     * @param position index
     * @param view
     */
    public void setStandardImage(int position, ImageView view) {
                String url = urls.get(position).getStandard();
                Picasso.with(this.context).load(url).placeholder(R.drawable.loadingimage).into(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SquaredImageView squaredImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            squaredImageView = (SquaredImageView) itemView.findViewById(R.id.squaredImageView);
        }
    }

}
