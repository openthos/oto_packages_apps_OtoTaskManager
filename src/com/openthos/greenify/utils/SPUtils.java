package com.openthos.greenify.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.openthos.greenify.app.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SPUtils {
    private static SPUtils instance;
    private SharedPreferences mPreferences;

    public static SPUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SPUtils(context);
        }
        return instance;
    }

    private SPUtils(Context context) {
        mPreferences = context.getSharedPreferences(Constants.SP_ADDED_APP, Context.MODE_PRIVATE);
    }

    /**
     * Add app to an automatic dormancy list
     *
     * @param packageName
     */
    public void saveAddedApp(String packageName, String appName) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(packageName, appName);
        edit.commit();
    }

    /**
     * Get the application stored in the automatic dormancy list
     *
     * @return
     */
    public Map<String, String> getAllAddedApp() {
        return (Map<String, String>) mPreferences.getAll();
    }

    /**
     * Remove the application added to the auto dormancy list
     *
     * @param packageName
     */
    public void removeAddApp(String packageName) {
        mPreferences.edit().remove(packageName).commit();
    }
}
