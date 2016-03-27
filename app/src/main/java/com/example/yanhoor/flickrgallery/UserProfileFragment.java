package com.example.yanhoor.flickrgallery;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.Group;
import com.example.yanhoor.flickrgallery.model.User;
import com.example.yanhoor.flickrgallery.util.GetUserProfileUtil;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.ImageRequest;
import org.kymjs.kjframe.http.HttpCallBack;

import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/20.
 */
public class UserProfileFragment extends Fragment {
    private static  final String TAG="UserProfileFragment";
    public static String EXTRA_LIST_TYPE_IS_FOLLOWINGS ="type";

    private User mUser;
    private String mUserId;
    private ArrayList<User>mFollowings;
    private ArrayList<Group>mGroups;
    ExpandableHeightGridView userPhotoGridView;
    TextView userName;
    RelativeLayout descriptionLayout;
    TextView userDescription;
    TextView followingNumber;
    RelativeLayout followingLayout;
    TextView groupNumber;
    RelativeLayout groupLayout;
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
        mGetUserProfileUtil.getGroups(getActivity());
        //mGetUserProfileUtil.getPublicGroups();
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

        buddyIconImageView=(ImageView)v.findViewById(R.id.buddy_icon_profile);
        userName=(TextView)v.findViewById(R.id.user_name_profile);
        descriptionLayout=(RelativeLayout)v.findViewById(R.id.description_layout_profile);
        userDescription=(TextView)v.findViewById(R.id.user_description_profile);
        followingLayout=(RelativeLayout)v.findViewById(R.id.following_layout);
        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollowings=new ArrayList<>();
                mFollowings.addAll(mUser.getFollowingUsers());
                if (mFollowings.size()>0){
                    Intent i=new Intent(getActivity(),ListActivity.class);
                    i.putExtra(EXTRA_LIST_TYPE_IS_FOLLOWINGS,true);
                    i.putExtra(ListFollowingsFragment.EXTRA_DATA_FOLLOWINGS,mFollowings);
                    startActivity(i);
                }
            }
        });

        followingNumber=(TextView)v.findViewById(R.id.following_number_profile);
        groupNumber=(TextView)v.findViewById(R.id.group_number_profile);
        groupLayout=(RelativeLayout)v.findViewById(R.id.groupLayout_profile);
        groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroups=new ArrayList<>();
                mGroups.addAll(mUser.getGroups());
                if (mGroups.size()>0){
                    Intent i=new Intent(getActivity(),ListActivity.class);
                    i.putExtra(EXTRA_LIST_TYPE_IS_FOLLOWINGS,false);
                    i.putExtra(ListGroupsFragment.EXTRA_DATA_GROUPS,mGroups);
                    startActivity(i);
                }
            }
        });

        locationTextView=(TextView)v.findViewById(R.id.location_profile);
        location=(TextView)v.findViewById(R.id.location_text_profile);
        //使用自定义ExpandableHeightGridView防止与scrollview冲突
        userPhotoGridView=(ExpandableHeightGridView)v.findViewById(R.id.photo_gridView_profile);
        userPhotoGridView.setExpanded(true);

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

    void updateUI(){
        //加载icon
        new KJBitmap.Builder().view(buddyIconImageView).imageUrl(mUser.getUserIconUrl()).display();
        if (mUser.getUserName()!=null){
            userName.setText(mUser.getUserName());
        }

        if (mUser.getDescription()!=null){
            descriptionLayout.setVisibility(View.VISIBLE);
            userDescription.setText(mUser.getDescription());
        }

        if (mUser.getFollowingsNumber()!=null){
            followingNumber.setText(mUser.getFollowingsNumber());
        }

        if (mUser.getGroups().size()>0){
            groupNumber.setText(String.valueOf(mUser.getGroups().size()));
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
                convertView=getActivity().getLayoutInflater().inflate(R.layout.item_image_view,parent,false);
            }
            final ImageView imageView=(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brain_up_close);

            int maxWidth=imageView.getWidth();
            int maxHeight=imageView.getHeight();
            new ImageRequest(mUser.getGalleryItems().get(position).getUrl(), maxWidth, maxHeight, new HttpCallBack() {
                @Override
                public void onSuccess(Bitmap t) {
                    super.onSuccess(t);
                    imageView.setImageBitmap(t);
                }
            });

            return convertView;
        }
    }

}
