package com.example.yanhoor.flickrgallery.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/3.
 */
public class GalleryItem implements Serializable{
    private static final String JSON_TITLE="title";
    private static final String JSON_ID="id";
    private static final String JSON_URL="url";
    private static final String JSON_UUID="uuid";
    private static final String JSON_SECRET="secret";
    private static final String JSON_SERVER="server";
    private static final String JSON_FARM="farm";
    private static final String JSON_DESCRIPTION="description";
    private static final String JSON_POSTED_DATE="posted_date";

    private String mTitle;//说明文字
    private String mId;
    private String mUrl;
    private UUID mUUID;
    private String mSecret;
    private String mServer;
    private String mFarm;
    private String isFavorite;
    private String mOriginalsecret;
    private String mOriginalformat;

    //需要调用PhotoInfoUtil才能获取
    private User mOwner;
    private String mDescription=null;
    private String mPostedTime;
    private String mViews;
    private String mPhotoSetId;
    private String mTotalFavoritesNum;
    private ArrayList<User>mFavorites=new ArrayList<>();

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
        mSecret=jsonObject.getString(JSON_SECRET);
        mServer=jsonObject.getString(JSON_SERVER);
        mFarm=jsonObject.getString(JSON_FARM);
        if (jsonObject.has(JSON_DESCRIPTION))
            mDescription=jsonObject.getString(JSON_DESCRIPTION);
        if (jsonObject.has(JSON_POSTED_DATE))
            mPostedTime =jsonObject.getString(JSON_POSTED_DATE);
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JSON_UUID,mUUID.toString());
        jsonObject.put(JSON_TITLE,mTitle);
        jsonObject.put(JSON_ID,mId);
        jsonObject.put(JSON_URL,mUrl);
        jsonObject.put(JSON_SECRET,mSecret);
        jsonObject.put(JSON_SERVER,mServer);
        jsonObject.put(JSON_FARM, mFarm);
        jsonObject.put(JSON_DESCRIPTION,mDescription);
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
        return mOwner.getId();
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

    public User getOwner() {
        return mOwner;
    }

    public void setOwner(User owner) {
        mOwner = owner;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLocation() {
        return mOwner.getLocation();
    }

    public String getPostedTime() {
        return mPostedTime;
    }

    public void setPostedTime(String postedTime) {
        mPostedTime = postedTime;
    }

    public ArrayList<User> getFavorites() {
        return mFavorites;
    }

    public void setFavorites(ArrayList<User> favorites) {
        mFavorites.addAll(favorites);
    }

    public String getViews() {
        return mViews;
    }

    public void setViews(String views) {
        mViews = views;
    }

    public String getTotalFavoritesNum() {
        return mTotalFavoritesNum;
    }

    public void setTotalFavoritesNum(String totalFavoritesNum) {
        mTotalFavoritesNum = totalFavoritesNum;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getOriginalformat() {
        return mOriginalformat;
    }

    public void setOriginalformat(String originalformat) {
        mOriginalformat = originalformat;
    }

    public String getOriginalsecret() {
        return mOriginalsecret;
    }

    public void setOriginalsecret(String originalsecret) {
        mOriginalsecret = originalsecret;
    }

    public String getPhotoSetId() {
        return mPhotoSetId;
    }

    public void setPhotoSetId(String photoSetId) {
        mPhotoSetId = photoSetId;
    }

    public String getPhotoPageUrl(){
        return "http://www.flickr.com/photos/"+ mOwner.getId() +"/"+mId;
    }

    public String getLargePhotoUrl(){
        //<320
        return "https://farm"+mFarm+".staticflickr.com/"+mServer+"/"+mId+"_"+mSecret+"_n.jpg";
    }

    public String getLargestPhotoUrl(){
        //<1024
        return "https://farm"+mFarm+".staticflickr.com/"+mServer+"/"+mId+"_"+mSecret+"_b.jpg";
    }

    public String getOriginalPhotoUrl(){
        return "https://farm"+mFarm+".staticflickr.com/"+mServer+"/"+mId+"_"+mOriginalsecret+"_o."+mOriginalformat;
    }

}
