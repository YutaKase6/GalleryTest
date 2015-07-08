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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, SwipeRefreshLayout.OnRefreshListener {
    /**
     * 設定関係を保持しているクラス
     * 現在はEntry Point の情報を持っているのみ
     */
    MyConfig myConfig = new MyConfig();

    /**
     * 初期タグ
     */
    private String tag = "iQON";
    /**
     * 画像の情報を持ったクラスのリスト
     */
    private ImageInfoList imageList = new ImageInfoList(myConfig.GenerateTagSearchEntryPoint(tag));
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
    /**
     * 通常時SwipeRefreshLayout
     */
    private SwipeRefreshLayout swipeRefreshLayout = null;
    /**
     * 現在のtag表示用TextView
     */
    private TextView tagTextView = null;

    /**
     * RecyclerViewのadapter
     */
    private RecyclerViewAdapter recyclerViewAdapter = null;


    public MainActivityFragment() {
    }

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
        //処理はonPressBackKey()に記述
        //Activity側からgetViewして設定したほうがわかりやすい？
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //ACTION_DOWNのみ実行(ACTION_UPは無視)
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                //Back Key
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onPressBackKey();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    /**
     * Back Keyが押されたときに呼ばれる
     */
    private void onPressBackKey() {
        //画像が拡大されているならそれを消す(INVISIVLEに変更)
        if (expandLinearLayout != null && expandLinearLayout.getVisibility() == View.VISIBLE) {
            expandLinearLayout.setVisibility(View.INVISIBLE);
        }
        //拡大されていないならActivityを終了する
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

        //SwipeRefreshLayoutのID取得
        this.swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.SwipeRefreshLayout);
        // プログレスアニメーションの色指定
        this.swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        // 更新リスナーの追加
        this.swipeRefreshLayout.setOnRefreshListener(MainActivityFragment.this);

        //RecyclerViewのID取得
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        //グリッドビューっぽく
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.num)));

        //ToolbarのID取得
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        //Toolbarに表示するタイトルをセット
        toolbar.setTitle(getString(R.string.app_name));
        //xmlからレイアウトを挿入
        toolbar.inflateMenu(R.menu.search);

        //SearchView(検索窓のようなView)
        //SearchViewのID取得
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        //入力欄に表示する文字(何を入力すべきかを暗示させる)
        searchView.setQueryHint(getString(R.string.query_hint));
        //検索実行時のリスナーを登録
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //検索ボタンが押されたときに呼ばれる
            @Override
            public boolean onQueryTextSubmit(String query) {
                //検索ルーチン
                search(query);
                return false;
            }

            //入力テキストが変更するたびに呼ばれる
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //現在表示している画像のタグを表示するView
        tagTextView = (TextView) getView().findViewById(R.id.tag_textView);
        tagTextView.setText("#" + tag);

        //拡大表示時のレイアウト
        expandLinearLayout = (LinearLayout) getView().findViewById(R.id.expand_LinearLayout);
        //拡大アイテムタッチ処理(アイテムを非表示にする)
        expandLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandLinearLayout.setVisibility(View.INVISIBLE);
            }
        });

        //拡大時用のImageViewのID取得
        imageView = (ImageView) getView().findViewById(R.id.imageView);
        //拡大時用のTextViewのID取得
        captionTextView = (TextView) getView().findViewById(R.id.caption_textView);

        //初回
        if (this.recyclerViewAdapter == null) {
            // アニメーション開始
            this.swipeRefreshLayout.setRefreshing(true);
            onRefresh(); // 更新処理
        }
        //画面回転
        else {
            setRecyclerViewAdapter(recyclerView);
        }
    }

    /**
     * 引数のviewにrecyclerViewAdapterをセットする
     *
     * @param recyclerView セットするRecyclerView
     */
    private void setRecyclerViewAdapter(RecyclerView recyclerView) {
        //Adapterの生成
        this.recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), this.imageList.getImageInfoList(),
                imageView, expandLinearLayout, captionTextView);
        //Viewにadapterをセット
        recyclerView.setAdapter(this.recyclerViewAdapter);

    }

    /**
     * あたえられたidのLoaderを再び呼び出す
     *
     * @param id Loaderのid
     */
    private void startLoader(int id) {
        getLoaderManager().restartLoader(id, null, MainActivityFragment.this);
    }

    /**
     * 与えられたIDに対する新しいLoaderをインスタンス化し返す
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

        // APIのレスポンスを解析する
        this.parseInstagramImage.loadJson(data);
        //アダプターをセット
        if (this.recyclerViewAdapter == null) {
            setRecyclerViewAdapter((RecyclerView) getView().findViewById(R.id.recyclerview));
        }

        //データセットの変更を通知
        this.recyclerViewAdapter.notifyDataSetChanged();
        //更新アニメーションの停止
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
     * 検索ルーチン
     * これまでの画像情報リストをクリア
     * 新しいタグでリクエストを送信
     *
     * @param query 入力されたクエリ
     */
    public void search(String query) {
        //更新アニメーションスタート
        swipeRefreshLayout.setRefreshing(true);
        tag = query;
        //現在のタグを表示するテキストビューを更新
        tagTextView.setText("#" + tag);
        //画像リストのクリア
        imageList.clear();
        //リクエストURLのセット
        imageList.setNextUrl(myConfig.GenerateTagSearchEntryPoint(tag));
        //更新処理
        onRefresh();
    }
}
