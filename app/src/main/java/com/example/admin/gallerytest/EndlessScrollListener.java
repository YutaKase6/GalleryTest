package com.example.admin.gallerytest;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * スクロールでアイテムを自動取得するリスナー
 * Created by admin on 2015/07/08.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    /**
     * 現在画面に表示されている最初のアイテムのインデックス
     */
    int firstVisibleItem;
    /**
     * 現在画面に表示されているアイテムの数
     */
    int visibleItemCount;
    /**
     * アイテムの総数
     */
    int totalItemCount;
    /**
     * ロードを開始するしきい値
     * 見ていないアイテムがこの値より少なくなった時ロードを開始する
     */
    int visibleThreshold;

    /**
     * これまでのアイテムの総数
     * この値をアイテムの総数が超えたならロードが完了したことを意味するため、ロードを停止する
     */
    private int previousTotal = 0;
    /**
     * ロードの実行フラグ
     */
    private boolean loading = true;

    /**
     * RecyclerViewのレイアウトマネージャー
     * アイテムの総数等を取得する為等に使用する
     */
    private GridLayoutManager GridLayoutManager;

    public EndlessScrollListener(GridLayoutManager gridLayoutManager, int visibleThreshold) {
        this.GridLayoutManager = gridLayoutManager;
        this.visibleThreshold = visibleThreshold;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        //表示されているアイテムの数
        visibleItemCount = recyclerView.getChildCount();
        //RecyclerViewが持っているアイテムの総数
        totalItemCount = GridLayoutManager.getItemCount();
        //表示されているアイテムの最初のインデックス
        firstVisibleItem = GridLayoutManager.findFirstVisibleItemPosition();

        // RecyclerViewに含まれるアイテムが検索等の更新によって減った場合、これまでの合計を0に戻す
        if (totalItemCount < previousTotal) {
            previousTotal = 0;
        }

        //新たなアイテムが取得できたならロードを停止する
        if (loading) {
            //アイテムが増加した、すなわちロードに成功した
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        //見ていないアイテムがしきい値を下回った場合ロードを開始する
        //しきい値はres/values/Integer.xmlのvisible_threshold
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            onLoadMore();
            loading = true;
        }
    }

    public abstract void onLoadMore();

}
