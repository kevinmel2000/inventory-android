package com.hartz.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hrtz on 12/30/2016.
 */

public class SharedPrefsHelper{
    public final static String NAME_PREFS = "UserName";
    public final static String ROLE_PREFS = "RoleName";
    public final static String TOKEN_PREFS = "UserToken";
    public final static String MRMART_PREFS = "MrmartJson";
    public final static String SATUAN_PREFS = "SatuanJson";

    public static void saveToPrefs(String key, String value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        prefs = null;
    }

    public static String readPrefs(String key, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null);
    }
}
