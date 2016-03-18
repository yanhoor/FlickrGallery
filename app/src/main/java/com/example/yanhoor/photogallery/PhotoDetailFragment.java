package com.example.yanhoor.photogallery;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yanhoor.photogallery.model.GalleryItem;
import com.example.yanhoor.photogallery.model.GalleryItemLab;
import com.example.yanhoor.photogallery.util.DiskLRUCacheUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class PhotoDetailFragment extends Fragment {
    private static final String TAG="PhotoDetailFragment";

    public static final String EXTRA_GALLERYITEM_uuid ="com.example.yanhoor.photogallery.galleryItem_id";

    private GalleryItem mGalleryItem;

    private int mComment;
    private int mViews;
    private int mFavorites;

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
        getPhotoStates(getActivity(),mGalleryItem.getUrl());

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


        TextView commentNumber=(TextView)v.findViewById(R.id.commentNumber);
        commentNumber.setText(mComment);

        TextView favoritesNumber=(TextView)v.findViewById(R.id.favorites_number);
        favoritesNumber.setText(mFavorites);

        TextView viewsNumber=(TextView)v.findViewById(R.id.views_number);
        viewsNumber.setText(mViews);

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

    public void getPhotoStates(Context context,String url){
        RequestQueue mQueue= Volley.newRequestQueue(context);

        final StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(new StringReader(s));
                    int eventType = parser.next();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG && "stats".equals(parser.getName())) {
                            mViews = Integer.parseInt(parser.getAttributeValue(null, "views"));
                            mComment = Integer.parseInt(parser.getAttributeValue(null, "comments"));
                            mFavorites = Integer.parseInt(parser.getAttributeValue(null, "favorites"));
                        }
                        eventType = parser.next();
                    }
                    Log.d(TAG,"Photo states is "+mViews);
                } catch (XmlPullParserException xppe) {
                    xppe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG", volleyError.getMessage(), volleyError);
            }
        });
        mQueue.add(stringRequest);

    }

}
