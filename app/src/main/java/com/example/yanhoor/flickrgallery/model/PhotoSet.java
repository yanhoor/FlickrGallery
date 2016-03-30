package com.example.yanhoor.flickrgallery.model;

/**
 * Created by yanhoor on 2016/3/30.
 */
public class PhotoSet {
    //还有videos="0" count_views="137" count_comments="0" can_comment="1" date_create="1299514498" date_update="1300335009">
    private String mId;
    private String mSecret;
    private String mServer;
    private String mFarm;
    private String mPhotosSum;
    private String mDateCreate;
    private String mDescription;
    private String mTitle;

    public String getDateCreate() {
        return mDateCreate;
    }

    public void setDateCreate(String dateCreate) {
        mDateCreate = dateCreate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getFarm() {
        return mFarm;
    }

    public void setFarm(String farm) {
        mFarm = farm;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPhotosSum() {
        return mPhotosSum;
    }

    public void setPhotosSum(String photosSum) {
        mPhotosSum = photosSum;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public String getServer() {
        return mServer;
    }

    public void setServer(String server) {
        mServer = server;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetailPhotoSetUrl(){
        return "https://farm"+mFarm+".staticflickr.com/"+mServer+"/"+mId+"_"+mSecret+"_b.jpg";
    }

}
