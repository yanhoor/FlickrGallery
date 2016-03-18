package com.example.yanhoor.photogallery;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yanhoor.photogallery.model.GalleryItem;
import com.example.yanhoor.photogallery.model.GalleryItemLab;
import com.example.yanhoor.photogallery.util.DiskLRUCacheUtil;
import com.example.yanhoor.photogallery.util.StaticMethodUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class PhotoDetailFragment extends Fragment {
    private static final String TAG="PhotoDetailFragment";
    private static final String ENDPOINT="https://api.flickr.com/services/rest/";
    private static final String API_KEY="0964378968b9ce3044e29838e2fc0cd8";
    private static final String METHOD="flickr.stats.getPhotoStats";

    private String mFullToken;

    public static final String EXTRA_GALLERYITEM_uuid ="com.example.yanhoor.photogallery.galleryItem_id";

    private GalleryItem mGalleryItem;

    private int mComment=0;
    private int mViews=0;
    private int mFavorites=0;

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
        mFullToken=PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(LogInFragment.PREF_FULL_TOKEN,null);
        if (mFullToken==null){
            Toast.makeText(getActivity(),R.string.fullToken_unavailable,Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        Date mCurrentDate=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String mCurrentDateString=sdf.format(mCurrentDate);

        //获取api_sig
        String[] mSignFullTokenStringArray={"method"+METHOD,"date"+mCurrentDateString,
                "api_key"+ LogInFragment.API_KEY, "auth_token"+mFullToken,
                LogInFragment.PUBLIC_CODE,"photo_id"+mGalleryItem.getId()};
        Arrays.sort(mSignFullTokenStringArray);
        StringBuilder mSB=new StringBuilder();
        for (String s:mSignFullTokenStringArray) {
            mSB.append(s);
        }
        String apiSig=StaticMethodUtil.countMD5OfString(mSB.toString());
        Log.d(TAG,"apiSig is "+apiSig);

        String url= Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method","flickr.stats.getPhotoStats")
                .appendQueryParameter("api_key",API_KEY)
                .appendQueryParameter("date",sdf.format(mCurrentDate))
                .appendQueryParameter("photo_id",mGalleryItem.getId())
                .appendQueryParameter("auth_token",mFullToken)
                .appendQueryParameter("api_sig",apiSig)
                .build().toString();
        getPhotoStates(getActivity(),url);
        TextView owner=(TextView)v.findViewById(R.id.owner);
        owner.setText(mGalleryItem.getOwner());
        TextView title=(TextView)v.findViewById(R.id.photo_title);
        if (mGalleryItem.getTitle()!=null){
            title.setText(mGalleryItem.getTitle());
        }

        ImageView mImageView=(ImageView) v.findViewById(R.id.photo_imageView);
        mImageView.setImageBitmap(DiskLRUCacheUtil.get(getActivity())
                .getBitmapFromCache(mGalleryItem.getUrl()));


        TextView commentNumber=(TextView)v.findViewById(R.id.comment_number);
        //commentNumber.setText(mComment);

        TextView favoritesNumber=(TextView)v.findViewById(R.id.favorites_number);
       // favoritesNumber.setText(mFavorites);

        TextView viewsNumber=(TextView)v.findViewById(R.id.views_number);
        //viewsNumber.setText(mViews);

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
        Log.d(TAG,"Get photo states from "+url);
        RequestQueue mQueue= Volley.newRequestQueue(context);

        final StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d(TAG,"Response string is "+s);
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(new StringReader(s));
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG && "stats".equals(parser.getName())) {
                            mViews = Integer.parseInt(parser.getAttributeValue(null, "views"));
                            mComment = Integer.parseInt(parser.getAttributeValue(null, "comments"));
                            mFavorites = Integer.parseInt(parser.getAttributeValue(null, "favorites"));
                        }
                        eventType = parser.next();
                    }
                } catch (XmlPullParserException xppe) {
                    xppe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                Log.d(TAG,"Photo states is "+mViews+mComment+mFavorites);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage(), volleyError);
                Log.e(TAG,"onErrorResponse");
            }
        });
        mQueue.add(stringRequest);

    }

}
