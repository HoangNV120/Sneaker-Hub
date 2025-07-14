package com.prm392_g1.sneakerhub.entities;

public class ProductVariant {
    public String id;
    public String product_id;
    public String size;
    public String colour;
    public int stock_quantity;
    public double price_adjustment;
    public boolean is_available;
    public long created_date;
    public long updated_date;

    // Required empty constructor for Firebase
    public ProductVariant() {}

    public ProductVariant(String size, String colour, int stockQuantity) {
        this.size = size;
        this.colour = colour;
        this.stock_quantity = stockQuantity;
        this.price_adjustment = 0.0;
        this.is_available = stockQuantity > 0;
        this.created_date = System.currentTimeMillis();
        this.updated_date = System.currentTimeMillis();
    }

    public ProductVariant(String size, String colour, int stockQuantity, double priceAdjustment) {
        this(size, colour, stockQuantity);
        this.price_adjustment = priceAdjustment;
    }
}
