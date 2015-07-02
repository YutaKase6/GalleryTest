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
     * �����������^�O
     */
//    private final String TAG = "iQon";
    private final String TAG = "photooftheday";


    private SwipeRefreshLayout swipeRefreshLayout = null;
    /**
     * �摜�̏����������N���X�̃��X�g
     */
    private ImageInfoList imageList = new ImageInfoList("https://api.instagram.com/v1/tags/" + TAG + "/media/recent?client_id=8f159dc9bf334630a37fdf4e607044cb");

    /**
     * Instagram API ��̓N���X
     */
    private ParseInstagramImage parseInstagramImage = new ParseInstagramImage(this.imageList);


    private RecyclerViewAdapter recyclerViewAdapter = null;

    /**
     * �g��pImageView
     */
    private ImageView imageView;


    public MainActivityFragment() {
    }

    /**
     * Fragment��View�K�w�Ɋ֘A�t����ꂽ���ɌĂ΂��
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return Fragment�ŕ\������View UI����Ȃ��Ȃ�null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     * Activity��onCreate()�̒���ɌĂ΂��
     * Activity, Fragment ����ok
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        this.swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.SwipeRefreshLayout);
        // �v���O���X�A�j���[�V�����̐F�w��
        this.swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        // �X�V���X�i�[�̒ǉ�
        this.swipeRefreshLayout.setOnRefreshListener(MainActivityFragment.this);



        RecyclerView recyclerView = (RecyclerView)getView().findViewById(R.id.recyclerview);
        //�O���b�h�r���[���ۂ�
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),getResources().getInteger(R.integer.num)));


        imageView = (ImageView) getView().findViewById(R.id.imageView);
        //�g��A�C�e���^�b�`����(�A�C�e�����\���ɂ���)
        getView().findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.INVISIBLE);
            }
        });

        //����
        if (this.recyclerViewAdapter == null) {
            // �A�j���[�V�����J�n
            this.swipeRefreshLayout.setRefreshing(true);
            onRefresh(); // �X�V����
        }
        //��ʉ�]
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
     * �^����ꂽID�ɑ΂���V����Loader���C���X�^���X�����Ԃ�
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
     * loader�����[�h�������������ɌĂ΂��
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null) return;

        this.parseInstagramImage.loadJson(data); // API�̃��X�|���X����͂���


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
     * �擾�f�[�^�̍X�V
     */
    @Override
    public void onRefresh() {
        startLoader(0);
    }
}
