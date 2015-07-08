package com.example.admin.gallerytest;

/**
 * 画面の情報を保持するクラス
 * Created by admin on 2015/06/25.
 */
public class ImageInfo {
    /**
     * 標準解像度の画像URL
     */
    private String standard;
    /**
     * サムネイル画像のURL
     */
    private String thumbnail;
    /**
     * 画像の説明(caption)
     */
    private String text;


    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
