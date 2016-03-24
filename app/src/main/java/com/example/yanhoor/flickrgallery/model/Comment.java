package com.example.yanhoor.flickrgallery.model;

import android.util.Log;

/**
 * Created by yanhoor on 2016/3/23.
 */
public class Comment {
    private static final String TAG="Comment";

    private String mId;
    private String mAuthorId;
    private String mAuthorName;
    private String mDateCreate;
    private String mContent;
    private String mIconFarm;
    private String mIconServer;

    public String getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(String authorId) {
        mAuthorId = authorId;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public String getDateCreate() {
        return mDateCreate;
    }

    public void setDateCreate(String dateCreate) {
        mDateCreate = dateCreate;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
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

    public String getIconUrl(){
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
        Log.d(TAG,"IconUrl is "+iconUrl);
        return iconUrl;
    }

}
