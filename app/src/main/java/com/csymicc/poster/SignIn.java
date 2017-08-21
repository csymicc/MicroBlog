package com.csymicc.poster;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Micc on 7/26/2017.
 */

public class SignIn extends BaseActivity implements View.OnClickListener {

    EditText email = null;
    EditText password = null;
    Button signIn = null;
    Button signUp = null;
    ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                Log.d("SignIn: ", "handle message type: " + msg.what);
                switch (msg.what) {
                    case PM_SIGNIN_SUCCEED:
                    case PM_SIGNIN_FAIL:

                        PosterMessage mess = (PosterMessage) msg.obj;
                        String[] data = mess.deserializeText();

                        dbHelper = new PosterDataBaseHelper(mcontext, "Poster.db", null, PosterDataBaseHelper.DataBaseVersion);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        if (mess != null && mess.getType() == 5) {

                            //StringBuilder stringBuilder = new StringBuilder();
                            //stringBuilder.append("insert into USER values(").append(Integer.valueOf(data[1])).append(',').append(e).append(',').append(0).append(''));


                            // problem exists here if Email is in table
                            String se = email.getText().toString();

                            try {
                                db.execSQL("insert into USER values(" + data[0] + ",'" + se + "','" + data[1] + "')");
                            } catch (SQLiteConstraintException e) {
                                e.printStackTrace();
                            }
                            try {
                                FileOutputStream out = openFileOutput("Last_login_user.txt", MODE_PRIVATE);
                                out.write(Integer.valueOf(data[0]));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(mcontext, MainActivity.class);
                            intent.putExtra("UID", Integer.valueOf(data[0]));
                            intent.putExtra("EMAIL", se);
                            intent.putExtra("USERNAME", data[1]);
                            startActivity(intent);
                            finish();
                        } else if (mess != null && mess.getType() == 6) {
                            Toast.makeText(mcontext, "Email or Password is not correct, please try again", Toast.LENGTH_SHORT).show();
                        }

                        signIn.setVisibility(View.VISIBLE);
                        signUp.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        db.close();
                        dbHelper.close();
                        break;

                    case RECOVER_BUTTON:
                        if(signIn.getVisibility() != View.VISIBLE) {
                            Toast.makeText(mcontext, "Network is down. Please try again later", Toast.LENGTH_SHORT).show();
                            signIn.setVisibility(View.VISIBLE);
                            signUp.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        break;

                    case CHANGE_BUTTON:
                        progressBar.setVisibility(View.VISIBLE);
                        signIn.setVisibility(View.GONE);
                        signUp.setVisibility(View.GONE);
                        break;
                    default:
                }
            }
        };

        email = (EditText) findViewById(R.id.signin_email);
        password = (EditText) findViewById(R.id.signin_password);
        progressBar = (ProgressBar) findViewById(R.id.signin_progressbar);
        signIn = (Button) findViewById(R.id.signin_signin);
        signUp = (Button) findViewById(R.id.signin_signup);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signin_signin :
                String se = email.getText().toString();
                String sp = password.getText().toString();

                if(se == null || se.isEmpty()) {
                    Toast.makeText(this, "Please enter in an email address", Toast.LENGTH_SHORT).show();
                    return;
                } else if (sp == null || sp.isEmpty()) {
                    Toast.makeText(this, "Please enter in the password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] sendData = new String[2];
                sendData[0] = se;
                sendData[1] = sp;
                PosterMessage message = new PosterMessage(PM_SIGNIN_2);
                message.serializeText(sendData);
                netService.sendList.add(message);

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

                break;
            case R.id.signin_signup :
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
                break;
            default:
        }
    }
}
