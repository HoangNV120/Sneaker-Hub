package com.prm392_g1.sneakerhub.entities;

public class UserRole {
    public String id;
    public String user_id;
    public String role_type; // dùng Role enum ở logic app
    public long created_date;
    public long updated_date;

    public UserRole() {}
}
