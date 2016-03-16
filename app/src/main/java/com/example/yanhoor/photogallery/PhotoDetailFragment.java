package com.example.yanhoor.photogallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yanhoor.photogallery.model.GalleryItem;
import com.example.yanhoor.photogallery.model.GalleryItemLab;
import com.example.yanhoor.photogallery.util.DiskLRUCacheUtil;

import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class PhotoDetailFragment extends Fragment {
    private static final String TAG="PhotoDetailFragment";

    public static final String EXTRA_GALLERYITEM_uuid ="com.example.yanhoor.photogallery.galleryItem_id";

    private GalleryItem mGalleryItem;

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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_detail,container,false);
        TextView owner=(TextView)v.findViewById(R.id.owner);
        owner.setText(mGalleryItem.getOwner());
        TextView title=(TextView)v.findViewById(R.id.photo_title);
        if (mGalleryItem.getTitle()!=null){
            title.setText(mGalleryItem.getTitle());
        }

        ImageView mImageView=(ImageView) v.findViewById(R.id.photo_imageView);
        mImageView.setImageBitmap(DiskLRUCacheUtil.get(getActivity())
                .getBitmapFromCache(mGalleryItem.getUrl()));

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
