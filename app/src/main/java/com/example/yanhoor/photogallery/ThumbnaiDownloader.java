package com.example.yanhoor.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.yanhoor.photogallery.util.CountMD5OfString;
import com.example.yanhoor.photogallery.util.DiskLRUCacheUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import libcore.io.DiskLruCache;

/**
 * Created by yanhoor on 2016/3/3.
 */
//Handy class for starting a new thread that has a looper.
// The looper can then be used to create handler classes. Note that start() must still be called.

public class ThumbnaiDownloader<Token> extends HandlerThread {
    private static final String TAG="ThumbnaiDownloader";
    private static final int MESSAGE_DOWNLOAD=0;

    public static int REFRESH_ALL_PIC=0;
    public static final String PRE_HAS_CACHE="hasCache";

    Handler mHandler;
    DiskLruCache mDiskLruCache = null;
    Context mContext;

    //创建键为Token类型，值为string类型的同步hashMap
    //Collections.synchronizedMap允许需要同步的用户可以拥有同步，而不需要同步的用户则不必为同步付出代价。
    Map<Token,String> requestMap= Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;

    public  interface Listener<Token>{
        void onThumbnailDownloaded(Token token,Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener){
        mListener=listener;//主线程的listener实例
    }

    public ThumbnaiDownloader(Context context,Handler responseHandler){
        super(TAG);
        mResponseHandler=responseHandler;//主线程Handler
        mContext=context;
    }

    //该方法的调用发生在Looper第一次检查消息队列之前
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
        requestMap.put(token,url);//将token-url键值对存放到requestMap

        //获取一条消息并发送出去存放到消息队列，以token为obj，常量作为what属性
        mHandler.obtainMessage(MESSAGE_DOWNLOAD,token)
                .sendToTarget();//发送message给handler，handler将message放在looper消息队列尾部
    }

    private void handleRequest(final Token token){
        final String urlString=requestMap.get(token);
        Log.d(TAG,"urlString is "+urlString);
        if (urlString==null)return;
        String key= CountMD5OfString.countStringMD5(urlString);
        Log.d(TAG,"key is "+key);

        //获取DiskLruCache实例
        try {
            File cacheDir=new DiskLRUCacheUtil().getDiskCacheDir(mContext,"bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir,
                    new DiskLRUCacheUtil().getAppVersion(mContext), 1, 10 * 1024 * 1024);//缓存10M
        }catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG,"REFRESH_ALL_PIC is "+REFRESH_ALL_PIC);
        DiskLruCache.Snapshot snapShot=null;
        try {
            snapShot = mDiskLruCache.get(key);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        //检查对应url是否有缓存
        if (snapShot==null){
            //下载图片并写入缓存
            try {
                DiskLruCache.Editor editor=mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (downloadUrlToStream(urlString, outputStream)) {
                        Log.d(TAG,"Write to cache finished");
                        editor.commit();
                        PreferenceManager.getDefaultSharedPreferences(mContext)
                                .edit()
                                .putBoolean(PRE_HAS_CACHE,true)
                                .commit();
                    } else {
                        editor.abort();
                    }
                }
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //读取缓存
            try {
                snapShot=mDiskLruCache.get(key);
                Log.d(TAG,"snapShot is "+snapShot);
                if (snapShot != null) {
                    InputStream is = snapShot.getInputStream(0);
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mResponseHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (requestMap.get(token)!=urlString)
                                return;

                            requestMap.remove(token);//删除键对应的条目
                            mListener.onThumbnailDownloaded(token,bitmap);
                            Log.d(TAG,"Reading from cache");
                        }
                    });
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            //读取缓存
            try {
                snapShot = mDiskLruCache.get(key);
                Log.d(TAG,"snapShot is "+snapShot);
                if (snapShot != null) {
                    InputStream is = snapShot.getInputStream(0);
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mResponseHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (requestMap.get(token)!=urlString)
                                return;

                            requestMap.remove(token);//删除键对应的条目
                            mListener.onThumbnailDownloaded(token,bitmap);
                            Log.d(TAG,"Reading from cache");
                        }
                    });
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*
        try {
            final String url=requestMap.get(token);
            if (url==null)
                return;

            byte[] bitmapBytes=new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap= BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
            Log.d(TAG,"Bitmap created");

            //下载与显示
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token)!=url)
                        return;

                    requestMap.remove(token);//删除键对应的条目
                    mListener.onThumbnailDownloaded(token,bitmap);
                }
            });
        }catch (IOException ioe){
            Log.e(TAG,"Error downloading image",ioe);
        }
        */

    }

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //清除队列外的所有请求
    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();//删除所有条目
    }

}
