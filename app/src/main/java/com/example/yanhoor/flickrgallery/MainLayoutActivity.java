package com.example.yanhoor.flickrgallery;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * Created by yanhoor on 2016/3/10.
 */
public class MainLayoutActivity extends FragmentActivity {
    private static final String TAG="MainLayoutActivity";

    public static String fullToken;
    public static String administratorId;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"on create");
        setContentView(R.layout.activity_main_layout);

        fullToken=PreferenceManager.getDefaultSharedPreferences(this)
                .getString(LogInFragment.PREF_FULL_TOKEN,null);

        administratorId=PreferenceManager.getDefaultSharedPreferences(this)
                .getString(LogInFragment.PREF_USER_ID,null);

        mTabLayout=(TabLayout) findViewById(R.id.tablayout);
        mViewPager=(ViewPager)findViewById(R.id.viewPager);

        FragmentManager fm=getSupportFragmentManager();
        PagerAdapter pagerAdapter=new PagerAdapter(fm);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

    }

    public class PagerAdapter extends FragmentStatePagerAdapter{

        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new PhotoGalleryFragment();
                case 1:
                    return new AdministratorProfileFragment();
                default:
                    return new PhotoGalleryFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "New photo";
                case 1:
                default:
                    return "Profile";
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment fragment=(PhotoGalleryFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query=intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG,"Received a new search query: "+query);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(FlickrFetchr.PREF_SEARCH_QUERY,query)
                    .commit();
        }
        if (fragment!=null){
            fragment.updateItems();
        }
    }

}
