package com.example.objectdetection.ImageDetection.Utils;

import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Matrix;

/**
 * 对图片进行简单的处理
 */
public class ImageUtils {
    private static final String TAG="IMAGEUTILS";
    /**
     * 对图片进行灰度化---先降低分辨率
     */
    public Bitmap color2Gray(Bitmap input){
            //得到图片的长和宽
        int width=input.getWidth();
        int height=input.getHeight();
        Bitmap bmpGray=null;
        //RGB_565表示R为5位,G为6位，B位5位
        bmpGray=Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
        //创建画布
        Canvas c=new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(input,0,0,paint);
        return bmpGray;
    }

    /**
     * 对图片进行阈值二值化算法
     */
    public Bitmap  adaptiveBinaryImage(Bitmap graymap,int threshold){
        //得到图形的宽度和长度
        int width = graymap.getWidth();
        int height = graymap.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = graymap.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                //对图像进行二值化处理
                if (gray <= threshold) {
                    gray = 0;
                } else {
                    gray = 255;
                }
                // 新的ARGB
                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }

    /**
     * 对图片进行自适应二值化算法
     */
    public Bitmap BinaryImageByItself(Bitmap input){
        //获取该图片的阈值
        int width=input.getWidth();
        int height=input.getHeight();
        Log.i(TAG,"width==="+width+";height===="+height);
        //存放的是input图像的灰度值
        int pixels[][]=new int[input.getWidth()][input.getHeight()];
        int threshold=Ostu(input,pixels,width,height);

        //二值化处理
        Bitmap binarymap = null;
        binarymap = input.copy(Bitmap.Config.ARGB_8888, true);
        //进行二值化处理
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                int col = binarymap.getPixel(i, j);
                int alpha = col & 0xFF000000;
                if (pixels[i][j]<= threshold) {
                    pixels[i][j]= 0;
                } else {
                    pixels[i][j]= 255;
                }
                int newColor = alpha | (pixels[i][j]<< 16) | (pixels[i][j]<< 8) | pixels[i][j];
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }

    /**
     * 获得Ostu阈值,基于灰度图
     */
    public int Ostu(Bitmap input,int grays[][],int width,int height){

        int size=width*height;
        int i;
        float gray=0; //前景灰度比例
        int threshold=0;
        float variance=0;  //类间方差
        float maxvariance=0;//最大方差
        float w0=0;//前景像素比例
        float u0=0; //前景平均灰度
        float u=0;//总平均灰度,可看做i=255时的前景灰度比例
        float histogram[]=new float[256];
         //将图片进行灰度化
        for(i=0;i<width;i++){
            for(int j=0;j<height;j++){
                //count gray
                int col=input.getPixel(i,j);
                int alpha = col & 0xFF000000;
                //RGB三通道
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray_tmp = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grays[i][j]=gray_tmp;
                histogram[gray_tmp]++;
            }
        }
        //计算0-255直方图,histogram中的是概率值
        for(i=0;i<256;i++){
            histogram[i]/=size;
            u+=histogram[i]*i;
        }
        for(i=0;i<256;i++){
            w0+=histogram[i];
            gray+=i*histogram[i];//平均的灰度值（根据全局信息获得）
            u0=gray/w0;
            variance=w0/(1-w0)*(u0-u)*(u0-u);
            if(variance>maxvariance){
                //当此时的方差大于过去所有的方差时
                maxvariance=variance;
                threshold=i;
            }
        }
        return threshold;
    }

    /**
     * 裁剪图片
     */
    public Bitmap bitmapCrop(Bitmap bit,int left,int top,int width,int height){
        if(bit==null||width<0||height<0) return null;
        int widthOrg = bit.getWidth();
        int heightOrg = bit.getHeight();
        if (widthOrg >= width && heightOrg >= height) {
            try {
                bit = Bitmap.createBitmap(bit, left, top, width, height);
            } catch (Exception e) {
                return null;
            }
        }
        return bit;
    }

    /**
     * 旋转图片
     */
    public Bitmap rotateToDegrees(Bitmap bit,float degrees){
        Matrix matrix=new Matrix();
        matrix.reset();
        matrix.setRotate(degrees);
        return Bitmap.createBitmap(bit,0, 0, bit.getWidth(),bit.getHeight(), matrix,true);
    }

}
