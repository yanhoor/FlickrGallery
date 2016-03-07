package com.example.yanhoor.photogallery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;
/*
Flickr的key: 0964378968b9ce3044e29838e2fc0cd8
密钥:a0e8c8d18675b5e2
*/

/**
 * Created by yanhoor on 2016/3/3.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG="PhotoGalleryFragment";
    
    GridView mGridView;
    ArrayList<GalleryItem> mItems;
    ThumbnaiDownloader<ImageView> mThumbnaiThread;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        mThumbnaiThread=new ThumbnaiDownloader<>(new Handler());//创建的handler默认与当前线程相关联
        mThumbnaiThread.setListener(new ThumbnaiDownloader.Listener<ImageView>(){
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()){
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnaiThread.start();
        mThumbnaiThread.getLooper();
        Log.d(TAG,"Background thread started");
    }

    public void updateItems(){
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mGridView=(GridView)v.findViewById(R.id.gridView);
        setupAdapter();
        return v;
    }

    void setupAdapter(){
        if (getActivity()==null || mGridView==null) return;

        if (mItems!=null){
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        }else {
            mGridView.setAdapter(null);
        }
    }

    //获取GalleryItem项
    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>>{
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            Activity activity=getActivity();
            if (activity==null)
                return new ArrayList<>();

            String query= PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(FlickrFetchr.PREF_SEARCH_QUERY,null);

            if (query!=null){
                return new FlickrFetchr().search(query);
            }else {
                return new FlickrFetchr().fetchItems();
            }
        }

        //在主线程运行，在doinbackground之后执行
        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mItems=galleryItems;
            setupAdapter();
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem>{
        public GalleryItemAdapter(ArrayList<GalleryItem> items){
            super(getActivity(),0,items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.gallery_item,parent,false);
            }
            ImageView imageView=(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brain_up_close);
            GalleryItem item=getItem(position);
            mThumbnaiThread.queueThumbnail(imageView,item.getUrl());
            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnaiThread.clearQueue();
    }

    @Override
    @TargetApi(11)
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery,menu);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            MenuItem searchItem=menu.findItem(R.id.menu_item_search);
            SearchView searchView=(SearchView)searchItem.getActionView();

            SearchManager searchManager=(SearchManager)getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            ComponentName name=getActivity().getComponentName();
            SearchableInfo searchableInfo=searchManager.getSearchableInfo(name);

            searchView.setSearchableInfo(searchableInfo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;

            case R.id.menu_item_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetchr.PREF_SEARCH_QUERY,null)
                        .commit();
                updateItems();
                return true;

            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm=!PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(),shouldStartAlarm);
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
                    getActivity().invalidateOptionsMenu();//刷新菜单项
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //更新选项菜单，除了菜单的首次创建外，每次菜单需要配置都会调用
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toggleItem=menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())){
            toggleItem.setTitle(R.string.stop_polling);
        }else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnaiThread.quit();//结束线程
        Log.d(TAG,"Background thread destroyed");
    }
}
