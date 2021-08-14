package com.example.objectdetection.spider;

import android.util.Log;

import com.example.objectdetection.object.web1.reptile_article_main;
import com.example.objectdetection.object.web1.reptile_article_specific;
import com.example.objectdetection.object.webCat.reptile_article_cat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class getDataFromInter {
    private static final String TAG="getDataFromInter";

    /**
     * 抓取什么值得买首页得精选文章
     * @param html
     * @return ArrayList<reptile_article> articles
     * way：use the jsoup.jar to finish the task of extracting the elements--HTML
     */
    public static ArrayList<reptile_article_main> spiderArticle_main(String html){
        ArrayList<reptile_article_main> articles=new ArrayList<>();

        Document document=Jsoup.parse(html);//change the string html to the document object like js or css
        //get the element of the topic "have good price"
        Elements elements = document
                .select("ul[class=feed-list-hits feed-list-index]")
                .select("li[class=feed-row-wide J_feed_za feed-haojia]");

       // Log.i(TAG,"spiderArticle========:elements"+elements.html());

        for(Element element:elements){
            /*
                get the variable String such  as imgUrl,author and so on
             */
            String title=element
                        .select("h5[class=feed-block-title has-price]")
                        .text();

            //which the info comes from such as taobao,jingdong
            String author=element
                          .select("div[class=z-feed-foot]")
                          .select("span[class=feed-block-extras]")
                          .select("a")
                          .select("span")
                          .text();

            String imgurl=element
                        .select("div[class=z-feed-img]")
                        .select("a")
                        .select("img")
                        .attr("src");

            String context = element
                    .select("div[class=feed-block-descripe]")
                    .text();
            String articleUrl = element
                    .select("div[class=z-feed-img ]")
                    .select("a")
                    .attr("href");
            reptile_article_main article=new reptile_article_main(title,author,imgurl,context,articleUrl);
            articles.add(article);
        }
        return articles;
    }

    /**
     * 抓取天猫网的商品信息
     * @param html
     * @return ArrayList<reptile_article_cat>
     */
    public static ArrayList<reptile_article_cat> spiderArticle_cat(String html){
        ArrayList<reptile_article_cat> articleCats=new ArrayList<reptile_article_cat>();
        // doc获取整个页面的所有数据
       Document doc = Jsoup.parse(html);
       Elements ulList = doc.select("div[class='view grid-nosku']");
       Elements liList = ulList.select("div[class='product']");
       for (Element item : liList) {
           // 商品ID
           String id = item.select("div[class='product']").select("p[class='productStatus']").select("span[class='ww-light ww-small m_wangwang J_WangWang']").attr("data-item");
           System.out.println("商品ID：" + id);
           // 商品名称
           String name = item.select("p[class='productTitle']").select("a").attr("title");
           System.out.println("商品名称：" + name);
           // 商品价格
           String price = item.select("p[class='productPrice']").select("em").attr("title");
           System.out.println("商品价格：" + price);
           // 商品网址
           String goodsUrl = item.select("p[class='productTitle']").select("a").attr("href");
           System.out.println("商品网址：" + goodsUrl);
           // 商品图片网址
           String imgUrl = item.select("div[class='productImg-wrap']").select("a").select("img").attr("data-ks-lazyload");
           System.out.println("商品图片网址：" + imgUrl);
           System.out.println("------------------------------------");
           articleCats.add(new reptile_article_cat(id,name,price,goodsUrl,imgUrl));
       }
       return articleCats;
    }
}
