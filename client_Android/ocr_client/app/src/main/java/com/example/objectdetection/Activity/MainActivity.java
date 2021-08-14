package com.example.objectdetection.Activity;
import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.objectdetection.ImageDetection.Detection.DetectionScene;
import com.example.objectdetection.ImageDetection.Detection.OCRdetection;
import com.example.objectdetection.ImageDetection.Utils.ImageUtils;
import com.example.objectdetection.R;
import com.example.objectdetection.object.DetectionObject.detectionSceneInfo;
import com.example.objectdetection.sendToServer.SendServer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public final String TAG="MainActivity";
    //服务器IP地址
    //服务器会发生改变 所以要相应的改变
    public final String url_server="http://192.168.43.51:5000/ocr";

    //实际类
    private DetectionScene detectionScene=null;  //识别图片的类型
    private OCRdetection ocrdetection=null;//利用tenserocr识别
    private SendServer sendServer=null;
    public int isTake=-1;//判断显示的图片是拍照的到还是打开相册得到的,1表示拍照得到的  0表示打开相册得到的
    public static final int REQUEST_IMAGE_CAPTURE=1;
    public static final int REQUEST_CODE_PICK_IMAGE=2;
    private boolean canUseCmera;
    private String curImagePath;//正在显示的图片路径
    private String imgFileName;//正在显示的文件名".jpg"
    private Bitmap curBitmap;//正在显示的图片
    private Button mTakePhoto, mChoosePhoto,mSearch,mOcr;
    private ImageView picture,processPic; //图片展示区
    private TextView tv_type,tv_words,tv_showInitImage,tv_showProcessImae;//图片的类型文本框，图片上文字文本框
    //刷新UIhandler
    private Handler handler_type=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://表示刷新UItype
                    detectionSceneInfo info=(detectionSceneInfo) msg.obj;
                    //显示商品type
                    tv_type.setText(info.getResult().get(0).getKeyword());
                    break;
                case 2://刷新UI(OCR识别图片)
                    tv_words.setText((String)msg.obj);
                    break;
                case 3:
                    processPic.setImageBitmap((Bitmap) msg.obj);
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detectionScene=new DetectionScene();
        ocrdetection=new OCRdetection();
        sendServer=new SendServer();
        //判断this device上是否有相机模块
        canUseCmera=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

        //查找组件
        mTakePhoto=(Button)findViewById(R.id.btn_take_photo);
        mChoosePhoto=(Button)findViewById(R.id.btn_open_album);
        mSearch=(Button)findViewById(R.id.btn_search);
        mOcr=(Button)findViewById(R.id.btn_ocr);
        picture=(ImageView)findViewById(R.id.iv_picture);
        processPic=(ImageView)findViewById(R.id.iv_processPicture);
        tv_type=(TextView)findViewById(R.id.tv_type);
        tv_words=(TextView)findViewById(R.id.tv_showText);
        tv_showInitImage=(TextView)findViewById(R.id.tv_initWord);
        tv_showProcessImae=(TextView)findViewById(R.id.tv_processWord);

        //对组件初始化
        tv_type.setText("商品类型");
        tv_words.setText("图片的文字信息");
        tv_showInitImage.setText("原图");
        tv_showProcessImae.setText("处理后的图片");
        tv_words.setMovementMethod(ScrollingMovementMethod.getInstance());
        //设置监听
        mTakePhoto.setOnClickListener(this);
        mChoosePhoto.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mOcr.setOnClickListener(this);
    }

    //设置监听
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open_album:
                open_album();
                break;
            case R.id.btn_take_photo:
                take_photo();
                break;
            case R.id.btn_search:
                ocrFromServer();
                break;
            case R.id.btn_ocr:
                doOcr();
                break;
        }
    }




    /**
     * 拍照入口函数
     */
    public void take_photo() {
            if(canUseCmera){
                Intent takePhotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //对intent是否能否调用进行检查
                if(takePhotoIntent.resolveActivity(getPackageManager())!=null){

                    //创建存放照片的特定文件夹
                    File photoFile=null;
                    try{
                        photoFile=createImageFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if(photoFile!=null){
                        //获得文件夹的统一资源标识符
                        Uri photoURI= FileProvider.getUriForFile(this,
                                "com.example.objectdetection.fileprovider",
                                photoFile);
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePhotoIntent,REQUEST_IMAGE_CAPTURE);
                    }else{
                        Toast.makeText(this,"打开相机失败!",Toast.LENGTH_SHORT).show();
                    }
                }

            }else{
                Toast.makeText(this,"你的设备上没有相机模块",Toast.LENGTH_SHORT);
            }
    }

    /**
     * 打开系统相册
     */
    public void open_album(){
        Toast.makeText(MainActivity.this,"选择本地照片",Toast.LENGTH_SHORT).show();
        //先判断是否有权限
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                          != PackageManager.PERMISSION_GRANTED){
            //申请权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200); // 申请的 requestCode 为 200
        }else{
            //从系统相册里面取照片
            Intent intentToPickPic=new Intent(Intent.ACTION_PICK, null);
            intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
            //先判断系统中是否有处理这个intent的Activity
            if (intentToPickPic.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentToPickPic,REQUEST_CODE_PICK_IMAGE);
            } else {
                Toast.makeText(MainActivity.this, "未找到图片查看器", Toast.LENGTH_SHORT).show();
            }
        }

    }


    /**
     * 本地进行ocr识别
     */
    public void doOcr(){
        tv_words.setText("请等候！正在识别中");
        //先判断是否有图片显示
        if(isTake!=-1) {
            if(isTake==1) {//拍照得到的图片时候
                    new Thread() {
                        public void run() {
                            Log.i(TAG, "进行处理光学识别");

                            Bitmap bit = BitmapFactory.decodeFile(curImagePath);
                            bit=commonProcessImage(bit,4,0);   //对图片先进行裁剪
                            bit = commonProcessImage(bit, 3, 80);//进行OSTU阈值二值化
                            //bit=commonProcessImage(bit,2,110);//进行阈值二值化
                            Message msg1 = handler_type.obtainMessage();
                            msg1.what = 3;
                            msg1.obj = bit;
                            handler_type.sendMessage(msg1);

                            String result = ocrdetection.ScanWords(bit);
                           // Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + (result.equals("") ? "无结果" : "有结果"));
                            Message msg = handler_type.obtainMessage();
                            msg.what = 2;
                            //利用bundle传数据
                            msg.obj = result.equals("")?"本张图片上未识别出有文字,识别物体结果请看左边":result;
                            handler_type.sendMessage(msg);
                        }
                    }.start();
                }
            if(isTake==0){//打开相册图片的时候

                    new Thread(){
                        public void run(){
                            Bitmap bit=curBitmap;
                            bit = commonProcessImage(bit, 3, 110);//进行OSTU阈值二值化
                            //bit=commonProcessImage(bit,2,110);//进行阈值二值化
                            Message msg1 = handler_type.obtainMessage();
                            msg1.what = 3;
                            msg1.obj = bit;
                            handler_type.sendMessage(msg1);
                            String result = ocrdetection.ScanWords(bit);
                            Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + (result.equals("") ? "无结果" : "有结果"));
                            Message msg = handler_type.obtainMessage();
                            msg.what = 2;
                            //利用bundle传数据
                            msg.obj = result.equals("")?"本张图片上未识别出有文字,识别物体结果请看左边：":result;
                            handler_type.sendMessage(msg);
                        }
                    }.start();
                }
        }else {
            Toast.makeText(this,"照片为空，请选择或者拍照进行识别",Toast.LENGTH_LONG).show();
        }

    }


    /**
     *从远程服务器进行光学ocr识别
     */
    public void  ocrFromServer(){
        tv_words.setText("请等候！正在识别中");
           if(isTake!=-1) {
               //拍照的时候上传图片
               if(isTake==1) {
                   new Thread() {
                       public void run() {
                           Log.i(TAG, "==================sendToServer");
                           //发送HTTP请求至后端且在页面上显示
                           String path = curImagePath;
                           Log.i(TAG, "========" + path);
                           File file = new File(path);
                           sendServer.sendImageToServer(url_server, file, handler_type);
                       }
                   }.start();
               }
               if(isTake==0){
                    new Thread(){
                        public void run(){
                            String path=curImagePath;
                            File file=new File(path);
                            sendServer.sendImageToServer(url_server,file,handler_type);
                        }
                    }.start();
               }
           }else{
               Toast.makeText(MainActivity.this,"照片为空，请选择或者拍照进行识别",Toast.LENGTH_SHORT).show();
           }
    }


    /***
     * 调用系统intent返回
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_IMAGE_CAPTURE://拍照获取的图片
                        if(data!=null) {
                            //要对数据进行判空处理 why data 总是为空
                            //如果对照片指定了URI那么data返回就为null，如果没有指定就返回一个略缩图
                            process_photo(data);
                        }else{
                            isTake=1;
                            //此时将拍照的图片放置在系统相册中
                           Uri  contentUri=galleryAddPic();
                           //Log.i(TAG,"=============="+contentUri.toString());
                            //显示原图
                            showImage(contentUri,curImagePath);
                        }
                        //detectscence识别图片中的类型
                        //开启一个子线程
                        getTheType(curImagePath);
                        break;
                    case REQUEST_CODE_PICK_IMAGE://系统相册图片
                        isTake=0;
                        getUriFromAblum(data);//在相片框中显示图片
                        getTheType(curImagePath);//获取图片中物体的类型
                        break;
                }
            }




    }


    /**************************************对图片进行常见的图像处理********************************************/

    public Bitmap commonProcessImage(Bitmap bitmap,int options,int threshold){
        ImageUtils imageUtils=new ImageUtils();
        switch (options){
            case 1://进行灰度化处理
                bitmap=imageUtils.color2Gray(bitmap);
                break;
            case 2://对图片进行阈值二值化处理
                bitmap=imageUtils.adaptiveBinaryImage(bitmap,threshold);
                break;
            case 3://对图片进行OSTU二值化处理
                bitmap=imageUtils.BinaryImageByItself(bitmap);
                break;
            case 4://对图片进行裁剪
                bitmap=imageUtils.bitmapCrop(bitmap,bitmap.getWidth()/3,bitmap.getHeight()/4,bitmap.getWidth()/3,bitmap.getHeight()/4);
                break;
        }
        return bitmap;
    }

    /*******************************************************************************************************/




    /************************************打开相册*************************************/
    public void getUriFromAblum(Intent data){
        Uri imageUri=data.getData();//获得图片的Uri
        Bitmap bit=null;
        if(imageUri!=null){
            try{
                bit=getBitmapFormUri(imageUri);
                /*bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                picture.setImageBitmap(bit);
                String path=UriToPathSelectImage(imageUri);
                getTheType(path);*/
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            //部分手机可能直接存放在bundle中
            Bundle bundleExtras=data.getExtras();
            if(bundleExtras!=null){
                bit=bundleExtras.getParcelable("data");
            }
        }
        curBitmap=bit;//设置全局的Bitmap
        //创建一个临时的文件 从而获得录进因为系统相册具有权限问题
        File file=saveFile(bit);//设置全局选中的图片bitmap的变量
        curImagePath=file.getAbsolutePath();//设置全局选中的图片路径变量
        picture.setImageBitmap(bit);
    }

    //为从系统相册的图片创建一个临时图片文件
    public File saveFile(Bitmap bm) {
        File myFile=null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            imgFileName = imageFileName+".jpg";
            String path = Environment.getExternalStorageDirectory()+"/Android/data/com.example.objectdetection/files/Pictures/";
            File dirFile=new File(path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            myFile=new File(path+imgFileName);
            //把图片通过流的形式转化为特定的文件
            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(myFile));
            bm.compress(Bitmap.CompressFormat.JPEG,80,bos);
            bos.flush();
            bos.close();
        } catch (Exception e){
            e.printStackTrace();
            return myFile;
        }
        return myFile;
    }

    /**
     * 对图片进行压缩
     */
    public Bitmap getBitmapFormUri(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getContentResolver().openInputStream(uri);

        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;//不加载到内存
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            if (options<=0)
                break;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }



    /**
     *将选中图片的uri转化为路径
     */
    public String UriToPathSelectImage(Uri uri){
        String imagePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i(TAG,"进入了");

            //权限判断
            int isPermitted= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (isPermitted!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }else{
                if (DocumentsContract.isDocumentUri(this, uri)) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                        //Log.d(TAG, uri.toString());
                        String id = docId.split(":")[1];
                        String selection = MediaStore.Images.Media._ID + "=" + id;
                        imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                    } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                        //Log.d(TAG, uri.toString());
                        Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(docId));
                        imagePath = getImagePath(contentUri, null);
                    }
                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                    //Log.d(TAG, "content: " + uri.toString());
                    imagePath = getImagePath(uri, null);
                }
            }
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    /************************************拍照*************************************/

    /**
     * 获得图片类型
     */
    public void getTheType(final String curImagePath){
        new Thread(
        ){
            public void run() {
                detectionSceneInfo info=detectionScene.DetectionScenes(curImagePath);
                Message msg=handler_type.obtainMessage();
                msg.what=1;
                msg.obj=info;
                handler_type.sendMessage(msg);
            }
        }.start();
    }

    /**
     *获得刚拍的照片(原图)并进行显示
     */
    public void showImage(Uri contentUri,String filepath){
        Bitmap bitmap= BitmapFactory.decodeFile(filepath);
        curBitmap=bitmap;//设置全局显示的图片
       // bitmap = rotateBimap(this, -90, bitmap);//旋转图片-90°
        picture.setImageBitmap(bitmap);//ImageView显示图片
    }

    /**
     * 把拍的图片设置为其他应用共享
     */
    private Uri galleryAddPic() {
        //把照片插入系统图库
        Uri contentUri=null;
        try {
            //把照片插入系统相册中
            MediaStore.Images.Media.insertImage(this.getContentResolver(), curImagePath, imgFileName, null);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(curImagePath);
            contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }finally {
            return contentUri;
        }
    }



    /**
     * 创建将相机拍摄的图片保存的文件夹
     * 注:如果该应用卸载后，数据就会自动消失
     */
    public File createImageFile()throws IOException{
        //创建一个文件夹名(以时间戳)
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="JPEG_"+timeStamp+"_";
        imgFileName=imageFileName+".jpg";
        //获得存放照片的外存文件夹
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        //获得当前文件路径
        curImagePath=image.getAbsolutePath();
        //Log.i(TAG,"++++++++++"+curImagePath);
        return image;
    }




    /**
     *获得照片（略缩图）以及显示在ImageView中
     */
    public void process_photo(Intent data){
        //把照片存放在相册中供其他应用可以查看
        //略缩图显示

        Bundle extras=data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");//获得从系统相机的activity中获得bitmap类型的图片
        curBitmap=imageBitmap;//设置现在显示图片的全局变量
        picture.setImageBitmap(imageBitmap);
    }



}
