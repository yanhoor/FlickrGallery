package com.example.yanhoor.flickrgallery.util;

import android.content.Context;
import android.util.Log;

import com.example.yanhoor.flickrgallery.model.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/14.
 */
//用于创建和解析JSON数据
public class GalleryItemToJSONSerializer {
    private static final String TAG="GalleryItemToSerializer";

    private Context mContext;
    private String mFilename;

    public GalleryItemToJSONSerializer(Context c, String f){
        mContext=c;
        mFilename=f;
    }

    //从json文件加载galleryitems数组
    public ArrayList<GalleryItem> loadGalleryItem() throws IOException,JSONException {
        ArrayList<GalleryItem>galleryItems=new ArrayList<>();
        BufferedReader reader=null;
        try{
            InputStream in=mContext.openFileInput(mFilename);
            reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonStrng=new StringBuilder();
            String line=null;
            while((line=reader.readLine())!=null){
                jsonStrng.append(line);
            }
            JSONArray array=(JSONArray)new JSONTokener(jsonStrng.toString()).nextValue();
            for (int i=0;i<array.length();i++){
                galleryItems.add(new GalleryItem(array.getJSONObject(i)));//将jsonobject解析成galleryitem
            }
        }catch(FileNotFoundException e){

        }finally{
            if (reader!=null)
                reader.close();
        }
        Log.d(TAG,"loadGalleryItems");
        return galleryItems;
    }

    //将galleryitems数组保存到json文件
    public void saveGalleryItems(ArrayList<GalleryItem>galleryItems) throws JSONException,IOException{
        JSONArray array=new JSONArray();
        for(GalleryItem g:galleryItems)
            array.put(g.toJSON());

        Writer writer=null;
        try{
            OutputStream out=mContext.openFileOutput(mFilename,Context.MODE_PRIVATE);
            writer=new OutputStreamWriter(out);
            writer.write(array.toString());
        }finally{
            if (writer!=null)
                writer.close();
        }
    }

}
