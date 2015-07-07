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
     * �����^�O
     */
    private String tag = "iQON";
    /**
     * �摜�̏����������N���X�̃��X�g
     */
    private ImageInfoList imageList = new ImageInfoList(generateUrl(tag));
    /**
     * Instagram API ��̓N���X
     */
    private ParseInstagramImage parseInstagramImage = new ParseInstagramImage(this.imageList);
    /**
     * �摜�g���ʎ��̃��C�A�E�g
     */
    private LinearLayout expandLinearLayout = null;
    /**
     * �g��摜�pImageView
     */
    private ImageView imageView;
    /**
     * �g��p������TextView
     */
    private TextView captionTextView = null;
    /**
     * �ʏ펞SwipeRefreshLayout
     */
    private SwipeRefreshLayout swipeRefreshLayout = null;
    /**
     * RecyclerView��adapter
     */
    private RecyclerViewAdapter recyclerViewAdapter = null;
    /**
     * ���݂�tag�\���pTextView
     */
    private TextView tagTextView = null;

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

        //SwipeRefreshLayout��ID�擾
        this.swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.SwipeRefreshLayout);
        // �v���O���X�A�j���[�V�����̐F�w��
        this.swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        // �X�V���X�i�[�̒ǉ�
        this.swipeRefreshLayout.setOnRefreshListener(MainActivityFragment.this);

        //RecyclerView��ID�擾
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        //�O���b�h�r���[���ۂ�
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.num)));

        //Toolbar��ID�擾
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        //Toolbar�ɕ\������^�C�g�����Z�b�g
//        toolbar.setTitle(R.string.app_name);
        //xml���烌�C�A�E�g��}��
        toolbar.inflateMenu(R.menu.search);

        //SearchView(�������̂悤��View)
        //SearchView��ID�擾
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        //���͗��ɕ\�����镶��(������͂��ׂ������Î�������)
        searchView.setQueryHint("tag");
        //�������s���̃��X�i�[��o�^
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //�����{�^���������ꂽ�Ƃ��ɌĂ΂��
            @Override
            public boolean onQueryTextSubmit(String query) {
                //�������[�`��
                search(query);
                return false;
            }
            //���̓e�L�X�g���ύX���邽�тɌĂ΂��
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //���ݕ\�����Ă���摜�̃^�O�\��
        tagTextView = (TextView) getView().findViewById(R.id.tag_textView);
        tagTextView.setText("#" + tag);

        //�g��\�����̃��C�A�E�g
        expandLinearLayout = (LinearLayout) getView().findViewById(R.id.expand_LinearLayout);
        //�g��A�C�e���^�b�`����(�A�C�e�����\���ɂ���)
        expandLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandLinearLayout.setVisibility(View.INVISIBLE);
            }
        });

        //�g�厞�p��ImageView��ID�擾
        imageView = (ImageView) getView().findViewById(R.id.imageView);
        //�g�厞�p��TextView��ID�擾
        captionTextView = (TextView) getView().findViewById(R.id.caption_textView);

        //����
        if (this.recyclerViewAdapter == null) {
            // �A�j���[�V�����J�n
            this.swipeRefreshLayout.setRefreshing(true);
            onRefresh(); // �X�V����
        }
        //��ʉ�]
        else {
            setRecyclerViewAdapter(recyclerView);
        }
    }

    /**
     * ������view��recyclerViewAdapter���Z�b�g����
     * @param recyclerView �Z�b�g����RecyclerView
     */
    private void setRecyclerViewAdapter(RecyclerView recyclerView) {
        this.recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), this.imageList.getImageInfoList(),
                imageView, expandLinearLayout, captionTextView);
        recyclerView.setAdapter(this.recyclerViewAdapter);

    }

    /**
     * ��������ꂽid��Loader���ĂьĂяo��
     * @param id Loader��id
     */
    private void startLoader(int id) {
        getLoaderManager().restartLoader(id, null, MainActivityFragment.this);
    }

    /**
     * �^����ꂽID�ɑ΂���V����Loader���C���X�^���X�����Ԃ�
     *
     * @param id Loader��id
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
     * loader�����[�h�������������ɌĂ΂��
     *
     * @param loader Loader
     * @param data �擾�����f�[�^
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null) return;

        // API�̃��X�|���X����͂���
        this.parseInstagramImage.loadJson(data);
        //�A�_�v�^�[���Z�b�g
        if (this.recyclerViewAdapter == null) {
            setRecyclerViewAdapter((RecyclerView)getView().findViewById(R.id.recyclerview));
        }

        //�f�[�^�Z�b�g�̕ύX��ʒm
        this.recyclerViewAdapter.notifyDataSetChanged();
        //�X�V�A�j���[�V�����̒�~
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

    /**
     * �^�O���烊�N�G�X�gURL�𐶐�
     *
     * @param tag �����������^�O
     * @return API�ւ̃��N�G�X�gURL
     */
    public String generateUrl(String tag) {
        return R.string.API_entry_point_before_tag + tag + R.string.API_entry_point_after_tag;
    }

    /**
     * �������[�`��
     * ����܂ł̉摜��񃊃X�g���N���A
     * �V�����^�O�Ń��N�G�X�g�𑗐M
     *
     * @param query ���͂��ꂽ�N�G��
     */
    public void search(String query) {
        //�X�V�A�j���[�V�����X�^�[�g
        swipeRefreshLayout.setRefreshing(true);
        tag = query;
        //���݂̃^�O��\������e�L�X�g�r���[���X�V
        tagTextView.setText("#" + tag);
        //�摜���X�g�̃N���A
        imageList.clear();
        //���N�G�X�gURL�̃Z�b�g
        imageList.setNextUrl(generateUrl(tag));
        //�X�V����
        onRefresh();
    }
}
