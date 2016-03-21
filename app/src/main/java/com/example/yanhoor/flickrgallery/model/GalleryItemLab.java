package com.example.yanhoor.flickrgallery.model;

import android.content.Context;
import android.util.Log;

import com.example.yanhoor.flickrgallery.util.GalleryItemToJSONSerializer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/14.
 */
//单例模式，管理GalleryItem()
public class GalleryItemLab {
    private static final String TAG="GalleryItemLab";
    private static final String FILE_NAME="galleryItem.json";

    private ArrayList<GalleryItem>mGalleryItems;
    private GalleryItemToJSONSerializer mSerializer;

    private static GalleryItemLab sGalleryItemLab;
    private Context mAppContext;

    private GalleryItemLab(Context appContext){
        mAppContext=appContext;
        mSerializer=new GalleryItemToJSONSerializer(mAppContext,FILE_NAME);

        try{
            mGalleryItems=mSerializer.loadGalleryItem();
        }catch(Exception e){
            mGalleryItems=new ArrayList<>();//如果加载数据失败，新建空数组列表
            Log.e(TAG,"Error loading galleryItems: ",e);
        }
    }

    public static GalleryItemLab get(Context c){
        if (sGalleryItemLab==null){
            sGalleryItemLab=new GalleryItemLab(c.getApplicationContext());
        }
        return sGalleryItemLab;
    }

    public ArrayList<GalleryItem> getGalleryItems(){
        return mGalleryItems;
    }

    public GalleryItem getGalleryItem(String mId){
        for (GalleryItem g:mGalleryItems){
            if (g.getId().equals(mId))
                return g;
        }
        return null;
    }

    public void addGalleryItem(GalleryItem g){
        mGalleryItems.add(g);
    }

    public void addGalleryItems(ArrayList<GalleryItem> galleryItems){
        mGalleryItems.addAll(galleryItems);
    }

    public void deleteGalleryItem(GalleryItem g){
        mGalleryItems.remove(g);
    }

    public void deleteGalleryItems(){
        Log.d(TAG,"Clear mGalleryItems in "+TAG);
        mGalleryItems.clear();
    }

    public boolean saveGalleryItems(){
        //deleteGalleryItemsFile();//先清空之前保存的内容
        try {
            mSerializer.saveGalleryItems(mGalleryItems);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //删除之前保存的GalleryItems文件
    public boolean deleteGalleryItemsFile(){
        String path=mAppContext.getFileStreamPath(FILE_NAME).getAbsolutePath();
        File file=new File(path,FILE_NAME);
        Log.d(TAG,"Delete file "+path);
        return file.delete();
    }

}
