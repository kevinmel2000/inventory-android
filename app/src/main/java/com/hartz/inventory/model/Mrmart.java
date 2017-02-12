package com.hartz.inventory.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.hartz.inventory.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hrtz on 12/29/2016.
 */

public class Mrmart implements Serializable{
    private String groupID;
    private String articleID;
    private String articleName;
    private int quantity;
    private String satuan;
    private String note;


    public final static String MRMART_GROUPID = "PPRE_GROUP";
    public final static String MRMART_ARTICLEID = "PPRE_ART";
    public final static String MRMART_QUANTITY = "PPRE_QTY";
    public final static String MRMART_SATUAN = "PPRE_SATUAN";
    public final static String MRMART_NOTE = "PPRE_NOTE";

    /**
     * constructor
     * @param groupID
     * @param articleID
     * @param articleName
     */
    public Mrmart(String groupID, String articleID, String articleName) {
        this.groupID = groupID;
        this.articleID = articleID;
        this.articleName = articleName;
    }

    /**
     *
     * @param groupID
     * @param articleID
     * @param articleName
     * @param quantity
     * @param satuan
     */
    public Mrmart(String groupID, String articleID, String articleName, int quantity, String satuan, String note) {
        this.groupID = groupID;
        this.articleID = articleID;
        this.articleName = articleName;
        this.quantity = quantity;
        this.satuan = satuan;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    /**
     * create new mrmart array from json response
     * @param context json text to be made into object
     * @return true if success, false if not
     */
    public static ArrayList<Mrmart> getListFromPrefs(Context context){
        try {
            String mrMartJson = SharedPrefsHelper.readPrefs(SharedPrefsHelper.MRMART_PREFS, context);
            JSONObject userObject = new JSONObject(mrMartJson);
            if(userObject.getBoolean("error")) return null;
            JSONArray martJSONArray = userObject.getJSONArray("mart");
            ArrayList<Mrmart> martArray = new ArrayList<Mrmart>();
            for(int i = 0; i < martJSONArray.length(); i++){
                JSONObject martObject = martJSONArray.getJSONObject(i);
                martArray.add(new Mrmart(martObject.getString("MRMART_GROUPID"),
                        martObject.getString("MRMART_ARTICLEID"),
                        martObject.getString("MRMART_ARTICLENAME")));
            }

            return martArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    @Override
    public String toString() {
        return articleName +" ("+groupID+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mrmart mrmart = (Mrmart) o;

        if (groupID != null ? !groupID.equals(mrmart.groupID) : mrmart.groupID != null)
            return false;
        return articleID != null ? articleID.equals(mrmart.articleID) : mrmart.articleID == null;

    }

    @Override
    public int hashCode() {
        int result = groupID != null ? groupID.hashCode() : 0;
        result = 31 * result + (articleID != null ? articleID.hashCode() : 0);
        return result;
    }
}
