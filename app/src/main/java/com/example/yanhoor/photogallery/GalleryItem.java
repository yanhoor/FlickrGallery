package com.example.yanhoor.photogallery;

/**
 * Created by yanhoor on 2016/3/3.
 */
public class GalleryItem {
    private String mCaption;//说明文字
    private String mId;
    private String mUrl;
    private String mOwner;

    public String toString(){
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
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
    public String getPhotoPageUrl(){
        return "http://www.flickr.com/photos/"+mOwner+"/"+mId;
    }

}
