package com.example.yanhoor.flickrgallery;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.Group;
import com.example.yanhoor.flickrgallery.model.User;
import com.example.yanhoor.flickrgallery.util.GetUserProfileUtil;
import com.squareup.picasso.Picasso;

import org.kymjs.kjframe.KJBitmap;

import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/29.
 */
public class AdministratorProfileFragment extends Fragment implements View.OnClickListener{
    private static final String TAG="AdministratorProfile";

    private User mUser;
    private String mId;
    private ArrayList<User> mFollowings;
    private ArrayList<Group>mGroups;
    ExpandableHeightGridView userPhotoGridView;
    TextView userName;
    RelativeLayout descriptionLayout;
    TextView userDescription;
    TextView followingNumber;
    RelativeLayout followingLayout;
    TextView groupNumber;
    RelativeLayout groupLayout;
    RelativeLayout locationLayout;
    TextView locationTextView;
    TextView location;
    ImageView buddyIconImageView;
    GetUserProfileUtil mGetUserProfileUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mId= PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(LogInFragment.PREF_USER_ID,null);
        Log.d(TAG,"mId is "+mId);
        if (mId==null){
            Toast.makeText(getActivity(),R.string.fullToken_unavailable,Toast.LENGTH_SHORT).show();
            onDestroy();
            Intent i=new Intent(getActivity(),LogInActivity.class);
            startActivity(i);
        }
        mUser=new User();
        mUser.setId(mId);
        mGetUserProfileUtil=new GetUserProfileUtil();
        mGetUserProfileUtil.getUserProfile(mId);
        mGetUserProfileUtil.getGroups(getActivity());
        mGetUserProfileUtil.setListener(new GetUserProfileUtil.listener() {
            @Override
            public void onUpdateFinish(User user) {
                mUser =user;
                updateUI();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_administrator_profile,container,false);

        buddyIconImageView=(ImageView)v.findViewById(R.id.buddy_icon_administratorProfile);
        userName=(TextView)v.findViewById(R.id.user_name_administratorProfile);
        descriptionLayout=(RelativeLayout)v.findViewById(R.id.description_layout_administratorProfile);
        userDescription=(TextView)v.findViewById(R.id.user_description_administratorProfile);
        followingLayout=(RelativeLayout)v.findViewById(R.id.following_layout_administratorProfile);
        followingLayout.setOnClickListener(this);

        followingNumber=(TextView)v.findViewById(R.id.following_number_administratorProfile);
        groupNumber=(TextView)v.findViewById(R.id.group_number_administratorProfile);
        groupLayout=(RelativeLayout)v.findViewById(R.id.groupLayout_administratorProfile);
        groupLayout.setOnClickListener(this);

        locationLayout=(RelativeLayout)v.findViewById(R.id.location_layout_administratorProfile);
        locationTextView=(TextView)v.findViewById(R.id.location_administratorProfile);
        location=(TextView)v.findViewById(R.id.location_text_administratorProfile);
        //使用自定义ExpandableHeightGridView防止与scrollview冲突
        userPhotoGridView=(ExpandableHeightGridView)v.findViewById(R.id.photo_gridView_administratorProfile);
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
                i.putExtra(PhotoDetailFragment.EXTRA_GALLERYITEM_mId,item.getId());
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.following_layout_administratorProfile:
                mFollowings=new ArrayList<>();
                mFollowings.addAll(mUser.getFollowingUsers());
                if (mFollowings.size()>0){
                    Intent i=new Intent(getActivity(),ListActivity.class);
                    ListActivity.dataType="followings";
                    i.putExtra(ListFollowingsFragment.EXTRA_DATA_FOLLOWINGS,mFollowings);
                    startActivity(i);
                }
                break;

            case R.id.groupLayout_administratorProfile:
                mGroups=new ArrayList<>();
                mGroups.addAll(mUser.getGroups());
                if (mGroups.size()>0){
                    Intent i=new Intent(getActivity(),ListActivity.class);
                    ListActivity.dataType="groups";
                    i.putExtra(ListGroupsFragment.EXTRA_DATA_GROUPS,mGroups);
                    startActivity(i);
                }
                break;

            default:
                break;
        }
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
            locationLayout.setVisibility(View.VISIBLE);
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
            super(getActivity().getApplicationContext(),0,items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG,"arrayAdapter, getView");
            if (convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.item_image_view,parent,false);
            }
            final ImageView imageView=(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brain_up_close);

            Picasso.with(getActivity())
                    .load(mUser.getGalleryItems().get(position).getUrl())
                    .resize(240,240)
                    .centerCrop()
                    .into(imageView);

            return convertView;
        }
    }

}
