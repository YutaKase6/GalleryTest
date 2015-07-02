package com.example.admin.gallerytest;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, SwipeRefreshLayout.OnRefreshListener {

    /**
     * 検索したいタグ
     */
//    private final String TAG = "iQon";
    private final String TAG = "photooftheday";


    private SwipeRefreshLayout swipeRefreshLayout = null;
    /**
     * 画像の情報を持ったクラスのリスト
     */
    private ImageInfoList imageList = new ImageInfoList("https://api.instagram.com/v1/tags/" + TAG + "/media/recent?client_id=8f159dc9bf334630a37fdf4e607044cb");

    /**
     * Instagram API 解析クラス
     */
    private ParseInstagramImage parseInstagramImage = new ParseInstagramImage(this.imageList);


    private RecyclerViewAdapter recyclerViewAdapter = null;

    /**
     * 拡大用ImageView
     */
    private ImageView imageView;


    public MainActivityFragment() {
    }

    /**
     * FragmentがView階層に関連付けられた時に呼ばれる
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return Fragmentで表示するView UIいらないならnull
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     * ActivityのonCreate()の直後に呼ばれる
     * Activity, Fragment 準備ok
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        this.swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.SwipeRefreshLayout);
        // プログレスアニメーションの色指定
        this.swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        // 更新リスナーの追加
        this.swipeRefreshLayout.setOnRefreshListener(MainActivityFragment.this);



        RecyclerView recyclerView = (RecyclerView)getView().findViewById(R.id.recyclerview);
        //グリッドビューっぽく
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),getResources().getInteger(R.integer.num)));


        imageView = (ImageView) getView().findViewById(R.id.imageView);
        //拡大アイテムタッチ処理(アイテムを非表示にする)
        getView().findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.INVISIBLE);
            }
        });

        //初回
        if (this.recyclerViewAdapter == null) {
            // アニメーション開始
            this.swipeRefreshLayout.setRefreshing(true);
            onRefresh(); // 更新処理
        }
        //画面回転
        else {
            setAdapter(recyclerView);
        }
    }

    private void setAdapter(View view) {
        this.recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),this.imageList.getImageInfoList(),imageView);
        ((RecyclerView) view).setAdapter(this.recyclerViewAdapter);

    }

    private void startLoader(int id) {
        getLoaderManager().restartLoader(id, null, MainActivityFragment.this);
    }

    /**
     * 与えられたIDに対する新しいLoaderをインスタンス化し返す
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        HttpAsyncTaskLoader loader = new HttpAsyncTaskLoader(getActivity(), this.imageList.getNextUrl());
        loader.forceLoad();
        return loader;
    }

    /**
     * loaderがロードを完了した時に呼ばれる
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null) return;

        this.parseInstagramImage.loadJson(data); // APIのレスポンスを解析する


        if (this.recyclerViewAdapter == null) {
            setAdapter(getView().findViewById(R.id.recyclerview));
        }

        this.recyclerViewAdapter.notifyDataSetChanged();
        this.swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    /**
     * 取得データの更新
     */
    @Override
    public void onRefresh() {
        startLoader(0);
    }
}
