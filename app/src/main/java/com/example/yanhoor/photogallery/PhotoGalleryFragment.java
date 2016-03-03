package com.example.yanhoor.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
/*
key: 0964378968b9ce3044e29838e2fc0cd8
密钥:a0e8c8d18675b5e2
*/

/**
 * Created by yanhoor on 2016/3/3.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG="PhotoGalleryFragment";
    GridView mGridView;
    ArrayList<GalleryItem> mItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
            mGridView.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_gallery_item,mItems));
        }else {
            mGridView.setAdapter(null);
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>>{
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        //在主线程运行，在doinbackground之后执行
        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mItems=galleryItems;
            setupAdapter();
        }
    }

}
