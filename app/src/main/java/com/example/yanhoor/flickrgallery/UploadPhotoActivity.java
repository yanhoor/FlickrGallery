package com.example.yanhoor.flickrgallery;

import android.support.v4.app.Fragment;

/**
 * Created by yanhoor on 2016/3/31.
 */
public class UploadPhotoActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new UploadPhotoFragment();
    }
}
