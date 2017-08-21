package com.csymicc.poster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Micc on 7/26/2017.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener{

    public int UID = -1;
    public String EMAIL = null;
    public String USERNAME = null;
    private MainHomeFragment Homefragment = null;
    private MainMeFragment Mefragment = null;
    public static boolean NeedRefresh = false;

    private int upFragmentType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        UID = intent.getIntExtra("UID", -1);
        EMAIL = intent.getStringExtra("EMAIL");
        USERNAME = intent.getStringExtra("USERNAME");
        NeedRefresh = true;
        Button home = (Button) findViewById(R.id.main_buttons_home);
        Button post = (Button) findViewById(R.id.main_buttons_post);
        Button me = (Button) findViewById(R.id.main_buttons_me);
        replaceFragment(new MainHomeFragment());
        home.setOnClickListener(this);
        me.setOnClickListener(this);
        post.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity: ", "onStart");
        Log.d("MainActivity: ", Homefragment == null ? "is null" : "not null");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity: ", "onResume");
        Log.d("MainActivity: ", Homefragment == null ? "is null" : "not null");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity: ", "onPause");
        Log.d("MainActivity: ", Homefragment == null ? "is null" : "not null");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity: ", "onStop");
        Log.d("MainActivity: ", Homefragment == null ? "is null" : "not null");
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_up_fragment, fragment);
        transaction.commit();
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.main_buttons_home:
                replaceFragment(new MainHomeFragment());
                break;
            case R.id.main_buttons_me:
                replaceFragment(new MainMeFragment());
                break;
            case R.id.main_buttons_post:
                intent = new Intent(this, MainPostActivity.class);
                intent.putExtra("UID", UID);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity: ", "on Destory");
        String file = "Last_login_user.txt";
        try {
            FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            BufferedOutputStream writer = new BufferedOutputStream(fos);
            writer.write(UID);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
