package com.example.yanhoor.flickrgallery;

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

import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.util.PhotoInfoUtil;
import com.example.yanhoor.flickrgallery.util.StaticMethodUtil;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
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

    TextView userName;
    TextView title;
    TextView description;
    TextView location;
    TextView postedTime;
    ImageView mImageView;
    TextView commentNumber;
    TextView favoritesNumber;
    TextView viewsNumber;
    PhotoInfoUtil mPhotoInfoUtil;

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

        //判断是否登录
        mFullToken= PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(LogInFragment.PREF_FULL_TOKEN,null);
        if (mFullToken==null){
            Toast.makeText(getActivity(),R.string.fullToken_unavailable,Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        String mId=(String) getArguments().getSerializable(EXTRA_GALLERYITEM_mId);
        mGalleryItem= new GalleryItem();
        mGalleryItem.setId(mId);

        //用于获取照片信息后调用更新UI
        mPhotoInfoUtil=new PhotoInfoUtil();
        mPhotoInfoUtil.setListener(new PhotoInfoUtil.listener() {
            @Override
            public void onUpdateFinish(GalleryItem galleryItem) {
                mGalleryItem=galleryItem;
                updateUI();
            }
        });

        //获取照片信息，userName,description,location等
        mGalleryItem=mPhotoInfoUtil.getPhotoInfo(mGalleryItem);
        getPhotoStates();//获取照片comment,favorites,views等
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_detail,container,false);
        userName=(TextView)v.findViewById(R.id.user_name);
        title=(TextView)v.findViewById(R.id.photo_title);
        description=(TextView)v.findViewById(R.id.Photo_description);
        location=(TextView)v.findViewById(R.id.location_text);
        postedTime=(TextView)v.findViewById(R.id.posted_time_text);
        mImageView=(ImageView) v.findViewById(R.id.photo_imageView);
        commentNumber=(TextView)v.findViewById(R.id.comment_number);
        favoritesNumber=(TextView)v.findViewById(R.id.favorites_number);
        viewsNumber=(TextView)v.findViewById(R.id.views_number);

        updateUI();

        return v;
    }

    void updateUI(){
        Log.d(TAG,"username is "+mGalleryItem.getUserName());
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

        title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        Log.d(TAG,"title is "+mGalleryItem.getTitle());
            title.setText(mGalleryItem.getTitle());

        Log.d(TAG,"description is "+mGalleryItem.getDescription());
            description.setText(mGalleryItem.getDescription());

            location.setText(mGalleryItem.getLocation());

        postedTime.setText(mGalleryItem.getPostedTime());

        Log.d(TAG,"Getting detail photo from "+mGalleryItem.getDetailPhotoUrl());
        //使用kjbitmap
        new KJBitmap.Builder().view(mImageView).imageUrl(mGalleryItem.getDetailPhotoUrl()).display();

        commentNumber.setText(mComment);

        favoritesNumber.setText(mFavorites);

        viewsNumber.setText(mViews);

    }

    //获取照片评论等
    public void getPhotoStates() {
        Date mCurrentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String mCurrentDateString = sdf.format(mCurrentDate);

        //为flickr.stats.getPhotoStats方法获取api_sig
        String[] mSignFullTokenStringArray = {"method" + METHOD, "date" + mCurrentDateString,
                "api_key" + LogInFragment.API_KEY, "auth_token" + mFullToken,
                LogInFragment.PUBLIC_CODE, "photo_id" + mGalleryItem.getId()};
        Arrays.sort(mSignFullTokenStringArray);
        StringBuilder mSB = new StringBuilder();
        for (String s : mSignFullTokenStringArray) {
            mSB.append(s);
        }
        String apiSig = StaticMethodUtil.countMD5OfString(mSB.toString());
        Log.d(TAG, "apiSig is " + apiSig);

        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", "flickr.stats.getPhotoStats")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("date", sdf.format(mCurrentDate))
                .appendQueryParameter("photo_id", mGalleryItem.getId())
                .appendQueryParameter("auth_token", mFullToken)
                .appendQueryParameter("api_sig", apiSig)
                .build().toString();
        Log.d(TAG, "Get photo states from " + url);

        KJHttp kjHttp = new KJHttp();
        kjHttp.get(url, new HttpCallBack() {
            @Override
            public void onFinish() {
                super.onFinish();
                updateUI();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.d(TAG, "Response string is " + t);
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(new StringReader(t));
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if ("err".equals(parser.getName())) {
                            Log.d(TAG, "Error occur when getting states");
                            break;
                        } else if (eventType == XmlPullParser.START_TAG && "stats".equals(parser.getName())) {
                            mViews = parser.getAttributeValue(null, "views");
                            mComment = parser.getAttributeValue(null, "comments");
                            mFavorites = parser.getAttributeValue(null, "favorites");
                        }
                        eventType = parser.next();
                    }
                }catch (XmlPullParserException xppe){
                    xppe.printStackTrace();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
    }

}
