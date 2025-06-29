package com.prm392_g1.sneakerhub.entities;

public class Order {
    public String id;
    public String product_id;
    public String user_id;
    public int amount;
    public double total_price;
    public String status; // dùng Status enum logic app
    public String short_code;
    public String shipping_address;
    public String shipping_phone;
    public String note;
    public long created_date;
    public long updated_date;

    public Order() {}
}
