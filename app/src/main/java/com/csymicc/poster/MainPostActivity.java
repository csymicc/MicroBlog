package com.csymicc.poster;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Micc on 8/3/2017.
 */

public class MainPostActivity extends BaseActivity implements View.OnClickListener{

    private Button cancel = null;

    private Button post = null;

    private Button photo = null;

    private EditText text = null;

    private RecyclerView images = null;

    private ProgressBar sending = null;

    private View popupView = null;

    private int UID = -1;

    private PhotoPopUpWindow window = null;

    private CameraImageAdapter ImagesAdapter = null;

    private List<Bitmap> ImageList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        UID = getIntent().getIntExtra("UID", -1);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PM_POST_CONFIRM:
                        sending.setVisibility(View.GONE);
                        post.setVisibility(View.VISIBLE);
                        MainActivity.NeedRefresh = true;
                        finish();
                        break;
                    case TIME_OUT:
                        Toast.makeText(mcontext, "NetWork is down, please try again", Toast.LENGTH_SHORT).show();
                        sending.setVisibility(View.GONE);
                        post.setVisibility(View.VISIBLE);
                    default:
                        break;
                }
            }
        };

        cancel = (Button) findViewById(R.id.post_cancel);
        post = (Button) findViewById(R.id.post_post);
        photo = (Button) findViewById(R.id.post_photo);
        text = (EditText) findViewById(R.id.post_text);
        images = (RecyclerView) findViewById(R.id.post_images);
        sending = (ProgressBar)  findViewById(R.id.post_sending);
        cancel.setOnClickListener(this);
        post.setOnClickListener(this);
        photo.setOnClickListener(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        images.setLayoutManager(layoutManager);
        ImagesAdapter = new CameraImageAdapter(ImageList);
        images.setAdapter(ImagesAdapter);
        images.setVisibility(View.GONE);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_cancel:
                finish();
                break;
            case R.id.post_post:
                Log.d("Post a Message", "ImageList: " + ImageList.size());
                PosterMessage posterMessage = new PosterMessage(PM_POST, ImageList);
                String[] sendData = new String[2];
                sendData[0] = Integer.toString(UID);
                sendData[1] = text.getText().toString();
                posterMessage.serializeText(sendData);
                sending.setVisibility(View.VISIBLE);
                post.setVisibility(View.INVISIBLE);
                NetService.sendList.add(posterMessage);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4000);
                            Message message = new Message();
                            message.what = TIME_OUT;
                            handler.sendMessage(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.post_photo:
                if(ContextCompat.checkSelfPermission(MainPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    if(popupView == null) {
                        popupView = getLayoutInflater().inflate(R.layout.photo_popup, null);
                        window = new PhotoPopUpWindow(popupView, this);
                    }
                    window.showAsDropDown(post, 350, 20);
                    break;
                }
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if(resultCode == RESULT_OK) {
                    try {
                        Log.d("Post Message: ", "get a image from camera");
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(window.imageUri));
                        ImageList.add(bitmap);
                        ImagesAdapter.notifyItemInserted(ImageList.size() - 1);
                        if(images.getVisibility() == View.GONE) {
                            images.setVisibility(View.VISIBLE);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if(resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        getImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        getImage(imagePath);
    }

    private void getImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Log.d("Post Message: ", "get a image from album");
        ImageList.add(bitmap);
        ImagesAdapter.notifyItemInserted(ImageList.size() - 1);
        if(images.getVisibility() == View.GONE) {
            images.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(popupView == null) {
                        popupView = getLayoutInflater().inflate(R.layout.photo_popup, null);
                        window = new PhotoPopUpWindow(popupView, this);
                    }
                    window.showAsDropDown(post, 350, 20);
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
