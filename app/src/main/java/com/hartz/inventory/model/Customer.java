package com.hartz.inventory.model;

import android.content.Context;
import android.util.Log;

import com.hartz.inventory.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Webmaster on 1/9/2017.
 */

public class Customer implements Serializable{
    private String id;
    private String name;

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Customer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ArrayList<Customer> getListFromPrefs(Context applicationContext) {
        try {
            String custJson = SharedPrefsHelper.readPrefs(SharedPrefsHelper.CUSTOMER_PREFS, applicationContext);
            JSONObject userObject = new JSONObject(custJson);
            if(userObject.getBoolean("error")) return null;
            JSONArray custJSONArray = userObject.getJSONArray("customers");
            ArrayList<Customer> custArray = new ArrayList<Customer>();
            for(int i = 0; i < custJSONArray.length(); i++){
                JSONObject martObject = custJSONArray.getJSONObject(i);
                custArray.add(new Customer(martObject.getString("MCUSTOMER_CUSTID"),
                        martObject.getString("MCUSTOMER_CUSTNAME")));
            }
            return custArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        return id != null ? id.equals(customer.id) : customer.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
