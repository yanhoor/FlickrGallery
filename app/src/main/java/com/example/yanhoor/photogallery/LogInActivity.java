package com.example.yanhoor.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class LogInActivity extends  SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LogInFragment();
    }
}
