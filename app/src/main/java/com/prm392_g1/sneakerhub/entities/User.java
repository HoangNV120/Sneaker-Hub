package com.prm392_g1.sneakerhub.entities;

public class User {
    public String id;
    public String name;
    public String address;
    public String email;
    private String password;
    public String phone_number;
    public long created_date;
    public long updated_date;
    public String avatar;
    public boolean is_banned;

    public User() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
