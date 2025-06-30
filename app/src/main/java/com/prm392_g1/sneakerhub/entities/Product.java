package com.prm392_g1.sneakerhub.entities;

public class Product {
    public String id;
    public String name;
    public String description;
    public double price;
    public String image;
    public long created_date;
    public long updated_date;
    public boolean is_available = true;

    // Required empty constructor for Firebase
    public Product() {
    }

    public Product(String name, String description, double price, String image) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.created_date = System.currentTimeMillis();
        this.updated_date = System.currentTimeMillis();
    }
}
