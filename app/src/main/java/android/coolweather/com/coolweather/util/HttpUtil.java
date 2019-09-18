package android.coolweather.com.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 用于和服务器进行交互的工具类
 */
public class HttpUtil {
    // 现在发起一条HTTP请求只需要调用sendOkHttpRequest()方法，传入请求地址，并注册一个回调
    // 来处理服务器响应就可以了
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        //OkHttpClient client = new  OkHttpClient.Builder().addInterceptor(new LoggerInterceptor("test")).build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
