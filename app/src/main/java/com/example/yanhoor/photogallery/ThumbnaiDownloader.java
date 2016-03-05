package com.example.yanhoor.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanhoor on 2016/3/3.
 */
public class ThumbnaiDownloader<Token> extends HandlerThread {
    private static final String TAG="ThumbnaiDownloader";
    private static final int MESSAGE_DOWNLOAD=0;

    Handler mHandler;
    Map<Token,String> requestMap= Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;

    public  interface Listener<Token>{
        void onThumbnailDownloaded(Token token,Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener){
        mListener=listener;
    }

    public ThumbnaiDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler=responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==MESSAGE_DOWNLOAD){
                    @SuppressWarnings("unchecked")
                    Token token=(Token)msg.obj;
                    Log.d(TAG,"Got a request for url: "+requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }

    public void queueThumbnail(Token token, String url){
        Log.d(TAG,"Got an URL: "+url);
        requestMap.put(token,url);

        //以token为obj获取一条消息并发送出去存放到消息队列，常量作为what属性
        mHandler.obtainMessage(MESSAGE_DOWNLOAD,token).sendToTarget();
    }

    private void handleRequest(final Token token){
        try {
            final String url=requestMap.get(token);
            if (url==null)
                return;

            byte[] bitmapBytes=new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap= BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
            Log.d(TAG,"Bitmap created");
        }catch (IOException ioe){
            Log.e(TAG,"Error downloading image",ioe);
        }
    }

}
