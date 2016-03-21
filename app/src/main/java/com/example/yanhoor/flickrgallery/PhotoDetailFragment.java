package com.example.yanhoor.flickrgallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.GalleryItemLab;
import com.example.yanhoor.flickrgallery.util.StaticMethodUtil;

import org.kymjs.kjframe.KJBitmap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class PhotoDetailFragment extends Fragment {
    private static final String TAG="PhotoDetailFragment";
    private static final String ENDPOINT="https://api.flickr.com/services/rest/";
    private static final String API_KEY="0964378968b9ce3044e29838e2fc0cd8";
    private static final String METHOD="flickr.stats.getPhotoStats";

    private String mFullToken;

    public static final String EXTRA_GALLERYITEM_mId ="com.example.yanhoor.photogallery.galleryItem_id";

    private GalleryItem mGalleryItem;

    private String mComment="0";
    private String mViews="0";
    private String mFavorites="0";

    public static PhotoDetailFragment newPhotoDetailFragmentInstance(String mId){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_GALLERYITEM_mId,mId);

        PhotoDetailFragment fragment=new PhotoDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mId=(String) getArguments().getSerializable(EXTRA_GALLERYITEM_mId);
        mGalleryItem= GalleryItemLab.get(getActivity()).getGalleryItem(mId);
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

        //为flickr.stats.getPhotoStats方法获取api_sig
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

        Log.d(TAG,"username is "+mGalleryItem.getUserName());
        TextView userName=(TextView)v.findViewById(R.id.user_name);
        userName.setText(mGalleryItem.getUserName());
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),UserProfileActivity.class);
                i.putExtra(UserProfileFragment.EXTRA_USER_ID,mGalleryItem.getUserId());
                startActivity(i);
                Log.d(TAG,"Going to user profile");
            }
        });

        TextView title=(TextView)v.findViewById(R.id.photo_title);
        title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        if (mGalleryItem.getTitle()!=null){
            title.setText(mGalleryItem.getTitle());
        }else {
            title.setVisibility(View.GONE);
        }

        TextView description=(TextView)v.findViewById(R.id.Photo_description);
        Log.d(TAG,"description is "+mGalleryItem.getDescription());
        if (mGalleryItem.getDescription()!=null){
            description.setText(mGalleryItem.getDescription());
        }else {
            description.setVisibility(View.GONE);
        }

        TextView location=(TextView)v.findViewById(R.id.location_text);
        if (mGalleryItem.getLocation()!=null){
            location.setText(mGalleryItem.getLocation());
        }else {
            location.setVisibility(View.GONE);
        }

        TextView postedTime=(TextView)v.findViewById(R.id.posted_time_text);
        //防止存在空格出现java.lang.NumberFormatException: Invalid long: "null"
        String mPostedTime=mGalleryItem.getPostedDate().trim();
        //unix timetamp转化为现在的ms要乘1000
        Date mDate=new Date(Long.parseLong(mPostedTime)*1000);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US);
        String dateString=simpleDateFormat.format(mDate);
        postedTime.setText(dateString);

        ImageView mImageView=(ImageView) v.findViewById(R.id.photo_imageView);
        Log.d(TAG,"Getting detail photo from "+mGalleryItem.getDetailPhotoUrl());
        //使用kjbitmap
        new KJBitmap.Builder().view(mImageView).imageUrl(mGalleryItem.getDetailPhotoUrl()).display();

        TextView commentNumber=(TextView)v.findViewById(R.id.comment_number);
        commentNumber.setText(mComment);

        TextView favoritesNumber=(TextView)v.findViewById(R.id.favorites_number);
        favoritesNumber.setText(mFavorites);

        TextView viewsNumber=(TextView)v.findViewById(R.id.views_number);
        viewsNumber.setText(mViews);

        return v;
    }

    //获取照片评论等
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
                        if ("err".equals(parser.getName())){
                            Log.d(TAG,"Error occur when getting states");
                            break;
                        }else if (eventType == XmlPullParser.START_TAG && "stats".equals(parser.getName())) {
                            mViews = parser.getAttributeValue(null, "views");
                            mComment = parser.getAttributeValue(null, "comments");
                            mFavorites = parser.getAttributeValue(null, "favorites");
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
