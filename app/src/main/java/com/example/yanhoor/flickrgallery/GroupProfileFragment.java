package com.example.yanhoor.flickrgallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.yanhoor.flickrgallery.util.GetGroupProfileUtil;
import com.squareup.picasso.Picasso;

import org.kymjs.kjframe.KJBitmap;

import java.util.ArrayList;

/**
 * Created by yanhoor on 2016/3/27.
 */
public class GroupProfileFragment extends Fragment {
    private static final String TAG="GroupProfileFragment ";

    public static String EXTRA_GROUP_ID="groupId";

    private Group mGroup;
    private GetGroupProfileUtil mGetGroupProfileUtil;

    private ImageView mGroupIcon;
    private TextView mGroupName;
    private TextView mMemberNumber;
    private RelativeLayout memberLayout;
    RelativeLayout topicLayout;
    private TextView mTopicNumber;
    private TextView mGroupDescription;
    private ExpandableHeightGridView mGroupPhotoGridview;

    public static GroupProfileFragment newInstance(String mGroupId){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_GROUP_ID,mGroupId);
        GroupProfileFragment fragment=new GroupProfileFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String mGroupId=(String) getArguments().getSerializable(EXTRA_GROUP_ID);
        mGroup=new Group();
        mGroup.setId(mGroupId);

        mGetGroupProfileUtil=new GetGroupProfileUtil();
        mGetGroupProfileUtil.setListener(new GetGroupProfileUtil.GroupListener() {
            @Override
            public void onUpdateFinish(Group group) {
                mGroup = group;
                updateUI();
            }
        });
        mGetGroupProfileUtil.getGroupProfile(mGroupId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_group_profile,container,false);
        mGroupIcon=(ImageView)v.findViewById(R.id.group_icon_profile);
        mGroupName=(TextView)v.findViewById(R.id.group_name_profile);
        memberLayout=(RelativeLayout)v.findViewById(R.id.member_layout_profile);
        mMemberNumber=(TextView)v.findViewById(R.id.member_number_profile);
        topicLayout=(RelativeLayout)v.findViewById(R.id.topic_layout);
        mTopicNumber=(TextView)v.findViewById(R.id.topic_number_profile);
        mGroupDescription=(TextView)v.findViewById(R.id.description_content_groupProfile);
        mGroupPhotoGridview=(ExpandableHeightGridView) v.findViewById(R.id.photo_gridView_groupProfile);
        mGroupPhotoGridview.setExpanded(true);

        mGroupPhotoGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(getActivity(),PhotoDetailActivity.class);
                i.putExtra(PhotoDetailFragment.EXTRA_GALLERYITEM_mId,mGroup.getGalleryItems().get(position).getId());
                startActivity(i);
            }
        });

        memberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroup.getMembers().size()>0){
                    Intent i=new Intent(getActivity(),ListActivity.class);
                    ListActivity.dataType="followings";
                    i.putExtra(ListFollowingsFragment.EXTRA_DATA_FOLLOWINGS,mGroup.getMembers());
                    startActivity(i);
                }
            }
        });

        topicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroup.getTopics().size()>0){
                    Intent i=new Intent(getActivity(),ListActivity.class);
                    i.putExtra(ListTopicsFragment.EXTRA_TOPICS_DATA,mGroup.getTopics());
                    ListActivity.dataType="topics";
                    startActivity(i);
                }
            }
        });

        updateUI();

        return v;
    }

    public void updateUI(){
        new KJBitmap.Builder().view(mGroupIcon).imageUrl(mGroup.getGroupIconUrl()).display();

        if (mGroup.getGroupName()!=null){
            mGroupName.setText(mGroup.getGroupName());
        }

        if (mGroup.getMemberNumber()!=null){
            mMemberNumber.setText(mGroup.getMemberNumber());
        }

        if (mGroup.getTopicsSum()!=null){
            mTopicNumber.setText(mGroup.getTopicsSum());
        }
        if (mGroup.getDescription()!=null){
            mGroupDescription.setText(mGroup.getDescription());
        }

        if (mGroup.getGalleryItems().size()>0){
            mGroupPhotoGridview.setAdapter(new GridViewAdapter(mGroup.getGalleryItems()));
        }else {
            mGroupPhotoGridview.setAdapter(null);
        }

    }

    private class GridViewAdapter extends ArrayAdapter<GalleryItem>{
        public GridViewAdapter(ArrayList<GalleryItem>items){
            super(getActivity(),0,items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.item_image_view,parent,false);
            }
            final ImageView imageView=(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brain_up_close);

            Picasso.with(getActivity())
                    .load(mGroup.getGalleryItems().get(position).getUrl())
                    .resize(240,240)
                    .centerCrop()
                    .into(imageView);

            return convertView;
        }
    }
}
