package com.example.yanhoor.flickrgallery.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/20.
 */
public class UserLab {
    private static final String TAG="UserLab";

    private ArrayList<User>mUsers;
    private Context mAppContext;

    private static UserLab sUserLab;

    private UserLab(Context appContext){
        mAppContext=appContext;
        mUsers=new ArrayList<>();
    }

    public static UserLab get(Context c){
        if (sUserLab==null){
            sUserLab=new UserLab(c.getApplicationContext());
        }
        return sUserLab;
    }

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public void setUsers(ArrayList<User> users) {
        mUsers.addAll(users);
    }

    public User getUser(String userId){
        for (User u:mUsers){
            if (u.getId().equals(userId)){
                return u;
            }
        }
        return null;
    }

}
