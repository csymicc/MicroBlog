package com.csymicc.poster;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Start extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        netService.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_SIGN:
                        try {
                            File file = new File("/data/data/com.csymicc.poster/files/Last_login_user.txt");
                            if (file.exists()) {
                                FileInputStream in = new FileInputStream(file);
                                int UID = in.read();
                                Log.d("Start: ", "UID: " + UID);
                                UserID = Integer.valueOf(UID);
                                String[] data = new String[2];
                                dbHelper = new PosterDataBaseHelper(mcontext, "Poster.db", null, PosterDataBaseHelper.DataBaseVersion);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                Cursor cursor = db.rawQuery("select * from USER where UID = " + UID, null);
                                cursor.moveToFirst();
                                String email = cursor.getString(1);

                                data[0] = Integer.toString(UID);
                                data[1] = email;
                                PosterMessage message = new PosterMessage(PM_SIGNIN_1);
                                message.serializeText(data);
                                netService.sendList.add(message);

                                Intent intent = new Intent(mcontext, MainActivity.class);
                                intent.putExtra("UID", UID);
                                intent.putExtra("EMAIL", email);
                                intent.putExtra("USERNAME", cursor.getString(2));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(mcontext, SignIn.class);
                                startActivity(intent);
                            }
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Message message = new Message();
                    message.what = START_SIGN;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
