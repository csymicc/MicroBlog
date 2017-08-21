package com.csymicc.poster;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Handler;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Micc on 7/26/2017.
 */

public class SignUp extends BaseActivity implements View.OnClickListener{
    EditText email = null;
    EditText username = null;
    EditText password = null;
    EditText confirm = null;
    Button submit = null;
    ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email = (EditText) findViewById(R.id.signup_email);
        username = (EditText) findViewById(R.id.signup_username);
        password = (EditText) findViewById(R.id.signup_password);
        confirm = (EditText) findViewById(R.id.signup_confirm);
        submit = (Button) findViewById(R.id.signup_signup);
        progressBar = (ProgressBar) findViewById(R.id.signup_progressbar);
        submit.setOnClickListener(this);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PM_REGISTRATION_SUCCEED:
                    case PM_REGISTRATION_FAIL:
                        dbHelper = new PosterDataBaseHelper(mcontext, "Poster.db", null, PosterDataBaseHelper.DataBaseVersion);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        PosterMessage mess = (PosterMessage) msg.obj;
                        String[] data = mess.deserializeText();

                        String se = email.getText().toString();
                        String su = username.getText().toString();

                        if (mess != null && mess.getType() == 0) {
                            //StringBuilder stringBuilder = new StringBuilder();
                            //stringBuilder.append("insert into USER values(").append(Integer.valueOf(data[1])).append(',').append(e).append(',').append(0).append(''));
                            db.execSQL("insert into USER values(" + data[1] + ", '" + se + "', '" + su + "')");

                            try {
                                FileOutputStream out = openFileOutput("Last_login_user.txt", MODE_PRIVATE);
                                out.write(Integer.valueOf(data[1]));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(mcontext, MainActivity.class);
                            intent.putExtra("UID", data[1]);
                            intent.putExtra("EMAIL", se);
                            intent.putExtra("USERNAME", su);
                            startActivity(intent);
                            finish();
                        } else if (mess != null && mess.getType() == 1) {
                            Toast.makeText(mcontext, "Email is exist, please use another one", Toast.LENGTH_SHORT).show();
                        }

                        db.close();
                        dbHelper.close();
                        break;

                    case RECOVER_BUTTON:
                        if(submit.getVisibility() != View.VISIBLE) {
                            Toast.makeText(mcontext, "Network is down. Please try again later", Toast.LENGTH_SHORT).show();
                            submit.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        break;

                    case CHANGE_BUTTON:
                        progressBar.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.GONE);
                        break;
                    default:

                }
            }
        };

    }

    @Override
    public void onClick(View v) {
        String se = email.getText().toString();
        String su = username.getText().toString();
        String sp = password.getText().toString();
        String sc = confirm.getText().toString();
        if(se == null || se.isEmpty()) {
            Toast.makeText(SignUp.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        } else if(su == null || su.isEmpty()) {
            Toast.makeText(SignUp.this, "Please enter a User name", Toast.LENGTH_SHORT).show();
            return;
        } else if(sp == null || sp.length() < 6 || sp.length() > 20) {
            Toast.makeText(SignUp.this, "Password format is incorrect, password should have at least 6 and at most 20 characters", Toast.LENGTH_SHORT).show();
            return;
        } else if(!sp.equals(sc)) {
            Toast.makeText(SignUp.this, "Two passwords are not same", Toast.LENGTH_SHORT).show();
            return;
        }

        submit.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        String[] sendData = new String[3];
        sendData[0] = se;
        sendData[1] = su;
        sendData[2] = sp;
        PosterMessage message = new PosterMessage(PM_REGISTRATION);
        message.serializeText(sendData);
        NetService.sendList.add(message);

        Message mess = new Message();
        mess.what = CHANGE_BUTTON;
        handler.sendMessage(mess);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message mess = new Message();
                mess.what = RECOVER_BUTTON;
                handler.sendMessage(mess);
            }
        }).start();

        submit.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }
}
