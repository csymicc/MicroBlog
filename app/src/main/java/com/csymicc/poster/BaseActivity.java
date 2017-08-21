package com.csymicc.poster;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * Created by Micc on 7/26/2017.
 */

public class BaseActivity extends AppCompatActivity implements MsgType{

    protected PosterDataBaseHelper dbHelper = null;

    public static NetService netService = new NetService();

    protected Context mcontext = this;

    public static Handler handler = null;

    public static int UserID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
