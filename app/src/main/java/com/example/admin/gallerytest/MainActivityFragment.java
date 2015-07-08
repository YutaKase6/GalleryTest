package com.example.admin.gallerytest;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    /**
     * 設定関係を保持しているクラス
     * ※現在はEntry Pointの情報を持っているのみ
     */
    MyConfig myConfig = new MyConfig();

    /**
     * 検索に使用されるタグ
     * 初期値として#iQON
     */
    private String tag = "iQON";
    /**
     * 画像情報のリスト
     */
    private ImageInfoList imageList = new ImageInfoList(myConfig.GenerateTagSearchEntryPoint(tag));
    /**
     * Instagram API 解析クラス
     */
    private ParseInstagramImage parseInstagramImage = new ParseInstagramImage(this.imageList);

    /**
     * 画像拡大画面で使用されるレイアウト
     */
    private LinearLayout expandLinearLayout = null;
    /**
     * 拡大された画像を表示するImageView
     */
    private ImageView imageView;
    /**
     * 画像拡大時、画像下部にテキストを表示するTextView
     */
    private TextView captionTextView = null;

    /**
     * 現在の検索結果のタグを表示するTextView
     */
    private TextView tagTextView = null;

    /**
     * RecyclerViewのadapter
     * RecyclerViewの管理をする
     */
    private RecyclerViewAdapter recyclerViewAdapter = null;


    /**
     * FragmentがView階層に関連付けられた時に呼ばれる
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState Bundle
     * @return Fragmentで表示するView UIいらないならnull
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Fragmentで表示するView
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        //Back keyの設定
        //FragmentのViewにOnKeyListenerを登録
        //実際の処理はonPressBackKey()に記述
        //Activity側からgetViewして設定するか迷い
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //ボタンを押したときのみ実行(ボタンを話した時は無視)
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                //Back Key
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //処理メソッドを呼び出し
                    onPressBackKey();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    /**
     * Back Keyが押された時に呼ばれるメソッド
     */
    private void onPressBackKey() {
        // 画像が拡大表示されているならば、それを非表示にする
        if (expandLinearLayout != null && expandLinearLayout.getVisibility() == View.VISIBLE) {
            expandLinearLayout.setVisibility(View.INVISIBLE);
        }
        // 通常状態
        // Activityを終了する
        else {
            getActivity().finish();
        }

    }

    /**
     * ActivityのonCreate()の直後に呼ばれる
     * Activity, Fragment 準備ok
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);


        //RecyclerViewの設定
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        //RecyclerViewをグリッド表示に
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.num)));
        //スクロール中にオートロードするためのリスナーを登録
        //オートロード関係の処理はEndlessScrollListenerに記述
        recyclerView.setOnScrollListener(new EndlessScrollListener((GridLayoutManager) recyclerView.getLayoutManager(), getResources().getInteger(R.integer.visible_threshold)) {
            @Override
            public void onLoadMore() {
                //ロード処理をここに記述

                //更新処理
                onRefresh();
            }
        });


        //Toolbarの設定
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        //Toolbarのタイトル
        toolbar.setTitle(getString(R.string.app_name));
        //Toolbarのレイアウト
        toolbar.inflateMenu(R.menu.search);

        //検索窓の設定(SearchView)
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        //入力欄に、入力前に表示する文字を設定
        //入力欄にどんな内容を入力すべきかを暗示
        searchView.setQueryHint(getString(R.string.query_hint));
        //検索ボタンが押された時のリスナーを登録
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //検索ボタンが押された時の処理を記述
                search(query);
                return false;
            }

            //テキストが入力される度呼ばれる
            //今回は使わない
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //検索結果のタグを設定
        tagTextView = (TextView) getView().findViewById(R.id.tag_textView);
        tagTextView.setText("#" + tag);

        //拡大表示用レイアウトのタッチ処理
        //レイアウトを非表示にする
        expandLinearLayout = (LinearLayout) getView().findViewById(R.id.expand_LinearLayout);
        expandLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandLinearLayout.setVisibility(View.INVISIBLE);
            }
        });

        //拡大画面用のViewのIDを取得
        imageView = (ImageView) getView().findViewById(R.id.imageView);
        captionTextView = (TextView) getView().findViewById(R.id.caption_textView);

        //初回
        if (this.recyclerViewAdapter == null) {
            //更新処理
            onRefresh();
        }
        //画面回転
        else {
            setRecyclerViewAdapter(recyclerView);
        }
    }

    /**
     * RecyclerViewにadapterをセットする
     *
     * @param recyclerView セットするRecyclerView
     */
    private void setRecyclerViewAdapter(RecyclerView recyclerView) {
        this.recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), this.imageList.getImageInfoList(),
                imageView, expandLinearLayout, captionTextView);
        recyclerView.setAdapter(this.recyclerViewAdapter);

    }

    /**
     * 与えられたidのLoaderを呼び出す
     *
     * @param id Loaderのid
     */
    private void startLoader(int id) {
        getLoaderManager().restartLoader(id, null, MainActivityFragment.this);
    }

    /**
     * 与えられたいｄに対するあたらしいLoaderをインスタンス化し返す
     *
     * @param id   Loaderのid
     * @param args Bundle
     * @return Loader
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
     * @param loader Loader
     * @param data   取得したデータ
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null) return;

        //APIのレスポンスを解析する
        this.parseInstagramImage.loadJson(data);
        //アダプターをセット
        if (this.recyclerViewAdapter == null) {
            setRecyclerViewAdapter((RecyclerView) getView().findViewById(R.id.recyclerview));
        }

        //データセットの変更を通知
        this.recyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    /**
     * 取得データの更新
     */
    public void onRefresh() {
        startLoader(0);
    }

    /**
     * 検索ルーチン
     * これまでの画像情報をクリア
     * 新しいタグでリクエストを送信
     *
     * @param query 入力されたクエリ
     */
    public void search(String query) {
        tag = query;
        imageList.clear();
        //検索結果の表示を変更
        tagTextView.setText("#" + tag);
        //リクエストURLを設定
        imageList.setNextUrl(myConfig.GenerateTagSearchEntryPoint(tag));
        //更新
        onRefresh();
    }


}
