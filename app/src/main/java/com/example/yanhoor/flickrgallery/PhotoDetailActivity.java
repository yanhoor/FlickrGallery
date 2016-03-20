package com.example.yanhoor.flickrgallery;

import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class PhotoDetailActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        UUID mUUID=(UUID) getIntent().getSerializableExtra(PhotoDetailFragment.EXTRA_GALLERYITEM_uuid);

        return PhotoDetailFragment.newPhotoDetailFragmentInstance(mUUID);
    }
}
