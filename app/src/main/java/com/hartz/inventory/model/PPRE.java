package com.hartz.inventory.model;

import java.util.ArrayList;

/**
 * Created by Webmaster on 1/5/2017.
 */

public class PPRE {
    private String id;
    private String user;
    private ArrayList<Mrmart> item;

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

    public ArrayList<Mrmart> getItem() {
        return item;
    }

    public void setItem(ArrayList<Mrmart> item) {
        this.item = item;
    }
}
