package com.example.yanhoor.flickrgallery.util;

import android.net.Uri;
import android.util.Log;

import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.PhotoSet;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/4/7.
 */
public class GetPhotoSetInfoUtil {
    private static final String TAG="GetPhotoSetInfoUtil";

    private static final String ENDPOINT="https://api.flickr.com/services/rest/";
    private static final String API_KEY="0964378968b9ce3044e29838e2fc0cd8";

    private PhotoSetListener mListener;
    private PhotoSet mPhotoSet;
    private String mUserId;
    private ArrayList<GalleryItem>mGalleryItems;

    public interface PhotoSetListener{
        void onUpdateFinish(PhotoSet photoSet);
    }

    public void setPhotoSetListener(PhotoSetListener listener){
        mListener=listener;
    }

    public PhotoSet getPhotoSetInfo(String photoSetId,String userId){
        mPhotoSet=new PhotoSet();
        mPhotoSet.setId(photoSetId);
        mUserId=userId;

        getInfo();
        getPhotos();

        return mPhotoSet;
    }

    public void getInfo(){
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", "flickr.photosets.getInfo")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("photoset_id",mPhotoSet.getId())
                .appendQueryParameter("user_id",mUserId)
                .build().toString();

        HttpConfig config=new HttpConfig();
        config.cacheTime=0;
        new KJHttp(config).get(url, new HttpCallBack() {
            @Override
            public void onFinish() {
                super.onFinish();
                mListener.onUpdateFinish(mPhotoSet);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.d(TAG,"Getting photoSet info from "+t);

                try {
                    XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                    XmlPullParser parser=factory.newPullParser();
                    parser.setInput(new StringReader(t));

                    int eventType=parser.getEventType();
                    while (eventType!=XmlPullParser.END_DOCUMENT){
                        if (eventType==XmlPullParser.START_TAG&&"photoset".equals(parser.getName())){
                            String id=parser.getAttributeValue(null,"id");
                            String primary=parser.getAttributeValue(null,"primary");
                            String secret=parser.getAttributeValue(null,"secret");
                            String farm=parser.getAttributeValue(null,"farm");
                            String photos=parser.getAttributeValue(null,"count_photos");
                            String countViews=parser.getAttributeValue(null,"count_views");
                            String countComments=parser.getAttributeValue(null,"count_comments");
                            String date_create=parser.getAttributeValue(null,"date_create");
                            String date_update=parser.getAttributeValue(null,"date_update");

                            mPhotoSet.setId(id);
                            mPhotoSet.setPrimary(primary);
                            mPhotoSet.setSecret(secret);
                            mPhotoSet.setFarm(farm);
                            mPhotoSet.setCount_photos(photos);
                            mPhotoSet.setCount_views(countViews);
                            mPhotoSet.setCount_comments(countComments);
                            mPhotoSet.setDateCreate(date_create);
                            mPhotoSet.setDateUpdate(date_update);
                        }
                        if (eventType==XmlPullParser.START_DOCUMENT&&"title".equals(parser.getName())){
                            String title=parser.nextText();
                            mPhotoSet.setTitle(title);
                        }
                        if (eventType==XmlPullParser.START_TAG&&"description".equals(parser.getName())){
                            String description=parser.nextText();
                            mPhotoSet.setDescription(description);
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
    }

    public void getPhotos(){
        mGalleryItems=new ArrayList<>();

        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", "flickr.photosets.getPhotos")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("photoset_id",mPhotoSet.getId())
                .appendQueryParameter("user_id",mUserId)
                .appendQueryParameter("extras","url_s")
                .build().toString();

        HttpConfig config=new HttpConfig();
        config.cacheTime=0;
        new KJHttp(config).get(url, new HttpCallBack() {
            @Override
            public void onFinish() {
                super.onFinish();
                mListener.onUpdateFinish(mPhotoSet);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                mGalleryItems.clear();

                try {
                    XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                    XmlPullParser parser=factory.newPullParser();
                    parser.setInput(new StringReader(t));

                    int eventType=parser.getEventType();
                    while (eventType!=XmlPullParser.END_DOCUMENT){
                        if (eventType==XmlPullParser.START_TAG&&"photo".equals(parser.getName())){
                            GalleryItem galleryItem=new GalleryItem();
                            String id=parser.getAttributeValue(null,"id");
                            String url=parser.getAttributeValue(null,"url_s");

                            galleryItem.setId(id);
                            galleryItem.setUrl(url);

                            mGalleryItems.add(galleryItem);
                        }

                        eventType=parser.next();
                    }
                }catch (XmlPullParserException xppe){
                    xppe.printStackTrace();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
                mPhotoSet.getGalleryItems().clear();
                mPhotoSet.setGalleryItems(mGalleryItems);
            }
        });

    }

}
