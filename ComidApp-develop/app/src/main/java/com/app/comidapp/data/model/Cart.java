package com.app.comidapp.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    private int id;
    private int UserId;
    private List<Food> items;
    private double total;
    private String date;

    public Cart(int id, int UserId, List<Food> items, double total) {
        this.id = id;
        this.UserId = UserId;
        this.items = items;
        this.total = total;
    }

    public Cart(int id, int userId, List<Food> items, double total, String date) {
        this.id = id;
        UserId = userId;
        this.items = items;
        this.total = total;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return UserId;
    }

    public List<Food> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public void addToTotal(double price) {
        total += price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Fecha: " + date + "  Total: " + total;
    }
}
