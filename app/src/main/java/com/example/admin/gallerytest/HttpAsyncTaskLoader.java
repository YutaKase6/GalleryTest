package com.example.admin.gallerytest;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * HTTP通信を非同期で実行するクラス
 * Created by admin on 2015/06/25.
 */
public class HttpAsyncTaskLoader extends AsyncTaskLoader<String> {

    /**
     * APIのURL
     */
    private String url = null;

    public HttpAsyncTaskLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public String loadInBackground() {
        String result = null;
        //リクエストオブジェクト生成
        Request request = new Request.Builder().url(url).build();
        //クライアントオブジェクト生成
        OkHttpClient client = new OkHttpClient();
        //リクエストを送信し結果を受け取る
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
}
