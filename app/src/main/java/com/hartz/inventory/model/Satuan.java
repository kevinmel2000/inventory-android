package com.hartz.inventory.model;

import android.content.Context;

import com.hartz.inventory.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hrtz on 12/29/2016.
 */

public class Satuan{
    private String satuanID;
    private String satuanName;

    public Satuan(String satuanID, String satuanName) {
        this.satuanID = satuanID;
        this.satuanName = satuanName;
    }

    @Override
    public String toString() {
        return satuanName;
    }

    /**
     * get satuan list from shared preferences
     * @param context application context
     * @return list converted from json preferences
     */
    public static ArrayList<Satuan> getListFromPrefs(Context context){
        try {
            String satuanJson = SharedPrefsHelper.readPrefs(SharedPrefsHelper.SATUAN_PREFS, context);
            JSONObject userObject = new JSONObject(satuanJson);
            if(userObject.getBoolean("error")) return null;
            JSONArray satuanJSONArray = userObject.getJSONArray("satuan");
            ArrayList<Satuan> satuanArray = new ArrayList<Satuan>();
            for(int i = 0; i < satuanJSONArray.length(); i++){
                JSONObject martObject = satuanJSONArray.getJSONObject(i);
                satuanArray.add(new Satuan(martObject.getString("MSATUAN_SATUANID"),
                        martObject.getString("MSATUAN_SATUANNAME")));
            }

            return satuanArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSatuanID() {
        return satuanID;
    }

    public void setSatuanID(String satuanID) {
        this.satuanID = satuanID;
    }

    public String getSatuanName() {
        return satuanName;
    }

    public void setSatuanName(String satuanName) {
        this.satuanName = satuanName;
    }

}
