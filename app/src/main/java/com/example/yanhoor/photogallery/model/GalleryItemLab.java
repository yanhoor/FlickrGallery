package com.example.yanhoor.photogallery.model;

import android.content.Context;

import com.example.yanhoor.photogallery.util.GalleryItemToJSONSerializer;

import java.util.ArrayList;
import java.util.UUID;

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

    public GalleryItem getGalleryItem(UUID uuid){
        for (GalleryItem g:mGalleryItems){
            if (g.getUUID().equals(uuid))
                return g;
        }
        return null;
    }

    public void addGalleryItem(GalleryItem g){
        mGalleryItems.add(g);
    }

    public void deleteGalleryItem(GalleryItem g){
        mGalleryItems.remove(g);
    }

    public boolean saveGalleryItems(){
        try {
            mSerializer.saveGalleryItems(mGalleryItems);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
