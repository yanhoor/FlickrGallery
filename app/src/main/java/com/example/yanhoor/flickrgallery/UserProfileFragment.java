package com.example.yanhoor.flickrgallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
    GridView userPhotoGridView;
    TextView userName;
    TextView followingNumber;
    TextView locationTextView;
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

        ImageView buddyIconImageView=(ImageView)v.findViewById(R.id.buddy_icon_profile);
        userName=(TextView)v.findViewById(R.id.user_name_profile);
        followingNumber=(TextView)v.findViewById(R.id.following_number_profile);
        locationTextView=(TextView)v.findViewById(R.id.location_profile);
        userPhotoGridView=(GridView)v.findViewById(R.id.photo_gridView_profile);

        updateUI();
        //加载icon
        new KJBitmap.Builder().view(buddyIconImageView).imageUrl(mUser.getUserIconUrl()).display();

        if (mUser.getGalleryItems().size()!=0){
            userPhotoGridView.setAdapter(new GalleryItemAdapter(mUser.getGalleryItems()));
        }

        return v;
    }

    /*
    public void updateUserInfo(){
        new FetchUserInfoTask().execute();
    }
    */

    void updateUI(){
        if (mUser.getUserName()!=null){
            userName.setText(mUser.getUserName());
        }

        if (mUser.getGalleryItems()!=null){
            followingNumber.setText(String.valueOf(mUser.getFollowingUsers().size()));
        }

        if (mUser.getLocation()!=null){
            locationTextView.setText(mUser.getLocation());
        }else {
            locationTextView.setVisibility(View.GONE);
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
            new KJBitmap.Builder().view(imageView).imageUrl(mUser.getGalleryItems().get(position).getUrl()).display();
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
