package com.example.admin.gallerytest;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

/**
 * Created by admin on 2015/06/25.
 */
public class ParseInstagramImage extends ParseJson {

    /**
     * 画像情報のリスト
     */
    private ImageInfoList imageList = null;

    public ParseInstagramImage(ImageInfoList image_list) {
        super();
        this.imageList = image_list;
    }

    /**
     * レスポンスの解析
     * @param str
     */
    @Override
    public void loadJson(String str) {
        JsonNode root = getJsonNode(str);
        if (root != null) {
            // 次のURLを取得
            this.imageList.setNextUrl(root.path("pagination").path("next_url").asText());

            Iterator<JsonNode> ite = root.path("data").elements();
            while (ite.hasNext()) {
                JsonNode j = ite.next();
                // 画像情報（URL）をリストに追加
                this.imageList.add(j.path("images").path("thumbnail").path("url").asText(),
                        j.path("images").path("standard_resolution").path("url").asText(),
                        j.path("caption").path("text").asText());
            }
        }
    }
}
