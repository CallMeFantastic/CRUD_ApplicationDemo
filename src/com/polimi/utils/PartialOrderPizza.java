package com.polimi.utils;

import java.util.List;

public class PartialOrderPizza {
    private String id;
    private String name;
    private int qty;
    private String notes;
    private List <String> addons;

    public void setId(String id) {
        this.id = id;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    //TODO: handle quando passi una lista di stringhe
    public void setAddons(List <String> s) {
        this.addons = s;
    }

    public String getId() {
        return id;
    }

    public int getQty() {
        return qty;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getAddons() {
        return addons;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
