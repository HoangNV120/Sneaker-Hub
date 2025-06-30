package com.prm392_g1.sneakerhub.entities;

public class OrderHistory {
    public String id;
    public String order_id;
    public String status; // dùng Status enum o logic app
    public int amount;
    public double total_price;
    public String shipping_address;
    public String shipping_phone;
    public long created_date;
    public long updated_date;

    public OrderHistory() {}
}
