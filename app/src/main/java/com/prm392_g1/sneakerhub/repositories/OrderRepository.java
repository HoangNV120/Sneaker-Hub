package com.prm392_g1.sneakerhub.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.prm392_g1.sneakerhub.entities.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static final String DATABASE_URL = "https://sneaker-hub-70299-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private static final String ORDERS_NODE = "orders";

    private DatabaseReference databaseReference;

    public OrderRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = database.getReference(ORDERS_NODE);
    }

    // Interface for callbacks
    public interface OrderCallback {
        void onSuccess(Order order);
        void onError(String error);
    }

    public interface OrderListCallback {
        void onSuccess(List<Order> orders);
        void onError(String error);
    }

    // Save order
    public void saveOrder(Order order, OrderCallback callback) {
        databaseReference.child(order.id).setValue(order)
            .addOnSuccessListener(aVoid -> callback.onSuccess(order))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get order by ID
    public void getOrderById(String orderId, OrderCallback callback) {
        databaseReference.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                if (order != null) {
                    callback.onSuccess(order);
                } else {
                    callback.onError("Order not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Get orders by user ID
    public void getOrdersByUserId(String userId, OrderListCallback callback) {
        databaseReference.orderByChild("user_id").equalTo(userId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Order> orders = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    callback.onSuccess(orders);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Get orders by status
    public void getOrdersByStatus(String status, OrderListCallback callback) {
        databaseReference.orderByChild("status").equalTo(status)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Order> orders = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    callback.onSuccess(orders);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Get all orders
    public void getAllOrders(OrderListCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orders.add(order);
                    }
                }
                callback.onSuccess(orders);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Update order status
    public void updateOrderStatus(String orderId, String status, OrderCallback callback) {
        databaseReference.child(orderId).child("status").setValue(status)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Delete order
    public void deleteOrder(String orderId, OrderCallback callback) {
        databaseReference.child(orderId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get order by short code
    public void getOrderByShortCode(String shortCode, OrderCallback callback) {
        databaseReference.orderByChild("short_code").equalTo(shortCode)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null) {
                            callback.onSuccess(order);
                            return;
                        }
                    }
                    callback.onError("Order not found");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }
}
