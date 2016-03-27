package com.example.yanhoor.flickrgallery.model;

import java.io.Serializable;

/**
 * Created by yanhoor on 2016/3/27.
 */
public class TopicReply implements Serializable {
    private String mTopicId;
    private String mId;
    private User mAuthor;
    private String mDateCreate;
    private String mMessage;

    public User getAuthor() {
        return mAuthor;
    }

    public void setAuthor(User author) {
        mAuthor = author;
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

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }

}
