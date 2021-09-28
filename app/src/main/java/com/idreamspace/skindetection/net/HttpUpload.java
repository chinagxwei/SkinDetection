package com.idreamspace.skindetection.net;

import android.util.Log;

import androidx.annotation.NonNull;

import com.idreamspace.skindetection.app.Component;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUpload implements Component {
    public final static String HTTP_TAG = "Http.";

    public void byPath(String getPath, String url) {
        File file = new File(getPath);
        this.byFile(file, url);
    }

    public void byFile(File file, String url) {
        long size = file.length();//文件长度
        OkHttpClient httpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
        RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("id", 0 + "")//添加表单数据
                .addFormDataPart("filesize", Long.toString(size))
                .addFormDataPart("file", file.getName(), requestBody)//文件名,请求体里的文件
                .build();

        Request request = new Request.Builder()
//                .header("Authorization", "Bearer d3e63518-1ba7-4342-b94c-63c8b9b9046b")//添加请求头的身份认证Token
                .url(url)
                .post(multipartBody)
                .build();

        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(HTTP_TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(HTTP_TAG, "onResponse: " + response.body().string());
            }
        });
    }
}
