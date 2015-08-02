package com.example.admin.gallerytest;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;


/**
 * RecyclerViewのAdapterクラス
 * Created by admin on 2015/07/02.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    /**
     * 画像情報のリスト
     */
    private List<ImageInfo> urls;
    /**
     * 拡大画面用レイアウト
     */
    private LinearLayout expandLinearLayout;
    /**
     * 拡大画像用ImageView
     */
    private ImageView standardImageView;
    /**
     * 拡大画面時、画像の説明文用TextView
     */
    private TextView textView;

    private Context context;

    /**
     * アイテムロード中に表示する画像
     */
    @BindDrawable(R.drawable.loadingimage)
    public Drawable loadingImage;


    public RecyclerViewAdapter(Context context, List<ImageInfo> urls, ImageView iv, LinearLayout expandLinearLayout, TextView textView) {
        super();
        this.context = context;
        this.urls = urls;
        this.standardImageView = iv;
        this.expandLinearLayout = expandLinearLayout;
        this.textView = textView;
        //bind 引数これで合ってるか不明(動きはする)
        ButterKnife.bind(this,(Activity)this.context);
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
            // Viewに画像を表示
            Picasso.with(this.context).load(urls.get(i).getThumbnail()).placeholder(loadingImage).into(viewHolder.squaredImageView);

            // 画像がタッチされた時の処理
            // 画像を拡大表示(拡大画面用レイアウトを表示)
            // ここに書くのは良くない？
            viewHolder.squaredImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStandardImage(position, standardImageView);
                    expandLinearLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * RecyclerViewに含まれているアイテムの数を返す
     *
     * @return RecyclerViewに含まれているアイテムの数
     */
    @Override
    public int getItemCount() {
        return urls.size();
    }

    /**
     * 標準解像度の画像を取得し、表示
     * ついでに画像の説明も取得し、表示
     *
     * @param position index
     * @param view     画像をセットするView
     */
    public void setStandardImage(int position, ImageView view) {
        String standardImageUrl = urls.get(position).getStandard();
        Picasso.with(this.context).load(standardImageUrl).placeholder(loadingImage).into(view);
        textView.setText(urls.get(position).getText());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * サムネイル用imageView
         */
        @Bind(R.id.squaredImageView)
        SquaredImageView squaredImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
