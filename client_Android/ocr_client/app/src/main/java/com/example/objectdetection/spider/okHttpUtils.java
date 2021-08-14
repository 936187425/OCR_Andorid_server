package com.example.objectdetection.spider;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Thread.sleep;

public class okHttpUtils {
    public static String TAG="okHttpUtils";

    //send the get request to get the  String  about HTML
    //值得买网页发送http
    public static String okGetArt(String url){
        String html = null;
        OkHttpClient client = new OkHttpClient();

        //create the Request Object
        Request request=new Request.Builder()
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                        .url(url)
                        .build();
        try{
            Response response=client.newCall(request).execute();
            html=response.body().string();
        }catch (IOException e){
            e.printStackTrace();
        }
        Log.i(TAG,"html=========:"+html!=null?"yes":"no");
        return html;
    }


}
