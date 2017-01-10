package com.hartz.inventory.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Webmaster on 1/9/2017.
 */

public class SSJDE implements Serializable{
    private String id;
    private String user;
    private Customer customer;
    private ArrayList<Mfgart> itemList;

    public SSJDE() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public SSJDE(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
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

    public ArrayList<Mfgart> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Mfgart> itemList) {
        this.itemList = itemList;
    }

    public static ArrayList<SSJDE> getFromJSON(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.getBoolean("error")) return null;
            ArrayList<SSJDE> list = new ArrayList<SSJDE>();
            JSONArray arr = jsonObject.getJSONArray("entries");
            for(int i = 0; i < arr.length(); i++){
                JSONObject jsonObj = arr.getJSONObject(i);
                //get item list
                JSONArray arr2 = jsonObj.getJSONArray("ssjded");
                ArrayList<Mfgart> listMfgart = new ArrayList<Mfgart>();
                for(int j = 0; j < arr2.length(); j++){
                    JSONObject mfgartobj = arr2.getJSONObject(j);
                    listMfgart.add(
                            new Mfgart(mfgartobj.getString("SSJDE_GROUP"),
                                    mfgartobj.getString("SSJDE_ART"),
                                    mfgartobj.getString("SSJDE_ARTICLENAME"),
                                    mfgartobj.getInt("SSJDE_QTY"),
                                    mfgartobj.getString("SSJDE_SATUAN")
                    ));
                }

                SSJDE ssjde = new SSJDE(jsonObj.getString("SSJDE_DateTime"),
                        new Customer(jsonObj.getString("SSJDE_CUSTID"),
                                jsonObj.getString("SSJDE_CUSTNAME")));
                ssjde.setItemList(listMfgart);
                list.add(ssjde);
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
            Mfgart mfgart = itemList.get(i);
            sb.append(mfgart.getArticleName());
            sb.append(" ("+mfgart.getGroupID()+")");
            sb.append(" "+mfgart.getQuantity());
            sb.append(" "+mfgart.getSatuan());
            if(i != itemList.size()-1)sb.append("\r\n");
        }
        return sb.toString();
    }
}
