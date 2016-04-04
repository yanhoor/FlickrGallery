package com.example.yanhoor.flickrgallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yanhoor.flickrgallery.model.GalleryItem;
import com.example.yanhoor.flickrgallery.model.GalleryItemLab;

import java.util.ArrayList;
/*
Flickr的key: 0964378968b9ce3044e29838e2fc0cd8
密钥:a0e8c8d18675b5e2
*/

/**
 * Created by yanhoor on 2016/3/3.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private static final String TAG="PhotoGalleryFragment";

    GridView mGridView;
    ArrayList<GalleryItem> mItems;
    ThumbnaiDownloader<ImageView> mThumbnaiThread;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mItems=GalleryItemLab.get(getActivity()).getGalleryItems();//获取文件中的items
        Log.d(TAG,"mItems is "+mItems);
        if (mItems==null){
            updateItems();
        }
        mThumbnaiThread=new ThumbnaiDownloader<>(getActivity(),new Handler());//创建的handler默认与当前线程相关联
        mThumbnaiThread.setListener( new ThumbnaiDownloader.Listener<ImageView>(){
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()){
                    Log.d(TAG,"setListener");
                    imageView.setImageBitmap(thumbnail);
                    /*
                    //先压缩图片
                    int reqWidth=imageView.getWidth();
                    int reqHeight=imageView.getHeight();
                    Bitmap bitmap= StaticMethodUtil.decodeSampledBitmapFromResource(getResources()
                    ,R.id.gallery_item_imageView,reqWidth,reqHeight);
                    imageView.setImageBitmap(bitmap);
                    */
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mGridView=(GridView)v.findViewById(R.id.gridView);
        setupAdapter();

        //下拉刷新颜色
        final SwipeRefreshLayout mSRL=(SwipeRefreshLayout)v.findViewById(R.id.swipeLayout);
        mSRL.setColorSchemeResources(R.color.colorPurple,R.color.colorOrangeLight,
                R.color.colorRedLight,R.color.colorPrimary);
        mSRL.setProgressBackgroundColorSchemeResource(R.color.colorWhite);

        mSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSRL.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity()!=null){
                            ConnectivityManager connectivityManager=(ConnectivityManager)getActivity()
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                            if (networkInfo!=null&&networkInfo.isAvailable()){
                                updateItems();
                            }else{
                                Toast.makeText(getActivity(),R.string.networt_unavailable,Toast.LENGTH_SHORT).show();
                            }
                            mSRL.setRefreshing(false);
                        }
                    }
                },5000);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem item=mItems.get(position);
                    Intent i=new Intent(getActivity(),PhotoDetailActivity.class);
                    //用于代替PhotoDetailActivity实现滑动查看图片详情
                    //Intent i=new Intent(getActivity(),PhotoPageActivity.class);
                    i.putExtra(PhotoDetailFragment.EXTRA_GALLERYITEM_mId,item.getId());
                    startActivity(i);
            }
        });

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

            Log.d(TAG, "doInBackground: activity is "+activity);
            if (activity==null)
                return new ArrayList<>();

            return new FlickrFetchr().fetchItems();
        }

        //在主线程运行，在doinbackground之后执行
        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mItems=galleryItems;
            Log.d(TAG,"mItems size is "+mItems.size());
            if (mItems.size()!=0){
                //添加新的图片前先删除原有的
                GalleryItemLab.get(getActivity()).deleteGalleryItems();
            }
            GalleryItemLab.get(getActivity()).addGalleryItems(mItems);
            setupAdapter();
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem>{
        public GalleryItemAdapter(ArrayList<GalleryItem> items){
            super(getActivity(),0,items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG,"arrayAdapter, getView");
            if (convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.item_image_view,parent,false);
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
    public void onPause() {
        super.onPause();
        GalleryItemLab.get(getActivity()).saveGalleryItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnaiThread.quit();//结束线程
        Log.d(TAG,"Background thread destroyed");
    }
}
