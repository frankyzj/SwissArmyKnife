package com.example.swissarmyknife.api;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientUtils {
    private static final String BASE_URL = "https://github.com/square/okhttp";
    private OkHttpClient client = new OkHttpClient();

    public String getContent() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();
        try (Response response = client.newCall(request).execute()){
            return response.body().string();
        }
    }
}
