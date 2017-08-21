package com.csymicc.poster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Micc on 7/30/2017.
 */

public class MainHomeFragment extends Fragment implements MsgType, View.OnClickListener {

    private MainActivity activity = null;

    private PosterDataBaseHelper dbHelper = null;

    private List<PostContent> postContents =  new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout = null;

    private RecyclerView PostContent = null;

    private PostAdapter postAdatper = null;

    private Button search = null;

    private boolean RequestEnd = false;

    private int countHandleMess = 0;

    private String handleMessageTAG = "Debug: Home Fragment/ ";

    private static Handler MainHomeFragmentHandler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("HomeFragment: ", "OnCreateView");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = (MainActivity) getActivity();
        initPostContent();
        PostContent = (RecyclerView) view.findViewById(R.id.main_post_list);
        search = (Button) view.findViewById(R.id.title_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        PostContent.setLayoutManager(layoutManager);
        postAdatper = new PostAdapter(postContents);
        PostContent.setAdapter(postAdatper);
        MainHomeFragmentHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ++countHandleMess;
                Log.d(handleMessageTAG, "get No." + countHandleMess + " message");
                dbHelper = new PosterDataBaseHelper(activity.mcontext, "Poster.db", null, PosterDataBaseHelper.DataBaseVersion);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                switch (msg.what) {
                    case PM_POST:
                        PosterMessage mess = (PosterMessage) msg.obj;
                        mess.show("HomeFragment: ");
                        String[] data = mess.deserializeText();
                        String CUID = data[0];
                        String USERNAME = data[1];
                        String PID = data[2];
                        String TIME = data[3];
                        String TEXT = data[4];

                        try {

                            db.execSQL("insert into POST values(" + PID + ", " + CUID + ", '" + USERNAME + "', '" + TIME + "', '" + TEXT + "')");
                            List<byte[]> images = mess.getImage();
                            List<Integer> imagesNo = mess.getImageNo();
                            if (images != null) {
                                int countImages = 0;
                                for (int i = 0; i != images.size(); ++i) {
                                    int imageNo = imagesNo.get(i);
                                    byte[] image = images.get(i);
                                    ++countImages;
                                    if (mess.getImage() != null) {
                                        writeImage(image, "/data/data/com.csymicc.poster/imagePost/" + data[0], imageNo);
                                    }
                                    db.execSQL("insert into IMAGES values (" + PID + ", " + imageNo + ")");
                                }
                                Log.d(handleMessageTAG, "No." + countHandleMess + " has " + countImages + " images");
                            }
                            //Toast.makeText(main.mcontext, "New Post available", Toast.LENGTH_SHORT).show();
                        } catch (SQLiteConstraintException e) {
                            e.printStackTrace();
                        }
                        break;
                    case PM_REQUEST_END:
                        RequestEnd = true;
                        initPostContent();
                        postAdatper.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                    case TIME_OUT:
                        if(!RequestEnd) {
                            RequestEnd = true;
                            Toast.makeText(activity.mcontext, "NetWork is down, please try again", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                    default:

                }
                dbHelper.close();
                db.close();
            }
        };
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_swipe_fresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        Log.d("HomeFragment: ", "onStart");
        super.onStart();
        activity.handler = MainHomeFragmentHandler;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment: ", "onStart");
        Log.d("HomeFragment: ", "NeedRefresh: " + activity.NeedRefresh);
        if(activity.NeedRefresh == true) {
            refresh();
            activity.NeedRefresh = false;
        }
        activity.handler = MainHomeFragmentHandler;
    }

    public void writeImage(byte[] image, String path, int Number) {

        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
        String[] list = file.list();
        File f = new File(path + '/' + Number + ".png");
        if(!f.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream writer = new BufferedOutputStream(fos);
                writer.write(image);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void initPostContent() {
        postContents.clear();
        dbHelper = new PosterDataBaseHelper(activity, "Poster.db", null, PosterDataBaseHelper.DataBaseVersion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select PID, POST.UID as UID, USERNAME, TEXT, TIME from FOLLOW, POST" +
                " where (FOLLOW.UID1 = " + activity.UID + " and FOLLOW.UID2 = POST.UID) or POST.UID = " + activity.UID + " order by POST.TIME desc", null);
        Log.d("Post: ", "UID: " + activity.UID);
        int[] PID = new int[cursor.getCount()];
        int[] PUID = new int[cursor.getCount()];
        String[] USERNAME = new String[cursor.getCount()];
        String[] TEXT = new String[cursor.getCount()];
        String[] TIME = new String[cursor.getCount()];
        int cursorsize = 0;
        while(cursor.moveToNext()) {
            PID[cursorsize] = cursor.getInt(cursor.getColumnIndex("PID"));
            PUID[cursorsize] = cursor.getInt(cursor.getColumnIndex("UID"));
            USERNAME[cursorsize] = cursor.getString(cursor.getColumnIndex("USERNAME"));
            TEXT[cursorsize] = cursor.getString(cursor.getColumnIndex("TEXT"));
            TIME[cursorsize] = cursor.getString(cursor.getColumnIndex("TIME"));
            ++cursorsize;
        }
        cursor.close();

        for(int i = 0; i != 10 && i != cursorsize; ++i) {
            PostContent item = new PostContent(PUID[i], TIME[i], USERNAME[i], TEXT[i]);
            item.images = new ArrayList<>();
            Cursor imageCursor = db.rawQuery("select IMAGE from IMAGES where PID = " + PID[i], null);
            while(imageCursor.moveToNext()) {
                item.images.add(imageCursor.getInt(0));
            }
            imageCursor.close();
            postContents.add(item);
        }
        Log.d("Post: ", "size:" + postContents.size());
        dbHelper.close();
        db.close();
    }

    public void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        PosterMessage message = new PosterMessage(PM_REQUEST);
        NetService.sendList.add(message);
        Log.d("HomeFragment: ", "refreshing");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Message mess = new Message();
                    mess.what = TIME_OUT;
                    activity.handler.sendMessage(mess);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_search:
                MainActivity mainActivity = (MainActivity) getActivity();
                Intent intent = new Intent(mainActivity.mcontext, MainFindUserActivity.class);
                intent.putExtra("UID", mainActivity.UID);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
