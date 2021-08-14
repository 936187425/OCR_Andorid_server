package com.example.objectdetection.ImageDetection.ConstValue;

import com.baidu.aip.imageclassify.AipImageClassify;





public class ImageDetection {
    /**
     * 图像识别
     * 通用物体和场景识别高级版
     */
    //set the api ID/AK/SK
    public static final String APP_ID="23058759";
    public static final String API_KEY="KShGgIqeoNxnnjIuo5SitZ24";
    public static final String API_SK="kvGqPvlPAlhgIjyXRlc196SfwnY5Hhf3";
    //api--the object
    public static AipImageClassify client=new AipImageClassify(APP_ID,API_KEY,API_SK);

}
