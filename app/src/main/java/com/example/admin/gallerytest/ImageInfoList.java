package com.example.admin.gallerytest;

import java.util.LinkedList;
import java.util.List;

/**
 * ImageInfoのリストクラス
 * Created by admin on 2015/06/29.
 */
public class ImageInfoList {

    /**
     * 画像情報のリスト
     */
    private LinkedList<ImageInfo> imageInfoList = new LinkedList<ImageInfo>();

    /**
     * 次の画像を取得するためのURL
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
     * 保持しているリストのクリア
     */
    public void clear() {
        this.imageInfoList.clear();
    }

    /**
     * 保持しているリストに画像情報を追加する
     *
     * @param thumbnail サムネイルURL
     * @param standard 標準画像URL
     * @param text 画像に関するテキスト情報
     */
    public void add(String thumbnail, String standard, String text) {
        ImageInfo img = new ImageInfo();
        img.setThumbnail(thumbnail);
        img.setStandard(standard);
        img.setText(text);
        this.imageInfoList.addFirst(img);
    }
}

