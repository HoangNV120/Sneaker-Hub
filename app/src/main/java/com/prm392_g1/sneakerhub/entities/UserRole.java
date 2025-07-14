package com.prm392_g1.sneakerhub.entities;

public class UserRole {
    private String id;
    private String user_id;
    private String role_type;
    private long created_date;
    private long updated_date;

    public UserRole() {
    }

    public long getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(long updated_date) {
        this.updated_date = updated_date;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public String getRole_type() {
        return role_type;
    }

    public void setRole_type(String role_type) {
        this.role_type = role_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
