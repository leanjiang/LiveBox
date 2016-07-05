package com.lean.livebox.utils;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by lean on 16/7/5.
 */
public class NetworkUtils {

    private static OkHttpClient okHttpClient;
    private static Headers fakeHeaders;

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build();

        }
        return okHttpClient;
    }

    private static Headers getFakeHeaders() {
        if (fakeHeaders == null) {
            fakeHeaders = new Headers.Builder()
                    .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .add("Accept-Charset", "UTF-8,*;q=0.5")
                    .add("Accept-Language", "en-US,en;q=0.8")
                    .add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:13.0) Gecko/20100101 Firefox/13.0")
                    .build();
        }
        return fakeHeaders;
    }

    private static Request createRequest(String url) {
        return new Request.Builder()
                .url(url)
                .headers(getFakeHeaders())
                .build();
    }

    public static String loadContent(String url) throws IOException {
        Request request = createRequest(url);
        Response response = getOkHttpClient().newCall(request).execute();
        return response.body().string();
    }

    public static void main(String[] args) throws Exception {
        String content = loadContent("http://www.panda.tv/api_room_v2?roomid=2009&pub_key=&_=1467705406221");
        System.out.println(content);
    }
}
