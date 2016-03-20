package com.example.yanhoor.flickrgallery.model;

import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/20.
 */
public class User {
    private static final String TAG="User";

    private String mUserName;
    private String mRealName;
    private String mId;
    private String mIconServer;
    private String mIconFarm;
    private String mLocation;

    private ArrayList<User>mFollowingUsers=new ArrayList<>();
    private ArrayList<GalleryItem>mGalleryItems=new ArrayList<>();

    public ArrayList<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }

    public void setGalleryItems(ArrayList<GalleryItem> galleryItems) {
        mGalleryItems = galleryItems;
    }

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

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
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

    public ArrayList<User> getFollowingUsers() {
        if (mFollowingUsers.size()==0){
            return null;
        }else {
            return mFollowingUsers;
        }
    }

    public void setFollowingUsers(ArrayList<User> followingUsers) {
        mFollowingUsers.addAll(followingUsers);
    }

    public String getUserIconUrl(){
        //http://farm{icon-farm}.staticflickr.com/{icon-server}/buddyicons/{nsid}.jpg
        if (Integer.parseInt(mIconServer)>0){
            return "http://farm"+mIconFarm+".staticflickr.com/"+mIconServer+"/buddyicons/"+mId+".jpg";
        }else {
            return "https://www.flickr.com/images/buddyicon.gif";
        }
    }
}
