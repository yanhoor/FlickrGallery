package com.example.yanhoor.flickrgallery.model;

import java.io.Serializable;

/**
 * Created by yanhoor on 2016/3/27.
 */
public class Topic implements Serializable {
    private static final String TAG="Topic";

    private User mAuthor;
    private Group mGroup;
    private String mId;
    private String mSubject;
    private String mCountReplies;
    private String mDateCreate;
    private String mMessage;

    public User getAuthor() {
        return mAuthor;
    }

    public void setAuthor(User author) {
        mAuthor = author;
    }

    public String getCountReplies() {
        return mCountReplies;
    }

    public void setCountReplies(String countReplies) {
        mCountReplies = countReplies;
    }

    public String getDateCreate() {
        return mDateCreate;
    }

    public void setDateCreate(String dateCreate) {
        mDateCreate = dateCreate;
    }

    public Group getGroup() {
        return mGroup;
    }

    public void setGroup(Group group) {
        mGroup = group;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

}
