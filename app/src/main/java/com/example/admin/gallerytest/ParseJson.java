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
            //JSON��������AJSONNode�I�u�W�F�N�g�ɕϊ�
            return new ObjectMapper().readTree(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // JSON�������ǂݍ���
    public void loadJson(String str) {
    }
}
