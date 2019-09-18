package android.coolweather.com.coolweather.util;

import android.text.TextUtils;
import android.util.Log;


import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggerInterceptor implements Interceptor
{
    public static final String TAG = "Deppon";
    private String tag;
    private boolean showResponse;
    private boolean showContent;

    public LoggerInterceptor(String tag, boolean showResponse,boolean showContent)
    {
        if (TextUtils.isEmpty(tag))
        {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.showContent = showContent;
        this.tag = tag;

    }

    public LoggerInterceptor(String tag)
    {
        this(tag, true,true);
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response)
    {
        try
        {
            //===>response log
            Log.d(tag, "========response'log=======");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            Log.d(tag, "url : " + clone.request().url());
            Log.d(tag, "code : " + clone.code());
            Log.d(tag, "protocol : " + clone.protocol());
            if (!TextUtils.isEmpty(clone.message()))
                Log.d(tag, "message : " + clone.message());

            if (showResponse)
            {
                ResponseBody body = clone.body();
                if (body != null)
                {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null)
                    {
                        Log.d(tag, "responseBody's contentType : " + mediaType.toString());
                        if (isText(mediaType))
                        {
                            String resp = body.string();
                            if(showContent){
                                LogUtils.e(tag, "responseBody's content : " + resp, LogUtils.LOG_TO_ALL);
                            }
                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else
                        {
                            Log.e(tag, "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }

            Log.d(tag, "========response'log=======end");
        } catch (Exception e)
        {
//            e.printStackTrace();
        }

        return response;
    }

    private void logForRequest(Request request)
    {
        try
        {
            String url = request.url().toString();
            Headers headers = request.headers();

            Log.d(tag, "========request'log=======");
            Log.d(tag, "method : " + request.method());
            Log.d(tag, "url : " + url);
            if (headers != null && headers.size() > 0)
            {
                Log.d(tag, "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null)
            {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null)
                {
                    Log.d(tag, "requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType))
                    {
                        //Log.d(tag, "requestBody's content : " + bodyToString(request));
                        if(showContent){
                            LogUtils.d(tag, "requestBody's content : " + bodyToString(request), LogUtils.LOG_TO_ALL);
                        }
                    } else
                    {
                        Log.d(tag, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
                }
            Log.d(tag, "========request'log=======end");
        } catch (Exception e)
        {
//            e.printStackTrace();
            LogUtils.e(tag, e.getMessage(), LogUtils.LOG_TO_ALL);
        }
    }

    private boolean isText(MediaType mediaType)
    {
        if (mediaType.type() != null && mediaType.type().equals("text"))
        {
            return true;
        }
        if (mediaType.subtype() != null)
        {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml") ||
                    mediaType.subtype().equals("x-www-form-urlencoded")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request)
    {
        try
        {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            //将url请求串进行decode
            return URLDecoder.decode(buffer.readUtf8(), "UTF-8");
        } catch (final IOException e)
        {
            return "something error when show requestBody.";
        }
    }
}
