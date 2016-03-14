package com.example.yanhoor.photogallery.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/3.
 */
public class GalleryItem {
    private static final String JSON_TITLE="title";
    private static final String JSON_ID="id";
    private static final String JSON_URL="url";
    private static final String JSON_OWNER="owner";
    private static final String JSON_UUID="uuid";

    private String mTitle;//说明文字
    private String mId;
    private String mUrl;
    private String mOwner;
    private UUID mUUID;

    public GalleryItem(){
        mUUID=UUID.randomUUID();
    }

    public GalleryItem(JSONObject jsonObject) throws JSONException{
        mUUID=UUID.fromString(jsonObject.getString(JSON_UUID));
        if (jsonObject.has(JSON_TITLE)){
            mTitle=jsonObject.getString(JSON_TITLE);
        }
        mId=jsonObject.getString(JSON_ID);
        mUrl=jsonObject.getString(JSON_URL);
        mOwner=jsonObject.getString(JSON_OWNER);
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JSON_UUID,mUUID.toString());
        jsonObject.put(JSON_TITLE,mTitle);
        jsonObject.put(JSON_ID,mId);
        jsonObject.put(JSON_URL,mUrl);
        jsonObject.put(JSON_OWNER,mOwner);
        return jsonObject;
    }

    public String toString(){
        return mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(UUID UUID) {
        mUUID = UUID;
    }

    public String getPhotoPageUrl(){
        return "http://www.flickr.com/photos/"+mOwner+"/"+mId;
    }

}