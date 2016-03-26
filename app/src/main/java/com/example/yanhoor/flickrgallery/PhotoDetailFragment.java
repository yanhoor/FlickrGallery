package com.example.yanhoor.flickrgallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.example.yanhoor.flickrgallery.model.Comment;
import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.User;
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
import java.util.ArrayList;
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

    ImageView ownerIcon;
    TextView userName;
    TextView title;
    TextView description;
    TextView location;
    TextView postedTime;
    ImageView mImageView;
    TextView commentNumber;
    TextView favoritesNumber;
    TextView viewsNumber;
    RecyclerView mRV;

    PhotoInfoUtil mPhotoInfoUtil;
    ArrayList<Comment>mComments;

    public static PhotoDetailFragment newPhotoDetailFragmentInstance(String mId){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_GALLERYITEM_mId,mId);

        PhotoDetailFragment fragment=new PhotoDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"on Create");
        super.onCreate(savedInstanceState);
        String mId=(String) getArguments().getSerializable(EXTRA_GALLERYITEM_mId);
        Log.d(TAG,"mId is "+mId);

        //获取评论
        mComments=new ArrayList<>();
        getComments(mComments,mId);

        //判断是否登录
        mFullToken= PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(LogInFragment.PREF_FULL_TOKEN,null);
        if (mFullToken==null){
            Toast.makeText(getActivity(),R.string.fullToken_unavailable,Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

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

        ownerIcon=(ImageView)v.findViewById(R.id.owner_icon);
        userName=(TextView)v.findViewById(R.id.user_name);
        title=(TextView)v.findViewById(R.id.photo_title);
        description=(TextView)v.findViewById(R.id.Photo_description);
        location=(TextView)v.findViewById(R.id.location_text);
        postedTime=(TextView)v.findViewById(R.id.posted_time_text);
        mImageView=(ImageView) v.findViewById(R.id.photo_imageView);
        commentNumber=(TextView)v.findViewById(R.id.comment_number);
        favoritesNumber=(TextView)v.findViewById(R.id.favorites_number);
        viewsNumber=(TextView)v.findViewById(R.id.views_number);
        mRV=(RecyclerView)v.findViewById(R.id.comment_list_view_RV);

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),UserProfileActivity.class);
                i.putExtra(UserProfileFragment.EXTRA_USER_ID,mGalleryItem.getUserId());
                startActivity(i);
                Log.d(TAG,"Going to user profile");
            }
        });

        updateUI();

        return v;
    }

    void updateUI(){
        User mOwner=mGalleryItem.getOwner();

        Log.d(TAG,"icon url is "+mOwner.getUserIconUrl());
        new KJBitmap.Builder()
                .view(ownerIcon)
                .size(ownerIcon.getWidth(),ownerIcon.getHeight())
                .imageUrl(mOwner.getUserIconUrl())
                .display();

        userName.setText(mOwner.getUserName());

        title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        Log.d(TAG,"title is "+mGalleryItem.getTitle());
        title.setText(mGalleryItem.getTitle());

        Log.d(TAG,"description is "+mGalleryItem.getDescription());
        description.setText(mGalleryItem.getDescription());

        location.setText(mGalleryItem.getLocation());

        postedTime.setText(mGalleryItem.getPostedTime());

        Log.d(TAG,"Getting detail photo from "+mGalleryItem.getDetailPhotoUrl());
        //使用kjbitmap
        new KJBitmap.Builder()
                .view(mImageView)
                .imageUrl(mGalleryItem.getDetailPhotoUrl())
                .display();

        Log.d(TAG,"mComments size is "+mComments.size());
        commentNumber.setText(String.valueOf(mComments.size()));

        favoritesNumber.setText(mFavorites);

        viewsNumber.setText(mViews);

        mRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRV.setItemAnimator(new DefaultItemAnimator());
        mRV.setAdapter(new RVAdapter());

    }

    //获取照片评论数等
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

    //获取评论
    public void getComments(final ArrayList<Comment>requestComments,String id){
        final ArrayList<Comment> comments=new ArrayList<>();

        String url=Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", "flickr.photos.comments.getList")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("photo_id",id)
                .build().toString();

        new KJHttp().get(url, new HttpCallBack() {
            @Override
            public void onFinish() {
                super.onFinish();
                ArrayList<Comment>newComments=new ArrayList<>();
                for (int i=comments.size()-1;i>=0;i--){
                    newComments.add(comments.get(i));
                }
                requestComments.addAll(newComments);
                Log.d(TAG,"mComments size in getComments is "+mComments.size());
                updateUI();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.d(TAG,"Getting comments from "+t);
                try {
                    XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                    XmlPullParser parser=factory.newPullParser();
                    parser.setInput(new StringReader(t));

                    int eventType=parser.getEventType();
                    while (eventType!=XmlPullParser.END_DOCUMENT){
                        if (eventType==XmlPullParser.START_TAG&&"comment".equals(parser.getName())){
                            Comment comment=new Comment();
                            String id=parser.getAttributeValue(null,"id");
                            String author=parser.getAttributeValue(null,"author");
                            String authorName=parser.getAttributeValue(null,"authorname");
                            String iconServer=parser.getAttributeValue(null,"iconserver");
                            String iconFarm=parser.getAttributeValue(null,"farm");
                            String dateCreate=parser.getAttributeValue(null,"datecreate");
                            String content=parser.nextText();

                            //unix timetamp转化为现在的ms要乘1000
                            Date mDate=new Date(Long.parseLong(dateCreate)*1000);
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM-dd HH:mm", Locale.US);
                            String dateString=simpleDateFormat.format(mDate);
                            comment.setId(id);
                            comment.setAuthorId(author);
                            comment.setAuthorName(authorName);
                            comment.setIconServer(iconServer);
                            comment.setIconFarm(iconFarm);
                            comment.setDateCreate(dateString);
                            comment.setContent(content);
                            Log.d(TAG,"Comment content is "+content);
                            comments.add(comment);
                        }
                        eventType=parser.next();
                    }
                }catch (XmlPullParserException xppe){
                    xppe.printStackTrace();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });

    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder>{
        @Override
        public RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RVViewHolder holder=new RVViewHolder(LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_comment,parent,false));

            return holder;
        }

        @Override
        public void onBindViewHolder(final RVViewHolder holder, final int position) {
            holder.authorIcon.setImageResource(R.drawable.brain_up_close);

            int maxWidth=holder.authorIcon.getWidth();
            int maxHeight=holder.authorIcon.getHeight();
            //volley包内
            new ImageRequest(mComments.get(position).getIconUrl(),
                    new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    holder.authorIcon.setImageBitmap(response);
                }
            },
                    maxWidth,maxHeight,null,null);
            /*
            //kjhttp包内
            new ImageRequest(mComments.get(position).getIconUrl(), maxWidth, maxHeight, new HttpCallBack() {
                @Override
                public void onSuccess(Bitmap t) {
                    super.onSuccess(t);
                    holder.authorIcon.setImageBitmap(t);
                }
            });
            /*
            HttpConfig config=new HttpConfig();
            config.CACHEPATH="/storage/sdcard/KJLibrary/cache";
            new KJBitmap.Builder()
                    .view(holder.authorIcon)
                    .imageUrl(mComments.get(position).getIconUrl())
                    .size(holder.authorIcon.getWidth(),holder.authorIcon.getHeight())
                    .display();
                    */
            holder.author.setText(mComments.get(position).getAuthorName());
            holder.content.setText(mComments.get(position).getContent());
            holder.time.setText(mComments.get(position).getDateCreate());

            holder.author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getActivity(),UserProfileActivity.class);
                    i.putExtra(UserProfileFragment.EXTRA_USER_ID,mComments.get(holder.getAdapterPosition()).getAuthorId());
                    startActivity(i);
                    Log.d(TAG,"Going to user profile");
                }
            });

        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        class RVViewHolder extends RecyclerView.ViewHolder{
            ImageView authorIcon;
            TextView author;
            TextView content;
            TextView time;

            public RVViewHolder(View view){
                super(view);
                authorIcon=(ImageView)view.findViewById(R.id.comment_author_icon);
                author=(TextView)view.findViewById(R.id.comment_author);
                content=(TextView)view.findViewById(R.id.comment_content);
                time=(TextView)view.findViewById(R.id.Comment_time);

            }
        }

    }

}
