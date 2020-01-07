package com.app.comidapp.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Food implements Serializable {

    private int id;
    private String name;
    private String description;
    private int quantity;
    private double price;
    private int StoreId;

    public Food(int id, String name, String description, int quantity, double price, int storeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        StoreId = storeId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStoreId() {
        return StoreId;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " |  Precio: $" + price + "  \nDisponible: " + quantity;
    }
}
