package com.hartz.inventory.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Webmaster on 1/5/2017.
 */

public class PPRE implements Serializable{
    private String id;
    private String user;
    private ArrayList<Mrmart> itemList;

    public PPRE(){}

    public PPRE(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<Mrmart> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Mrmart> itemList) {
        this.itemList = itemList;
    }

    public static ArrayList<PPRE> getFromJSON(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.getBoolean("error")) return null;
            ArrayList<PPRE> list = new ArrayList<PPRE>();
            JSONArray arr = jsonObject.getJSONArray("entries");
            for(int i = 0; i < arr.length(); i++){
                JSONObject jsonObj = arr.getJSONObject(i);
                JSONArray arr2 = jsonObj.getJSONArray("ppred");
                ArrayList<Mrmart> listMart = new ArrayList<Mrmart>();
                for(int j = 0; j < arr2.length(); j++){
                    JSONObject mrmartobj = arr2.getJSONObject(j);
                    listMart.add(new Mrmart(mrmartobj.getString("PPRED_GROUP"), mrmartobj.getString("PPRED_ART"),
                            mrmartobj.getString("PPRED_ARTICLENAME"), mrmartobj.getInt("PPRED_QTY"),
                            mrmartobj.getString("PPRED_SATUAN")
                            ));
                }
                PPRE ppre = new PPRE(jsonObj.getString("PPRE_DateTime"));
                ppre.setItemList(listMart);
                list.add(ppre);
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String itemListToString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < itemList.size(); i++){
            Mrmart mrmart = itemList.get(i);
            sb.append(mrmart.getArticleName());
            sb.append(" ("+mrmart.getGroupID()+")");
            sb.append(" "+mrmart.getQuantity());
            sb.append(" "+mrmart.getSatuan());
            if(i != itemList.size()-1)sb.append("\r\n");
        }
        return sb.toString();
    }
}
