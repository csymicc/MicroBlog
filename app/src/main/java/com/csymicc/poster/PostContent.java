package com.csymicc.poster;

import java.util.List;

/**
 * Created by Micc on 7/31/2017.
 */

public class PostContent {

    public int UID = -1;
    public String username = null;
    public int portrait = R.drawable.standard_portrait;
    public String time = null;
    public String text = null;
    public List<Integer> images = null;

    public PostContent(int uid, String Time, String Username, String Text) {
        UID = uid;
        username = Username;
        text = Text;
        time = Time;
    }

}
