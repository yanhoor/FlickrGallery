package com.example.yanhoor.flickrgallery;

import android.support.v4.app.Fragment;

/**
 * Created by yanhoor on 2016/3/20.
 */
public class UserProfileActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String mUserId=(String)getIntent().getSerializableExtra(UserProfileFragment.EXTRA_USER_ID);

        return UserProfileFragment.newUserProfileFragmentInstance(mUserId);
    }
}
