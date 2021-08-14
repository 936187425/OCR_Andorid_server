package com.example.objectdetection.object.web1;

/**
 * this class show the info
 * now we get the info from the “什么都值得买” website
 */
public class reptile_article_main {
    private String title;//商品标题部分
    private String author;//表示该物品来自哪个电商平台
    private String imgUrl;//图片的url
    private String context;//商品内容介绍部分
    private String articleUrl;//转向商品的介绍部分

    @Override
    public String toString() {
        return "reptile_article{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", context='" + context + '\'' +
                ", articleUrl='" + articleUrl + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public reptile_article_main(String title, String author, String imgUrl, String context, String articleUrl) {
        this.title = title;
        this.author = author;
        this.imgUrl = imgUrl;
        this.context = context;
        this.articleUrl = articleUrl;
    }


}
