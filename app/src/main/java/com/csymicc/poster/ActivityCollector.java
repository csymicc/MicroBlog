package com.csymicc.poster;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Micc on 7/26/2017.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishALl() {
        for(Activity activity : activities) {
            if(!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
