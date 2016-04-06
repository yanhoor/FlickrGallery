package com.example.yanhoor.flickrgallery;

import android.support.v4.app.Fragment;

/**
 * Created by yanhoor on 2016/4/6.
 */
public class PhotoViewActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        String url=getIntent().getStringExtra(PhotoViewFragment.EXTRA_PHOTO_URL);

        return PhotoViewFragment.newInstance(url);
    }
}
