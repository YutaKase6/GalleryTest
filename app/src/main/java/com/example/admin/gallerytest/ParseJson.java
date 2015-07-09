package com.example.admin.gallerytest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * JSON文字列を解析する親クラス
 * 実際のAPIに合わせた処理は子クラスでloadJsonをオーバーライドして処理する
 * Created by admin on 2015/06/25.
 */
public class ParseJson {
    protected String content;

    /**
     * JSON文字列をJsonNodeオブジェクトに変換
     *
     * @param str JSON文字列
     * @return JsonNode
     */
    protected JsonNode getJsonNode(String str) {
        try {
            return new ObjectMapper().readTree(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * JSON文字列を読み込む
     *
     * @param str JSON文字列
     */
    public void loadJson(String str) {
    }
}
