package com.prm392_g1.sneakerhub.entities;

public class Product {
    public String id;
    public String name;
    public String description;
    public String size;     // dùng ProductSize enum logic app
    public String colour;   // dùng ProductColour enum logic app
    public double price;
    public String image;
    public long created_date;
    public long updated_date;

    public Product() {}
}
