package com.example.admin.gallerytest;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, SwipeRefreshLayout.OnRefreshListener {

    /**
     * 初期タグ
     */
    private String tag = "iQon";

    /**
     * 画像の情報を持ったクラスのリスト
     */
    private ImageInfoList imageList = new ImageInfoList(generateUrl(tag));
    /**
     * Instagram API 解析クラス
     */
    private ParseInstagramImage parseInstagramImage = new ParseInstagramImage(this.imageList);
    /**
     * 画像拡大画面時のレイアウト
     */
    private LinearLayout expandLinearLayout = null;

    /**
     * 拡大画像用ImageView
     */
    private ImageView imageView;

    /**
     * 拡大用説明文TextView
     */
    private TextView captionTextView = null;


    private SwipeRefreshLayout swipeRefreshLayout = null;

    private RecyclerViewAdapter recyclerViewAdapter = null;

    /**
     * tag表示用TextView
     */
    private TextView tagTextView = null;

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
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.num)));


        Toolbar toolbar = (Toolbar)getView().findViewById(R.id.toolbar);
        toolbar.setTitle("Instagram Gallery");
        toolbar.inflateMenu(R.menu.search);


        SearchView searchView = (SearchView)toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        searchView.setQueryHint("tag");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //検索ルーチン
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //現在表示している画像のタグ表示
        tagTextView = (TextView)getView().findViewById(R.id.tag_textView);
        tagTextView.setText("#" + tag);

        //拡大表示時のレイアウト
        expandLinearLayout = (LinearLayout)getView().findViewById(R.id.expand_LinearLayout);
        //拡大アイテムタッチ処理(アイテムを非表示にする)
        expandLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandLinearLayout.setVisibility(View.INVISIBLE);
            }
        });
        imageView = (ImageView)getView().findViewById(R.id.imageView);

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
        this.recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),this.imageList.getImageInfoList(),imageView,expandLinearLayout,(TextView)getView().findViewById(R.id.caption_textView));
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

        // APIのレスポンスを解析する
        this.parseInstagramImage.loadJson(data);

        if (this.recyclerViewAdapter == null) {
            setAdapter(getView().findViewById(R.id.recyclerview));
        }

        //データセットの変更を通知
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

    /**
     * タグからリクエストURLを生成
     * @param tag
     * @return
     */
    public String generateUrl(String tag){
        return "https://api.instagram.com/v1/tags/" + tag + "/media/recent?client_id=8f159dc9bf334630a37fdf4e607044cb";
    }

    /**
     * 検索ルーチン
     * これまでの画像情報リストをクリア
     * 新しいタグでリクエストを送信
     * @param query
     */
    public void search(String query){
        swipeRefreshLayout.setRefreshing(true);//更新アニメーションスタート
        tag = query;
        tagTextView.setText("#"+tag);//現在のタグを表示するテキストビューを更新
        imageList.clear();//リストのクリア
        imageList.setNextUrl(generateUrl(tag));
        onRefresh();
    }

}
