package com.example.yanhoor.flickrgallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.GalleryItemLab;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/20.
 */
//用于代替PhotoDetailActivity实现滑动查看图片详情
public class PhotoDetailPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<GalleryItem> mGalleryItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager=new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mGalleryItems= GalleryItemLab.get(this).getGalleryItems();

        FragmentManager fm=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                GalleryItem galleryItem=mGalleryItems.get(position);
                return PhotoDetailFragment.newPhotoDetailFragmentInstance(galleryItem.getId());
            }

            @Override
            public int getCount() {
                return mGalleryItems.size();
            }
        });

        UUID galleryItemUUID=(UUID)getIntent()
                .getSerializableExtra(PhotoDetailFragment.EXTRA_GALLERYITEM_mId);
        for (int i=0;i<mGalleryItems.size();i++){
            if (mGalleryItems.get(i).getUUID().equals(galleryItemUUID)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
