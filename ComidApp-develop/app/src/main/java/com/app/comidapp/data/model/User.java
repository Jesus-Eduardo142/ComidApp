package com.app.comidapp.data.model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;
    private String password;
    private String role;
    private String city;
    private String address;
    private String cp;

    public User(int id, String username, String role, String city, String address, String cp) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.city = city;
        this.address = address;
        this.cp = cp;
    }

    public User(String username, String password, String role, String city, String address, String cp) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.city = city;
        this.address = address;
        this.cp = cp;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }
}
