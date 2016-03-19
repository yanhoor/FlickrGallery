package com.example.yanhoor.photogallery.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.yanhoor.photogallery.model.GalleryItem;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yanhoor on 2016/3/12.
 */
//计算传入string的MD5值

public class StaticMethodUtil {
    private static final String TAG="StaticMethodUtil ";

    public  static final String ENDPOINT="https://api.flickr.com/services/rest/";
    public static final String API_KEY="0964378968b9ce3044e29838e2fc0cd8";
    public static final String METHOD_GET_RECENT="flickr.photos.getRecent";

    public static String countMD5OfString(String input){
        try{
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest=MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray=input.getBytes();
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        }catch (NoSuchAlgorithmException e){
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray){
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };
        // new一个字符数组，这个就是用来组成结果字符串的
        // 一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
        char[] resultCharArray =new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    public static GalleryItem getPhotoInfo(final GalleryItem galleryItem){
        String photo_id=galleryItem.getId();
        String url= Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method","flickr.photos.getInfo")
                .appendQueryParameter("api_key",API_KEY)
                .appendQueryParameter("photo_id",photo_id)
                .build().toString();
        KJHttp kjHttp=new KJHttp();
        kjHttp.get(url, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.d(TAG,"Getting photo info from "+t);

                try{
                    XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                    XmlPullParser parser=factory.newPullParser();
                    parser.setInput(new StringReader(t));
                    int eventType=parser.getEventType();
                    while (eventType!=XmlPullParser.END_DOCUMENT){
                        if (eventType==XmlPullParser.START_TAG&&"owner".equals(parser.getName())){
                            String userName=parser.getAttributeValue(null,"username");
                            String realName=parser.getAttributeValue(null,"realname");
                            String location=parser.getAttributeValue(null,"location");
                            Log.d(TAG,"username is "+userName);
                            Log.d(TAG,"realname is "+realName);

                            galleryItem.setUserName(userName);
                            galleryItem.setRealName(realName);
                            galleryItem.setLocation(location);
                        }
                        if (eventType==XmlPullParser.START_TAG&&"description".equals(parser.getName())){
                            String description=parser.nextText();
                            Log.d(TAG,"description is "+description);
                            galleryItem.setDescription(description);
                        }
                        if (eventType==XmlPullParser.START_TAG&&"dates".equals(parser.getName())){
                            String postedDate=parser.getAttributeValue(null,"posted");
                            galleryItem.setPostedDate(postedDate);
                        }
                        eventType=parser.next();
                    }
                }catch (XmlPullParserException xppe){
                    xppe.printStackTrace();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        return galleryItem;
    }

    //用于压缩图片
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用下面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options
            ,int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
