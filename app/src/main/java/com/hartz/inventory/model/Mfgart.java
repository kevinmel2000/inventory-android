package com.hartz.inventory.model;

import android.content.Context;

import com.hartz.inventory.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Webmaster on 1/9/2017.
 */

public class Mfgart implements Serializable{
    private String groupID;
    private String articleID;
    private String articleName;
    private int quantity;
    private String satuan;
    private String note;

    public final static String MFGART_GROUPID = "SSJDE_GROUP";
    public final static String MFGART_ARTICLEID = "SSJDE_ART";
    public final static String MFGART_QUANTITY = "SSJDE_QTY";
    public final static String MFGART_SATUAN = "SSJDE_SATUAN";
    public final static String MFGART_NOTE = "SSJDE_NOTE";


    public Mfgart(String groupID, String articleID, String articleName, int quantity, String satuan, String note) {
        this.groupID = groupID;
        this.articleID = articleID;
        this.articleName = articleName;
        this.quantity = quantity;
        this.satuan = satuan;
        this.note = note;
    }

    public Mfgart(String groupID, String articleID, String articleName) {
        this.groupID = groupID;
        this.articleID = articleID;
        this.articleName = articleName;
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

    @Override
    public String toString() {
        return articleName +" ("+groupID+")";
    }

    /**
     * create new mrmart array from json response
     * @param context json text to be made into object
     * @return true if success, false if not
     */
    public static ArrayList<Mfgart> getListFromPrefs(Context context){
        try {
            String mfgartJson = SharedPrefsHelper.readPrefs(
                    SharedPrefsHelper.MFGART_PREFS, context);
            JSONObject userObject = new JSONObject(mfgartJson);
            if(userObject.getBoolean("error")) return null;
            JSONArray martJSONArray = userObject.getJSONArray("products");
            ArrayList<Mfgart> martArray = new ArrayList<Mfgart>();
            for(int i = 0; i < martJSONArray.length(); i++){
                JSONObject martObject = martJSONArray.getJSONObject(i);
                martArray.add(new Mfgart(martObject.getString("MFGART_GROUPID"),
                        martObject.getString("MFGART_ARTICLEID"),
                        martObject.getString("MFGART_ARTICLENAME")));
            }

            return martArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mfgart mfgart = (Mfgart) o;

        if (groupID != null ? !groupID.equals(mfgart.groupID) : mfgart.groupID != null)
            return false;
        return articleID != null ? articleID.equals(mfgart.articleID) : mfgart.articleID == null;

    }

    @Override
    public int hashCode() {
        int result = groupID != null ? groupID.hashCode() : 0;
        result = 31 * result + (articleID != null ? articleID.hashCode() : 0);
        return result;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
