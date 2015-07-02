package com.example.admin.gallerytest;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 2015/06/29.
 */
public class ImageInfoList {

    /**
     * �摜���̃��X�g
     */
    private LinkedList<ImageInfo> imageInfoList = new LinkedList<ImageInfo>();

    /**
     * ���̉摜���擾���邽�߂�URL
     */
    private String nextUrl = null;


    public ImageInfoList(String next_url) {
        this.nextUrl = next_url;
    }

    public List<ImageInfo> getImageInfoList() {
        return this.imageInfoList;
    }

    public String getNextUrl() {
        return this.nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    /**
     * �ێ����Ă��郊�X�g�̃N���A
     */
    public void clear() {
        this.imageInfoList.clear();
    }

    /**
     * �ێ����Ă��郊�X�g�ɉ摜�ǉ��iURL�j��ǉ�����
     * @param thumbnail
     * @param standard
     */
    public void add(String thumbnail, String standard,String text) {
        ImageInfo img = new ImageInfo();
        img.setThumbnail(thumbnail);
        img.setStandard(standard);
        img.setText(text);
        this.imageInfoList.addFirst(img);
    }
}

