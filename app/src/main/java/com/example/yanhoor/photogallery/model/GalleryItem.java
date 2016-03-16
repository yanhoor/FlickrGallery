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
    private static final String JSON_SECRET="secret";
    private static final String JSON_SERVER="server";
    private static final String JSON_FARM="farm";

    private String mTitle;//说明文字
    private String mId;
    private String mUrl;
    private String mOwner;
    private UUID mUUID;
    private String mSecret;
    private String mServer;
    private String mFarm;

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
        jsonObject.put(JSON_SECRET,mSecret);
        jsonObject.put(JSON_SERVER,mServer);
        jsonObject.put(JSON_FARM, mFarm);
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

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public String getFarm() {
        return mFarm;
    }

    public void setFarm(String farm) {
        mFarm = farm;
    }

    public String getServer() {
        return mServer;
    }

    public void setServer(String server) {
        mServer = server;
    }

    public String getPhotoPageUrl(){
        return "http://www.flickr.com/photos/"+mOwner+"/"+mId;
    }

    public String get

}
