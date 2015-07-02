package com.example.admin.gallerytest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 2015/06/28.
 */
public class GridViewAdapter extends BaseAdapter {
    /**
     * �摜���̃��X�g
     */
    private List<ImageInfo> urls;
    private Context context;

    public GridViewAdapter(Context context, List<ImageInfo> urls) {
        this.urls = urls;
        this.context = context;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        // �T���l�C���摜��URL��Ԃ�
        if(urls.size() == 0){
            return null;
        }else{
            return urls.get(position).getThumbnail();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;
        // ����
        if (view == null) {
            view = new SquaredImageView(context);
        }
        // �T���l�C���摜��URL���擾
        String url = (String)getItem(position);

        // �r���[�ɕ\������摜��URL��ݒ�
        Picasso.with(this.context).load(url).placeholder(R.drawable.loadingimage).into(view);
        return view;
    }

    /**
     * �W���T�C�Y�̉摜��ݒ肷��
     * @param position
     * @param view
     */
    public void setStandardImage(int position, ImageView view) {
        if (urls.size() > 0){
            String url =urls.get(position).getStandard();
            Picasso.with(this.context).load(url).placeholder(R.drawable.loadingimage).into(view);
        }
    }
}

