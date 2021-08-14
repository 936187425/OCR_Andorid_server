package com.example.objectdetection.ImageDetection.Detection;

import com.example.objectdetection.ImageDetection.GetToken.AuthService;
import com.example.objectdetection.ImageDetection.Utils.Base64Util;
import com.example.objectdetection.ImageDetection.Utils.FileUtil;
import com.example.objectdetection.ImageDetection.Utils.HttpUtil;
import com.example.objectdetection.object.DetectionObject.detectionSceneInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 通用物体和场景识别
 */
public class DetectionScene {
    /**
     *获取detectionSceneInfo对象的方法
     * @param path
     * @return  detectionSceneInfo对象
     */
    public  detectionSceneInfo DetectionScenes(String path){
        String gson=detectionScenes(path);
        return new Gson().fromJson(gson, detectionSceneInfo.class);
    }

    private  String detectionScenes(String path){
        //请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general";
        try{
            // 本地文件路径
            String filePath = path;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            String accessToken = AuthService.getAuth();  //get the token value
            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
