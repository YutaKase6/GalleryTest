package com.example.admin.gallerytest;

/**
 * 設定関係のクラス
 * Created by admin on 2015/07/08.
 */
public class MyConfig {

    /**
     * tag検索用Entry point(tagよりも前半の部分)
     */
    private final String TAG_SEARCH_ENTRY_POINT_BEFORE_TAG = "https://api.instagram.com/v1/tags/";
    /**
     * tag検索用Entry point(tagよりも後半の部分)
     */
    private final String TAG_SEARCH_ENTRY_POINT_AFTER_TAG = "/media/recent?client_id=8f159dc9bf334630a37fdf4e607044cb";


    /**
     * タグからリクエストURLを生成
     *
     * @param tag 検索したいタグ
     * @return APIへのリクエストURL
     */
    public String GenerateTagSearchEntryPoint(String tag) {
        return TAG_SEARCH_ENTRY_POINT_BEFORE_TAG + tag + TAG_SEARCH_ENTRY_POINT_AFTER_TAG;
    }
}
