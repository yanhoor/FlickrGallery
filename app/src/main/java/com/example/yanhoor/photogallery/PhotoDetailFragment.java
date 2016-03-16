package com.example.yanhoor.photogallery;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yanhoor.photogallery.model.GalleryItem;
import com.example.yanhoor.photogallery.model.GalleryItemLab;

import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class PhotoDetailFragment extends Fragment {
    private static final String TAG="PhotoDetailFragment";

    public static final String EXTRA_GALLERYITEM_uuid ="com.example.yanhoor.photogallery.galleryItem_id";

    private GalleryItem mGalleryItem;

    ThumbnaiDownloader<ImageView> mThumbnaiThread;

    public static PhotoDetailFragment newPhotoDetailFragmentInstance(UUID mUUID){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_GALLERYITEM_uuid,mUUID);

        PhotoDetailFragment fragment=new PhotoDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID mUUID=(UUID) getArguments().getSerializable(EXTRA_GALLERYITEM_uuid);
        mGalleryItem= GalleryItemLab.get(getActivity()).getGalleryItem(mUUID);

        mThumbnaiThread=new ThumbnaiDownloader<>(getActivity(),new Handler());//创建的handler默认与当前线程相关联
        mThumbnaiThread.setListener( new ThumbnaiDownloader.Listener<ImageView>(){
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()){
                    Log.d(TAG,"setListener");
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnaiThread.start();
        mThumbnaiThread.getLooper();
        Log.d(TAG,"Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_detail,container,false);
        TextView owner=(TextView)v.findViewById(R.id.owner);
        owner.setText(mGalleryItem.getOwner());

        ImageView mImageView=(ImageView) v.findViewById(R.id.photo_imageView);
        mThumbnaiThread.queueThumbnail(mImageView,mGalleryItem.getUrl());

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnaiThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnaiThread.quit();//结束线程
        Log.d(TAG,"Background thread destroyed");
    }

}
