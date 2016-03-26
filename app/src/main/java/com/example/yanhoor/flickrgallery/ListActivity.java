package com.example.yanhoor.flickrgallery;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.yanhoor.flickrgallery.model.Group;
import com.example.yanhoor.flickrgallery.model.User;

import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/26.
 */
public class ListActivity extends SingleFragmentActivity {
    private static final String TAG="ListActivity";

    @Override
    protected Fragment createFragment() {
        boolean type=(boolean)getIntent().getSerializableExtra(UserProfileFragment.EXTRA_LIST_TYPE_IS_FOLLOWINGS);
        Log.d(TAG,"type is "+type);

        if (!type){
            Log.d(TAG,"Start ListGroupsFragment");
            ArrayList<Group> mGroups=(ArrayList<Group>) getIntent()
                    .getSerializableExtra(ListGroupsFragment.EXTRA_DATA_GROUPS);
            return ListGroupsFragment.newInstance(mGroups);
        }else {
            Log.d(TAG,"Start ListFollowingsFragment");
            ArrayList<User>mFollowings=(ArrayList<User>)getIntent()
                    .getSerializableExtra(ListFollowingsFragment.EXTRA_DATA_FOLLOWINGS);
            return ListFollowingsFragment.newInstance(mFollowings);
        }
    }
}
