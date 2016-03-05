package com.example.yanhoor.photogallery;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/3.
 */
public class FlickrFetchr {
    public static final String TAG="FlickrFetchr";

    private static final String ENDPOINT="https://api.flickr.com/services/rest/";
    private static final String API_KEY="0964378968b9ce3044e29838e2fc0cd8";
    private static final String METHOD_GET_RECENT="flickr.photos.getRecent";
    private static final String PARAM_EXTRAS="extras";
    private static final String EXTRA_SMALL_URL="url_s";

    private static final String XML_PHOTO="photo";

    //从指定url获取原始数据并返回一个字节流数组
    byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url=new URL(urlSpec);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();

            if (connection.getResponseCode() !=HttpURLConnection.HTTP_OK){
                return null;
            }

            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while((bytesRead=in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    //将getUrlBytes方法返回的结果转换为string
    public String getUrl(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    //构建适当的url并获取所需内容
    public ArrayList<GalleryItem> fetchItems(){
        ArrayList<GalleryItem> items=new ArrayList<>();
        try {
            String url= Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method",METHOD_GET_RECENT)//自动转义查询字符串
                    .appendQueryParameter("api_key",API_KEY)
                    .appendQueryParameter(PARAM_EXTRAS,EXTRA_SMALL_URL)
                    .build().toString();
            Log.d(TAG,"Query url is "+url);
            String xmlString=getUrl(url);
            Log.d(TAG,"Received xml: "+xmlString);

            //将返回的xmlstring创建为xmlpullparser实例
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser parser=factory.newPullParser();
            parser.setInput(new StringReader(xmlString));//事件类型初始化为START_DOCUMENT
            parseItems(items,parser);//解析flickr图片
        }catch (IOException ioe){
            Log.e(TAG,"Failed to fetch items",ioe);
        }catch (XmlPullParserException xppe){
            Log.e(TAG,"Failed to parse items",xppe);
        }
        return items;
    }

    //解析flickr图片
    void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException,IOException{
        int eventType=parser.next();

        while (eventType!=XmlPullParser.END_DOCUMENT){
            if (eventType==XmlPullParser.START_TAG&&XML_PHOTO.equals(parser.getName())){
                String id=parser.getAttributeValue(null,"id");
                String caption=parser.getAttributeValue(null,"title");
                String smallUrl=parser.getAttributeValue(null,EXTRA_SMALL_URL);
                GalleryItem item=new GalleryItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(smallUrl);
                items.add(item);
            }
            eventType=parser.next();
        }

    }

}
