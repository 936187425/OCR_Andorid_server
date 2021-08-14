package com.example.objectdetection.sendToServer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;
import android.widget.RelativeLayout;
import android.os.Handler;
import android.widget.Toast;

import com.example.objectdetection.Activity.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.ClientInfoStatus;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendServer {
        final  String TAG="sendServer";
        String url_getBBImage="http://192.168.43.51:5000/getBBImage";
        //上传文本至服务器端获得处理后的图片
        public void sendTextToServer(String url, String msg, final Handler handler){
            //控制request
            OkHttpClient client=new OkHttpClient();

            FormBody.Builder formBuilder=new FormBody.Builder();
            formBuilder.add("bbImagePath",msg);
            //请求存放的是URL(交互的目标)以及要上传的信息(RequestBody)
            Request request=new Request.Builder().url(url).post(formBuilder.build()).build();

            //发送请求的类，一个request可以有多个call
            okhttp3.Call call=client.newCall(request);

            call.enqueue(new Callback(){
                //当服务器返回失败的时候处理函数
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        final String error="服务器连接失败，无法进行远程光学识别";
                        Message msg=handler.obtainMessage();
                        msg.what=2;
                        msg.obj=error;
                        handler.sendMessage(msg);
                }


                //当服务器返回成果成功是的处理函数
                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                       //获得response的body
                        InputStream inputStream = response.body().byteStream();
                        Bitmap inputBit= BitmapFactory.decodeStream(inputStream);

                        //handle刷新UI
                        Message msg=handler.obtainMessage();
                        msg.what=3;
                        msg.obj=inputBit;
                        handler.sendMessage(msg);
                }
            }
            );
        }

        //上传图片至服务器端
        public void sendImageToServer(String url,File file,final Handler handler){
                    final OkHttpClient client=new OkHttpClient();
                    //创建一个请求体
                    RequestBody body;

                    //创建一个文件上传的请求体构造器
                    //表单形式上传
                    MultipartBody.Builder builder=new MultipartBody.Builder().setType(MultipartBody.FORM);

                    builder.addFormDataPart("picture","picture",RequestBody.create(MediaType.parse("image/jpg"),file));
                    body=builder.build();

                    //创建一个请求，添加url和请求体
                    Request request=new Request.Builder().post(body).url(url).build();
                    //定义一个call.利用okhttpclient的newcall方法来创建对象，
                    Call call=client.newCall(request);


                    //异步调度方法，上传和接收的工作都在子线程里面运作,如果要使用同步的方法就用call.excute()
                    call.enqueue(new Callback(){
                             //当服务器返回失败的时候处理函数
                             @Override
                             public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                 Log.e("connection_problem",e.getMessage());
                                 if(e instanceof SocketTimeoutException){
                                     //重新提交验证,
                                     client.newCall(call.request()).enqueue(this);
                                 }
                                 if(e instanceof ConnectException){
                                     Log.e("frost_connection",e.getMessage());
                                 }
                             }


                             //当服务器返回成果成功是的处理函数
                             @Override
                             public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                                 //获得response的body
                                 String res=response.body().string();
                                 int index=res.indexOf('$');
                                 final String URI_BB=res.substring(0,index);

                                 //new Thread 获得有回归框的图片
                                 new Thread(){
                                     public void run(){
                                         sendTextToServer(url_getBBImage,URI_BB,handler);
                                     }
                                 }.start();

                                 //handle刷新UI文本框UI
                                 Message msg=handler.obtainMessage();
                                 msg.what=2;
                                 msg.obj=(index+1==res.length())?"":res.substring(index+1,res.length());
                                 handler.sendMessage(msg);
                             }
                         }
                    );
        }

}
