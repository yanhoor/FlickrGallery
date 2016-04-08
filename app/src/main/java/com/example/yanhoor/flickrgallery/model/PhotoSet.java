package com.example.yanhoor.flickrgallery.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/30.
 */
public class PhotoSet  implements Serializable{
    //还有videos="0" count_views="137" count_comments="0" can_comment="1" date_create="1299514498" date_update="1300335009">
    private String mId;
    private String mSecret;
    private String mServer;
    private String mFarm;
    private String mCount_photos;
    private String mCount_comments;
    private String mCount_views;
    private String mDateCreate;
    private String mDateUpdate;
    private String mDescription;
    private String mTitle;
    private String mOwnerId;
    private String mPrimary;
    private String mPrimaryPhotoUrl;//在getList获得
    private ArrayList<GalleryItem>mGalleryItems=new ArrayList<>();

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

    public String getCount_photos() {
        return mCount_photos;
    }

    public void setCount_photos(String count_photos) {
        mCount_photos = count_photos;
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

    public String getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(String ownerId) {
        mOwnerId = ownerId;
    }

    public String getDateUpdate() {
        return mDateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        mDateUpdate = dateUpdate;
    }

    public String getCount_views() {
        return mCount_views;
    }

    public void setCount_views(String count_views) {
        mCount_views = count_views;
    }

    public String getCount_comments() {
        return mCount_comments;
    }

    public void setCount_comments(String count_comments) {
        mCount_comments = count_comments;
    }

    public String getPrimary() {
        return mPrimary;
    }

    public void setPrimary(String primary) {
        mPrimary = primary;
    }

    public ArrayList<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }

    public void setGalleryItems(ArrayList<GalleryItem> galleryItems) {
        mGalleryItems.addAll(galleryItems);
    }

    public String getPrimaryPhotoUrl() {
        return mPrimaryPhotoUrl;
    }

    public void setPrimaryPhotoUrl(String primaryPhotoUrl) {
        mPrimaryPhotoUrl = primaryPhotoUrl;
    }

    public String setupPrimaryPhotoUrl(){
        return "https://farm"+mFarm+".staticflickr.com/"+mServer+"/"+mPrimary+"_"+mSecret+"_m.jpg";
    }

}
