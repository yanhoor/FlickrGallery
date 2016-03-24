package com.example.yanhoor.flickrgallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.User;
import com.example.yanhoor.flickrgallery.util.GetUserProfileUtil;

import org.kymjs.kjframe.KJBitmap;

import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/20.
 */
public class UserProfileFragment extends Fragment {
    private static  final String TAG="UserProfileFragment";

    private User mUser;
    private String mUserId;
    ExpandableHeightGridView userPhotoGridView;
    TextView userName;
    TextView userDescription;
    TextView followingNumber;
    TextView locationTextView;
    TextView location;
    ImageView buddyIconImageView;
    GetUserProfileUtil mGetUserProfileUtil;

    public static final String EXTRA_USER_ID="com.example.yanhoor.flickrgallery.UserProfileFragment.user_Id";

    public static Fragment newUserProfileFragmentInstance(String userId){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_USER_ID,userId);

        UserProfileFragment fragment=new UserProfileFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId=(String)getArguments().getSerializable(EXTRA_USER_ID);
        mUser=new User();
        mUser.setId(mUserId);
        mGetUserProfileUtil=new GetUserProfileUtil();
        mGetUserProfileUtil.getUserProfile(mUserId);
        //updateUserInfo();
        mGetUserProfileUtil.setListener(new GetUserProfileUtil.listener() {
            @Override
            public void onUpdateFinish(User user) {
                mUser=user;
                Log.d(TAG,"userName is "+mUser.getUserName());
                updateUI();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_user_profile,container,false);

        //final SwipeRefreshLayout mSRL=(SwipeRefreshLayout)v.findViewById(R.id.SwipeRefreshLayout_user_profile);
        buddyIconImageView=(ImageView)v.findViewById(R.id.buddy_icon_profile);
        userName=(TextView)v.findViewById(R.id.user_name_profile);
        userDescription=(TextView)v.findViewById(R.id.user_description_profile);
        followingNumber=(TextView)v.findViewById(R.id.following_number_profile);
        locationTextView=(TextView)v.findViewById(R.id.location_profile);
        location=(TextView)v.findViewById(R.id.location_text_profile);
        //使用自定义ExpandableHeightGridView防止与scrollview冲突
        userPhotoGridView=(ExpandableHeightGridView)v.findViewById(R.id.photo_gridView_profile);
        userPhotoGridView.setExpanded(true);

        /*
        mSRL.setColorSchemeColors(R.color.colorGreenLight,R.color.colorOrangeLight,
                R.color.colorRedLight,R.color.colorPrimary);
        mSRL.setProgressBackgroundColorSchemeResource(R.color.colorWhite);

        mSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSRL.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ConnectivityManager connectivityManager=(ConnectivityManager)getActivity()
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                        if (networkInfo!=null&&networkInfo.isAvailable()){
                            mGetUserProfileUtil.getUserProfile(mUserId);
                        }else{
                            Toast.makeText(getActivity(),R.string.networt_unavailable,Toast.LENGTH_SHORT).show();
                        }
                        mSRL.setRefreshing(false);
                    }
                },3000);
            }
        });
        */

        updateUI();

        if (mUser.getGalleryItems().size()!=0){
            userPhotoGridView.setAdapter(new GalleryItemAdapter(mUser.getGalleryItems()));
        }

        userPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GalleryItem item=mUser.getGalleryItems().get(position);
                Intent i=new Intent(getActivity(),PhotoDetailActivity.class);
                //用于代替PhotoDetailActivity实现滑动查看图片详情
                //Intent i=new Intent(getActivity(),PhotoPageActivity.class);
                i.putExtra(PhotoDetailFragment.EXTRA_GALLERYITEM_mId,item.getId());
                startActivity(i);
            }
        });

        return v;
    }

    /*
    public void updateUserInfo(){
        new FetchUserInfoTask().execute();
    }
    */

    void updateUI(){
        //加载icon
        new KJBitmap.Builder().view(buddyIconImageView).imageUrl(mUser.getUserIconUrl()).display();
        if (mUser.getUserName()!=null){
            userName.setText(mUser.getUserName());
        }

        if (mUser.getDescription()!=null){
            userDescription.setText(mUser.getDescription());
        }

        if (mUser.getFollowingsNumber()!=null){
            followingNumber.setText(mUser.getFollowingsNumber());
        }

        //Log.d(TAG,"location length is "+mUser.getLocation().length());
        if (mUser.getLocation()!=null&&mUser.getLocation().length()!=0){
            location.setVisibility(View.VISIBLE);
            locationTextView.setText(mUser.getLocation());
        }

        if (mUser.getGalleryItems().size()>0){
            userPhotoGridView.setAdapter(new GalleryItemAdapter(mUser.getGalleryItems()));
        }else {
            userPhotoGridView.setAdapter(null);
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
        public GalleryItemAdapter(ArrayList<GalleryItem> items){
            super(getActivity(),0,items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG,"arrayAdapter, getView");
            if (convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.image_view,parent,false);
            }
            ImageView imageView=(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brain_up_close);

            new KJBitmap.Builder()
                    .view(imageView)
                    .imageUrl(mUser.getGalleryItems().get(position).getUrl())
                    .size(imageView.getWidth(),imageView.getHeight())
                    .display();

            return convertView;
        }
    }

    /*
    private class FetchUserInfoTask extends AsyncTask<Void,Void,User>{
        @Override
        protected User doInBackground(Void... params) {
            Log.d(TAG,"mUserId is "+mUserId);
            return new GetUserProfileUtil().getUserProfile(mUserId);
        }

        @Override
        protected void onPostExecute(User user) {
            mUser=user;
            Log.d(TAG,"user name is "+mUser.getUserName());
            updateUI();
        }
    }
    */

}
