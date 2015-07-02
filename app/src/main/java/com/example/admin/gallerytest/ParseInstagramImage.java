package com.example.admin.gallerytest;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

/**
 * Created by admin on 2015/06/25.
 */
public class ParseInstagramImage extends ParseJson {

    /**
     * �摜���̃��X�g
     */
    private ImageInfoList imageList = null;

    public ParseInstagramImage(ImageInfoList image_list) {
        super();
        this.imageList = image_list;
    }

    /**
     * ���X�|���X�̉��
     * @param str
     */
    @Override
    public void loadJson(String str) {
        JsonNode root = getJsonNode(str);
        if (root != null) {
            // ����URL���擾
            this.imageList.setNextUrl(root.path("pagination").path("next_url").asText());

            Iterator<JsonNode> ite = root.path("data").elements();
            while (ite.hasNext()) {
                JsonNode j = ite.next();
                // �摜���iURL�j�����X�g�ɒǉ�
                this.imageList.add(j.path("images").path("thumbnail").path("url").asText(),
                        j.path("images").path("standard_resolution").path("url").asText(),
                        j.path("caption").path("text").asText());
            }
        }
    }
}
