package com.csymicc.poster;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

/**
 * Created by Micc on 8/3/2017.
 */

public class MainFindUserActivity extends BaseActivity implements View.OnClickListener{

    private Button search = null;

    private RecyclerView users = null;

    private EditText text = null;

    private int UID = -1;

    private Handler MainFindUserActivityHandler = null;

    private List<SearchUserContent> UserList = new ArrayList<>();

    private SearchUsersAdapter searchUsersAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        UID = intent.getIntExtra("UID", -1);
        search = (Button) findViewById(R.id.find_search);
        text = (EditText) findViewById(R.id.find_text);
        search.setOnClickListener(this);

        users =  (RecyclerView) findViewById(R.id.find_users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        users.setLayoutManager(layoutManager);
        searchUsersAdapter = new SearchUsersAdapter(UserList);
        users.setAdapter(searchUsersAdapter);

        MainFindUserActivityHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dbHelper = new PosterDataBaseHelper(mcontext, "Poster.db", null, PosterDataBaseHelper.DataBaseVersion);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                switch (msg.what) {
                    case PM_SEARCH_RESPONSE:
                        if (UserList != null) UserList.clear();
                        PosterMessage searchResponse = (PosterMessage) msg.obj;
                        String[] searchData = searchResponse.deserializeText();
                        for (int i = 0; i != searchData.length; i += 2) {
                            SearchUserContent userContent = new SearchUserContent(searchData[i + 1], Integer.valueOf(searchData[i]));
                            UserList.add(userContent);
                        }
                        searchUsersAdapter.notifyDataSetChanged();
                        break;
                    case SEND_FOLLOW:
                        int FUID = msg.arg1;
                        PosterMessage mess = new PosterMessage(PM_FOLLOW);
                        String[] sendData = new String[2];
                        sendData[0] = Integer.toString(UID);
                        sendData[1] = Integer.toString(FUID);
                        break;
                    case PM_FOLLOW_CONFIRM:
                        PosterMessage followConfirm = (PosterMessage) msg.obj;
                        String[] followData = followConfirm.deserializeText();
                        db.execSQL("Insert into FOLLOW values ('" + followData[0] + "', '" + followData[1] + "')");
                        break;
                    default:
                        break;
                }
                db.close();
                dbHelper.close();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = MainFindUserActivityHandler;
        searchUsersAdapter.setHandler(handler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_search:
                String name = text.getText().toString();
                if(name != null && !name.isEmpty()) {
                    String[] sendData = new String[2];
                    sendData[0] = Integer.toString(UID);
                    sendData[1] = name;
                    PosterMessage posterMessage = new PosterMessage(PM_SEARCH);
                    posterMessage.serializeText(sendData);
                    NetService.sendList.add(posterMessage);
                }
                break;
            default:
                break;
        }
    }
}
