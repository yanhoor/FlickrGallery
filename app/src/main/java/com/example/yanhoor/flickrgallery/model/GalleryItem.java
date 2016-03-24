package com.example.yanhoor.flickrgallery.model;

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
    private static final String JSON_USER_NAME="username";
    private static final String JSON_REAL_NAME="realname";
    private static final String JSON_DESCRIPTION="description";
    private static final String JSON_LOCATION="location";
    private static final String JSON_POSTED_DATE="posted_date";

    private String mTitle;//说明文字
    private String mId;
    private String mUrl;
    private String mUserId;
    private UUID mUUID;
    private String mSecret;
    private String mServer;
    private String mFarm;

    //需要调用PhotoInfoUtil才能获取
    private String mUserName;
    private String mRealName;
    private String mDescription=null;
    private String mLocation=null;
    private String mPostedTime;

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
        mUserId =jsonObject.getString(JSON_OWNER);
        mSecret=jsonObject.getString(JSON_SECRET);
        mServer=jsonObject.getString(JSON_SERVER);
        mFarm=jsonObject.getString(JSON_FARM);
        if (jsonObject.has(JSON_USER_NAME))
            mUserName=jsonObject.getString(JSON_USER_NAME);
        if (jsonObject.has(JSON_REAL_NAME))
            mRealName=jsonObject.getString(JSON_REAL_NAME);
        if (jsonObject.has(JSON_DESCRIPTION))
            mDescription=jsonObject.getString(JSON_DESCRIPTION);
        if (jsonObject.has(JSON_LOCATION))
            mLocation=jsonObject.getString(JSON_LOCATION);
        mPostedTime =jsonObject.getString(JSON_POSTED_DATE);
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JSON_UUID,mUUID.toString());
        jsonObject.put(JSON_TITLE,mTitle);
        jsonObject.put(JSON_ID,mId);
        jsonObject.put(JSON_URL,mUrl);
        jsonObject.put(JSON_OWNER, mUserId);
        jsonObject.put(JSON_SECRET,mSecret);
        jsonObject.put(JSON_SERVER,mServer);
        jsonObject.put(JSON_FARM, mFarm);
        jsonObject.put(JSON_USER_NAME,mUserName);
        jsonObject.put(JSON_REAL_NAME,mRealName);
        jsonObject.put(JSON_DESCRIPTION,mDescription);
        jsonObject.put(JSON_LOCATION,mLocation);
        jsonObject.put(JSON_POSTED_DATE, mPostedTime);
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

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getRealName() {
        return mRealName;
    }

    public void setRealName(String realName) {
        mRealName = realName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getPostedTime() {
        return mPostedTime;
    }

    public void setPostedTime(String postedTime) {
        mPostedTime = postedTime;
    }

    public String getPhotoPageUrl(){
        return "http://www.flickr.com/photos/"+ mUserId +"/"+mId;
    }

    public String getDetailPhotoUrl(){
        return "https://farm"+mFarm+".staticflickr.com/"+mServer+"/"+mId+"_"+mSecret+"_b.jpg";
    }

}
