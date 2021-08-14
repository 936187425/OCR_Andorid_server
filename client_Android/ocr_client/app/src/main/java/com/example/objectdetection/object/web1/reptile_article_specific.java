package com.example.objectdetection.object.web1;

/**
 * 什么值得买网中商品框信息
 */
public class reptile_article_specific {
    //左侧图片URL：imgURL
    private String imgURL;
    //左侧图片点击href
    private String imghref;

    //商品title
    private String title;
    //商品content
    private String content;

    //商品中值得买数
    private int willBuy;
    //商品不值得买数
    private int unwillBuy;
    //收藏人数:startNum
    private int startNum;

    //来自什么网站:fromWeb
    private String fromWeb;

    @Override
    public String toString() {
        return "reptile_article_specific{" +
                "imgURL='" + imgURL + '\'' +
                ", imghref='" + imghref + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", willBuy=" + willBuy +
                ", unwillBuy=" + unwillBuy +
                ", startNum=" + startNum +
                ", fromWeb='" + fromWeb + '\'' +
                '}';
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getImghref() {
        return imghref;
    }

    public void setImghref(String imghref) {
        this.imghref = imghref;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWillBuy() {
        return willBuy;
    }

    public void setWillBuy(int willBuy) {
        this.willBuy = willBuy;
    }

    public int getUnwillBuy() {
        return unwillBuy;
    }

    public void setUnwillBuy(int unwillBuy) {
        this.unwillBuy = unwillBuy;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public String getFromWeb() {
        return fromWeb;
    }

    public void setFromWeb(String fromWeb) {
        this.fromWeb = fromWeb;
    }

    public reptile_article_specific(String imgURL, String imghref, String title, String content, int willBuy, int unwillBuy, int startNum, String fromWeb) {
        this.imgURL = imgURL;
        this.imghref = imghref;
        this.title = title;
        this.content = content;
        this.willBuy = willBuy;
        this.unwillBuy = unwillBuy;
        this.startNum = startNum;
        this.fromWeb = fromWeb;
    }
}
