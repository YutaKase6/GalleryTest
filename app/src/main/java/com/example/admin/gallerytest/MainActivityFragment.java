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

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;

/**
 * メインのFragment
 */
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
    @Bind(R.id.expand_LinearLayout)
    LinearLayout expandLinearLayout = null;
    /**
     * 拡大された画像を表示するImageView
     */
    @Bind(R.id.imageView)
    ImageView imageView;
    /**
     * 画像拡大時、画像下部にテキストを表示するTextView
     */
    @Bind(R.id.caption_textView)
    TextView captionTextView = null;

    /**
     * 現在の検索結果のタグを表示するTextView
     */
    @Bind(R.id.tag_textView)
    TextView tagTextView = null;

    /**
     * RecyclerViewのadapter
     * RecyclerViewの管理をする
     */
    private RecyclerViewAdapter recyclerViewAdapter = null;

    /**
     * グリッドの列の数
     */
    @BindInt(R.integer.num)
    public int columnNum;
    /**
     * アイテムをオートロードするための閾値
     * まだ表示していないアイテムの数が閾値以下になった時、ロードを開始する
     */
    @BindInt(R.integer.visible_threshold)
    public int visibleThreshold;

    /**
     * このアプリの名前
     */
    @BindString(R.string.app_name)
    public String appName;
    /**
     * 検索窓に表示しておく文字列
     * 入力のヒントとなる
     */
    @BindString(R.string.query_hint)
    public String queryHint;


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

        //Injection
        ButterKnife.bind(this, view);

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
        RecyclerView recyclerView = findById(getView(), R.id.recyclerview);
        //RecyclerViewをグリッド表示に
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnNum));
        //スクロール中にオートロードするためのリスナーを登録
        //オートロード関係の処理はEndlessScrollListenerに記述
        recyclerView.setOnScrollListener(new EndlessScrollListener((GridLayoutManager) recyclerView.getLayoutManager(), visibleThreshold) {
            @Override
            public void onLoadMore() {
                //ロード処理をここに記述

                //更新処理
                onRefresh();
            }
        });


        //Toolbarの設定
        Toolbar toolbar = findById(getView(), R.id.toolbar);
        //Toolbarのタイトル
        toolbar.setTitle(appName);
        //Toolbarのレイアウト
        toolbar.inflateMenu(R.menu.search);

        //検索窓の設定(SearchView)
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        //入力欄に、入力前に表示する文字を設定
        //入力欄にどんな内容を入力すべきかを暗示
        searchView.setQueryHint(queryHint);
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
        tagTextView.setText("#" + tag);

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
     * 画像拡大画面がタッチされた時の処理
     * アイテムを消す(レイアウトを非表示にする)
     */
    @OnClick(R.id.expand_LinearLayout)
    public void onClickExpandLinearLayout() {
        expandLinearLayout.setVisibility(View.INVISIBLE);
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
     * 与えられたidに対するあたらしいLoaderをインスタンス化し返す
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
            RecyclerView recyclerView = findById(getView(), R.id.recyclerview);
            setRecyclerViewAdapter(recyclerView);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Viewが破棄されるときに必ず呼ぶ
        ButterKnife.unbind(this);
    }


}
