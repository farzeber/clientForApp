package example.com.classappp.util;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by mick2017 on 2017/9/12.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,String json, okhttp3.Callback callback){
        MediaType JSON=MediaType.parse("application/json;charset=utf-8");
        OkHttpClient client=new OkHttpClient.Builder()
                            .connectTimeout(5,TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .build();
        RequestBody body=RequestBody.create(JSON,json);
        Request request=new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
