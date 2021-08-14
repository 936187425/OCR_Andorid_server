package com.example.objectdetection.ImageDetection.Detection;
import android.graphics.Bitmap;
import android.os.Environment;

import com.example.objectdetection.ImageDetection.Utils.ImageUtils;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileInputStream;

/**
 * 识别工具
 */
public class OCRdetection {
    //文字数据库放置的位置
    private String TESSBASE_PATH= Environment.getExternalStorageDirectory()+ File.separator+"Android"+File.separator+"data"+File.separator+"com.example.objectdetection"+File.separator+"files"+ File.separator;
    //英文
    private String ENGLISH_LANGUAGE="eng";
    //简体中文
    private String CHINESE_LANGUAGE="chi_sim";


    /**
     * 识别文字
     */
    public  String ScanWords(Bitmap bmp){
            ImageUtils imageUtils=new ImageUtils();
            TessBaseAPI baseAPI=new TessBaseAPI();
            String result=null;
            //初始化OCR的字体数据,TESSBASE_PATH为路径,ENGLISH_LANGUAGE指明要用的字体库(不用加后缀)
            if(baseAPI.init(TESSBASE_PATH,CHINESE_LANGUAGE)){
                //设置识别模式
                baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
                //设置要识别的图片
                baseAPI.setImage(bmp);
                //开始识别
                result=baseAPI.getUTF8Text();
                baseAPI.clear();
                baseAPI.end();
            }
            return result;
    }




}
