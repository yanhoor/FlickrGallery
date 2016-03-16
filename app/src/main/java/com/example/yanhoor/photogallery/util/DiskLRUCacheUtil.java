package com.example.yanhoor.photogallery.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import libcore.io.DiskLruCache;

/**
 * Created by yanhoor on 2016/3/13.
 */
public class DiskLRUCacheUtil {
    private static final String TAG="DiskLRUCacheUtil";

    private static DiskLRUCacheUtil sDiskLRUCacheUtil;
    DiskLruCache mDiskLruCache;
    private Context mAppContext;

    private DiskLRUCacheUtil(Context appContext){
        mAppContext=appContext;
        mDiskLruCache=getDiskLruCacheInstance(appContext);
    }

    public static DiskLRUCacheUtil get(Context c){
        if (sDiskLRUCacheUtil==null){
            sDiskLRUCacheUtil=new DiskLRUCacheUtil(c.getApplicationContext());
        }
        return sDiskLRUCacheUtil;
    }

    //获取缓存地址，通常为/sdcard/Android/data/<application package>/cache
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径
            //即 /sdcard/Android/data/<application package>/cache
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //否则就调用getCacheDir()方法来获取缓存路径
            //即/data/data/<application package>/cache
            cachePath = context.getCacheDir().getPath();
        }
        //将获取到的路径和一个uniqueName进行拼接，作为最终的缓存路径返回
        //uniqueName是为了对不同类型的数据进行区分而设定的一个唯一值
        return new File(cachePath + File.separator + uniqueName);
    }

    //获取应用版本号
    //每当版本号改变，缓存路径下存储的所有数据都会被清除掉，
    // 因为DiskLruCache认为当应用程序有版本更新的时候，所有的数据都应该从网上重新获取。
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    //获取DiskLruCache实例
    public DiskLruCache getDiskLruCacheInstance(Context mContext){
        mContext=mContext.getApplicationContext();
        try {
            File cacheDir=getDiskCacheDir(mContext,"bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir,
                    getAppVersion(mContext), 1, 15 * 1024 * 1024);//缓存15M
        }catch (IOException e) {
            e.printStackTrace();
        }
        return mDiskLruCache;
    }

    public void writeToCache(String url){
        String key= CountMD5OfString.countStringMD5(url);
        Log.d(TAG,"key is "+key);
        //下载图片并写入缓存
        try {
            DiskLruCache.Editor editor=mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (downloadUrlToStream(url, outputStream)) {
                    Log.d(TAG,"Write to cache finished");
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DiskLruCache.Snapshot getSnapShot(String url){
        String key= CountMD5OfString.countStringMD5(url);
        DiskLruCache.Snapshot snapShot=null;
        try {
            snapShot = mDiskLruCache.get(key);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return snapShot;
    }

    public Bitmap getBitmapFromCache(String url){
        Bitmap bitmap=null;
        String key= CountMD5OfString.countStringMD5(url);
        DiskLruCache.Snapshot snapShot=getSnapShot(url);

        if (snapShot==null){
            writeToCache(url);
        }else {
            //读取缓存
            try {
                snapShot=mDiskLruCache.get(key);
                if (snapShot != null) {
                    InputStream is = snapShot.getInputStream(0);
                    bitmap=BitmapFactory.decodeStream(is);
                    Log.d(TAG,"Reading from cache");
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    //从传入的urlstring下载并缓存图片
    public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
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

}
