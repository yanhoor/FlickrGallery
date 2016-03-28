package com.example.yanhoor.flickrgallery.model;

import java.io.Serializable;

/**
 * Created by yanhoor on 2016/3/23.
 */
public class Comment implements Serializable{
    private static final String TAG="Comment";

    private String mId;
    private User mAuthor;
    private String mDateCreate;
    private String mContent;

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

    public User getAuthor() {
        return mAuthor;
    }

    public void setAuthor(User author) {
        mAuthor = author;
    }
}
