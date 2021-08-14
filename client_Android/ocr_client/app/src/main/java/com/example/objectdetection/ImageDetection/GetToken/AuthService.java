package com.example.objectdetection.ImageDetection.GetToken;

import com.example.objectdetection.ImageDetection.ConstValue.ImageDetection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 获取token类：注意一般来说每30天就需要更新一个次token
 */
public class AuthService {
    //对外的接口
    public static String  getAuth(){
        String clientId = ImageDetection.API_KEY;
        String clientSecret=ImageDetection.API_SK;
        return getAuth(clientId,clientSecret);
    }

    /**
     * 获取API访问token
     * @param ak
     * @param sk
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     */
    private  static String getAuth(String ak,String sk) {
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                //固定的参数
                +"grant_type=client_credentials"
                //可变参数
                +"&client_id=" + ak + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

}
