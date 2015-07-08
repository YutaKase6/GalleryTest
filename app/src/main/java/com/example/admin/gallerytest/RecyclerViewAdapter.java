package com.example.admin.gallerytest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 2015/07/02.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    /**
     * �摜���̃��X�g
     */
    private List<ImageInfo> urls;
    /**
     * �g���ʗp���C�A�E�g
     */
    private LinearLayout expandLinearLayout;
    /**
     * �g��摜ImageView
     */
    private ImageView standardImageView;
    /**
     * �g��摜�������ptextView
     */
    private TextView textView;

    private Context context;


    public RecyclerViewAdapter(Context context, List<ImageInfo> urls, ImageView iv, LinearLayout expandLinearLayout, TextView textView) {
        super();
        this.context = context;
        this.urls = urls;
        this.standardImageView = iv;
        this.expandLinearLayout = expandLinearLayout;
        this.textView = textView;
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
            // �r���[�ɕ\������摜��URL��ݒ肵�\��
            Picasso.with(this.context).load(urls.get(i).getThumbnail()).placeholder(R.drawable.loadingimage).into(viewHolder.squaredImageView);

            // �摜���^�b�`���ꂽ���̏���
            // �^�b�`���ꂽ�摜���g�債�A�\������
            // (���łɗp�ӂ��Ă���g��摜�pimageView�ɉ摜���Z�b�g�����C�A�E�g�̕\����Visible�ɕύX����)
            // �����ɏ����̂͂悭�Ȃ������c(���̂����Ń����o�ϐ�����)
            viewHolder.squaredImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStandardImage(position, standardImageView);
                    expandLinearLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    /**
     * �W���T�C�Y�̉摜��ݒ肵�\������
     * �摜�̐�������textView�ɃZ�b�g����
     *
     * @param position index
     * @param view     �Z�b�g����View
     */
    public void setStandardImage(int position, ImageView view) {
        String url = urls.get(position).getStandard();
        Picasso.with(this.context).load(url).placeholder(R.drawable.loadingimage).into(view);
        textView.setText(urls.get(position).getText());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SquaredImageView squaredImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            squaredImageView = (SquaredImageView) itemView.findViewById(R.id.squaredImageView);
        }
    }

}
