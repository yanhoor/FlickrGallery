package com.example.yanhoor.flickrgallery;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.Toolbar;

/**
 * Created by yanhoor on 2016/3/10.
 */
public class MainLayoutActivity extends FragmentActivity {
    private static final String TAG="MainLayoutActivity";

    public static String fullToken;
    public static String administratorId;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"on create");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_layout);

        fullToken=PreferenceManager.getDefaultSharedPreferences(this)
                .getString(LogInFragment.PREF_FULL_TOKEN,null);

        administratorId=PreferenceManager.getDefaultSharedPreferences(this)
                .getString(LogInFragment.PREF_USER_ID,null);

        mTabLayout=(TabLayout) findViewById(R.id.tabLayout);
        mViewPager=(ViewPager)findViewById(R.id.viewPager);
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle(null);
        setActionBar(mToolbar);

        FragmentManager fm=getSupportFragmentManager();
        pagerAdapter=new PagerAdapter(fm);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_photo_gallery,menu);

        MenuItem searchItem=menu.findItem(R.id.menu_item_search);
        SearchView searchView=(SearchView)searchItem.getActionView();

        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        ComponentName name=getComponentName();
        SearchableInfo searchableInfo=searchManager.getSearchableInfo(name);

        searchView.setSearchableInfo(searchableInfo);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_search:
                Intent searchIntent=new Intent(this,SearchActivity.class);
                startActivity(searchIntent);
                return true;

            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm=!PollService.isServiceAlarmOn(this);
                PollService.setServiceAlarm(this,shouldStartAlarm);
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
                    invalidateOptionsMenu();//刷新菜单项
                return true;

            case R.id.menu_item_login:
                Intent i=new Intent(this,LogInActivity.class);
                startActivity(i);
                return true;

            case R.id.menu_item_upload_photo:
                Intent uploadIntent=new Intent(this,UploadPhotoActivity.class);
                startActivity(uploadIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //更新选项菜单，除了菜单的首次创建外，每次菜单需要配置都会调用
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toggleItem=menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(this)){
            toggleItem.setTitle(R.string.stop_polling);
        }else {
            toggleItem.setTitle(R.string.start_polling);
        }

        return true;
    }

}
