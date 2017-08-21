package com.csymicc.poster;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import java.io.File;
import java.io.IOException;

/**
 * Created by Micc on 8/3/2017.
 */

public class PhotoPopUpWindow extends PopupWindow implements View.OnClickListener {

    private Button camera;

    private Button album;

    private MainPostActivity parentActivity;

    public Uri imageUri = null;

    public PhotoPopUpWindow(View view, MainPostActivity context) {
        super(view);
        InitUi(view);
        parentActivity = context;
    }

    private void InitUi(View view) {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.popup_window_anim);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        camera = (Button) view.findViewById(R.id.popup_camera);
        album = (Button) view.findViewById(R.id.popup_album);
        camera.setOnClickListener(this);
        album.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        Intent intent = null;
        switch (v.getId()) {
            case R.id.popup_camera:
                File cameraImage = new File(parentActivity.getExternalCacheDir(), "camera_image.jpg");
                try {
                    if (cameraImage.exists()) {
                        cameraImage.delete();
                    }
                    cameraImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(parentActivity, "com.csymicc.poster.fileprovider", cameraImage);
                } else {
                    imageUri = Uri.fromFile(cameraImage);
                }

                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                parentActivity.startActivityForResult(intent, 0);
                break;
            case R.id.popup_album:
                intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                parentActivity.startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

}
