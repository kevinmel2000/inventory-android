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
    public static final String SERVER_PREFS = "ServerName";
    public static final String PASSWORD_PREFS = "Password";
    public static final String MFGART_PREFS = "MfgartJson";
    public static final String CUSTOMER_PREFS = "ClientJson";
    public static final String LAST_SERVER_PREFS = "LastServerName";
    public static final String LAST_NAME_PREFS = "LastUserName";


    public static void saveToPrefs(String key, String value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        prefs = null;
    }

    /**
     * read existing prefs
     * @param key key for preferences, use static from this class
     * @param context
     * @return return the value, null if the record does not exist
     */
    public static String readPrefs(String key, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null);
    }

    /**
     * check if the user have logged in in this device
     * @param context
     * @return true if the user is logged in, false otherwise
     */
    public static boolean isLoggedIn(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getString(ROLE_PREFS, null) != null && prefs.getString(TOKEN_PREFS, null) != null) return true;
        return false;
    }

    public static String lastServer(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(LAST_SERVER_PREFS, null);
    }

    public static String lastName(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(LAST_NAME_PREFS, null);
    }

    public static void logout(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(SharedPrefsHelper.NAME_PREFS);
        editor.remove(SharedPrefsHelper.ROLE_PREFS);
        editor.remove(SharedPrefsHelper.TOKEN_PREFS);
        editor.apply();
        preferences = null;
    }
}
