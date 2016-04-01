package com.example.yanhoor.flickrgallery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.yanhoor.flickrgallery.util.StaticMethodUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by yanhoor on 2016/3/31.
 */
public class UploadPhotoFragment extends Fragment {
    private static final String TAG="UploadPhotoFragment";

    private static final int REQUEST_CODE_PICK_PICTURE=1;

    EditText editTitle;
    EditText editDescription;
    GridView newPhotoGridView;
    Button cancelButton;
    Button postButton;

    private ArrayList<HashMap<String,Object>> imageItems=new ArrayList<>();
    private SimpleAdapter mSimpleAdapter;
    private String imagePath;
    private String title;
    private String description;
    private ArrayList<byte[]>bitmapByteArrays=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_upload_photo,container,false);

        editTitle=(EditText)v.findViewById(R.id.edit_new_photo_title);
        editDescription=(EditText)v.findViewById(R.id.edit_new_photo_description);
        newPhotoGridView=(GridView)v.findViewById(R.id.add_photo_gridView);
        cancelButton=(Button)v.findViewById(R.id.cancel_post_new_photo_button);
        postButton=(Button)v.findViewById(R.id.post_new_photo_button);

        title=editTitle.getText().toString().trim();
        description=editDescription.getText().toString().trim();

        //添加点击图片
        Bitmap addRes= BitmapFactory.decodeResource(getResources(),R.drawable.add_photo);
        HashMap<String,Object> map=new HashMap<>();
        map.put("imageItem",addRes);
        imageItems.add(map);

        setupAdapter();

        newPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageItems.size()==10){
                    Toast.makeText(getActivity(),R.string.photo_full_toast,Toast.LENGTH_SHORT).show();
                }else if (position==0){
                    Toast.makeText(getActivity(),R.string.add_photo_toast,Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i,REQUEST_CODE_PICK_PICTURE);
                }else {
                    buildAlertDialog(position);
                }
            }
        });

        return v;
    }

    private void setupAdapter(){
        mSimpleAdapter=new SimpleAdapter(getActivity(),imageItems,R.layout.item_image_view,
                new String[]{"imageItem"},new int[]{R.id.gallery_item_imageView});

        mSimpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView &&data instanceof Bitmap){
                    ImageView i=(ImageView)view;
                    i.setImageBitmap((Bitmap)data);
                    return true;
                }
                return false;
            }
        });
        newPhotoGridView.setAdapter(mSimpleAdapter);
    }

    private void buildAlertDialog(final int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_photo_confirm_dialog)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        imageItems.remove(position);
                        mSimpleAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==Activity.RESULT_OK&&requestCode==REQUEST_CODE_PICK_PICTURE){
            Uri uri=data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())){
                Cursor cursor=getActivity().getContentResolver().query(
                        uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);

                if (cursor==null)return;
                cursor.moveToFirst();
                imagePath=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
    }

    //选择照片后刷新
    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(imagePath)){
            BitmapFactory.Options options;
            Bitmap newPic;

            try {
                newPic = BitmapFactory.decodeFile(imagePath);
            } catch (OutOfMemoryError e) {
                    options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    newPic = BitmapFactory.decodeFile(imagePath,options);
            }

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            newPic.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] bitmapByteArray=baos.toByteArray();
            bitmapByteArrays.add(bitmapByteArray);

            //Bitmap newPic=BitmapFactory.decodeFile(imagePath);
            Bitmap pic=Bitmap.createScaledBitmap(newPic,40,40,false);
            HashMap<String,Object>map=new HashMap<>();
            map.put("imageItem",pic);
            imageItems.add(map);

            Log.d(TAG,"onResume");
            setupAdapter();
            mSimpleAdapter.notifyDataSetChanged();

//刷新后释放防止手机休眠后自动添加
            imagePath=null;

        }
    }

    private void uploadPhoto(byte[] photoBinary){
        String[] mSignFullTokenStringArray = {"api_key" + LogInFragment.API_KEY,
                "auth_token" + MainLayoutActivity.fullToken,
                LogInFragment.PUBLIC_CODE, "title"+title, "description"+description};
        Arrays.sort(mSignFullTokenStringArray);
        StringBuilder mSB = new StringBuilder();
        for (String s : mSignFullTokenStringArray) {
            mSB.append(s);
        }
        String apiSig = StaticMethodUtil.countMD5OfString(mSB.toString());

        String url = Uri.parse("https://up.flickr.com/services/upload/").buildUpon()
                .appendQueryParameter("api_key", LogInFragment.API_KEY)
                .appendQueryParameter("photo",photoBinary.toString())
                .appendQueryParameter("auth_token", MainLayoutActivity.fullToken)
                .appendQueryParameter("api_sig",apiSig)
                .build().toString();
    }

}
