package com.example.admin.gallerytest;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
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
        //WEB APIの呼び出し(HTTP通信)
        HttpClient httpClient = new DefaultHttpClient();
        try {
            String responseBody = httpClient.execute(new HttpGet(this.url), new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    //HTTP200の場合のみ結果を返す
                    if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
                        return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                    }
                    return null;
                }
            });
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //通信終了後は接続をシャットダウン
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }
}
