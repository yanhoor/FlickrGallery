package com.example.yanhoor.flickrgallery.model;

/**
 * Created by yanhoor on 2016/3/25.
 */
public class Group {
    //使用flickr.people.getGroups获得
    private String mId;
    private String mGroupName;
    private String mIconFarm;
    private String mIconServer;
    private String mMemberNumber;
    private String mPool_count;//照片数

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
}
