package com.example.admin.gallerytest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by admin on 2015/06/25.
 */
public class ParseJson {
    protected String content;

    protected JsonNode getJsonNode(String str) {
        try {
            //JSON文字列を、JSONNodeオブジェクトに変換
            return new ObjectMapper().readTree(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // JSON文字列を読み込む
    public void loadJson(String str) {
    }
}
