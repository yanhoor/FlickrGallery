package com.example.yanhoor.flickrgallery.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/25.
 */
public class Group implements Serializable {
    private static final String TAG="Group";

    //使用flickr.people.getGroups获得
    private String mId;
    private String mGroupName;
    private String mIconFarm;
    private String mIconServer;
    private String mMemberNumber;
    private String mPool_count;//照片数

    private String mDescription;

    //使用flickr.groups.discuss.topics.getList获取
    private ArrayList<Topic>mTopics=new ArrayList<>();
    private ArrayList<User>mMembers=new ArrayList<>();

    private ArrayList<GalleryItem>mGalleryItems=new ArrayList<>();

    public String getIconFarm() {
        return mIconFarm;
    }

    public void setIconFarm(String iconFarm) {
        mIconFarm = iconFarm;
    }

    public String getIconServer() {
        return mIconServer;
    }

    public void setIconServer(String iconServer) {
        mIconServer = iconServer;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMemberNumber() {
        return mMemberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        mMemberNumber = memberNumber;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public String getPool_count() {
        return mPool_count;
    }

    public void setPool_count(String pool_count) {
        mPool_count = pool_count;
    }

    public ArrayList<User> getMembers() {
        return mMembers;
    }

    public void setMembers(ArrayList<User> members) {
        mMembers.addAll(members);
    }

    public ArrayList<Topic> getTopics() {
        return mTopics;
    }

    public void setTopics(ArrayList<Topic> topics) {
        mTopics.addAll(topics);
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public ArrayList<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }

    public void setGalleryItems(ArrayList<GalleryItem> galleryItems) {
        mGalleryItems.addAll(galleryItems);
    }

    public String getGroupIconUrl(){
        String iconUrl;
        //http://farm{icon-farm}.staticflickr.com/{icon-server}/buddyicons/{nsid}.jpg
        if (mIconServer!=null){
            if (Integer.parseInt(mIconServer)>0){
                iconUrl= "http://farm"+mIconFarm+".staticflickr.com/"+mIconServer+"/buddyicons/"+mId+".jpg";
            }else {
                iconUrl= "https://www.flickr.com/images/buddyicon.gif";
            }
        }else {
            iconUrl= "https://www.flickr.com/images/buddyicon.gif";
        }
        Log.d(TAG,"Group IconUrl is "+iconUrl);
        return iconUrl;
    }

}
