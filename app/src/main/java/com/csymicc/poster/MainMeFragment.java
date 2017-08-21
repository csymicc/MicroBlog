package com.csymicc.poster;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Micc on 7/30/2017.
 */

public class MainMeFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("123", "View changed");
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        TextView email = (TextView) view.findViewById(R.id.main_me_email);
        TextView name = (TextView) view.findViewById(R.id.main_me_name);
        final MainActivity activity = (MainActivity) getActivity();
        email.setText(activity.EMAIL);
        name.setText(activity.USERNAME);
        Button signOut = (Button) view.findViewById(R.id.main_me_signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/data/data/com.csymicc.poster/files/Last_login_user.txt");
                if(file.exists()) {
                    file.delete();
                }
                Intent intent = new Intent(activity.mcontext, SignIn.class);
                startActivity(intent);
                activity.finish();
            }
        });
        return view;
    }
}
