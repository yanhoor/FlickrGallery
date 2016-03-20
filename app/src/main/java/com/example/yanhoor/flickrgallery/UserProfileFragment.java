package com.example.yanhoor.flickrgallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yanhoor.flickrgallery.model.User;
import com.example.yanhoor.flickrgallery.model.UserLab;

/**
 * Created by yanhoor on 2016/3/20.
 */
public class UserProfileFragment extends Fragment {
    private static  final String TAG="UserProfileFragment";

    private User mUser;

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
        String userId=(String)getArguments().getSerializable(EXTRA_USER_ID);
        mUser= UserLab.get(getActivity()).getUser(userId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_user_profile,container,false);

        return v;
    }

}
