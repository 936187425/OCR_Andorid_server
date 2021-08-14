package com.example.objectdetection.object.webCat;

//爬取天猫的信息
public class reptile_article_cat {
    //商品id
    private String prod_id;
    //商品名称
    private String name;
    private String price;
    private String goodsUrl;
    private String imgUrl;

    @Override
    public String toString() {
        return "reptile_article_cat{" +
                "prod_id=" + prod_id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", goodsUrl='" + goodsUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoodsUrl() {
        return goodsUrl;
    }

    public void setGoodsUrl(String goodsUrl) {
        this.goodsUrl = goodsUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public reptile_article_cat(String prod_id, String name, String price, String goodsUrl, String imgUrl) {
        this.prod_id = prod_id;
        this.name = name;
        this.price = price;
        this.goodsUrl = goodsUrl;
        this.imgUrl = imgUrl;
    }
}
