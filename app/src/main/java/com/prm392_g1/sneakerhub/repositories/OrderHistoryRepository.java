package com.prm392_g1.sneakerhub.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.prm392_g1.sneakerhub.entities.OrderHistory;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryRepository {
    private static final String DATABASE_URL = "https://sneaker-hub-70299-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private static final String ORDER_HISTORY_NODE = "order_history";

    private DatabaseReference databaseReference;

    public OrderHistoryRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = database.getReference(ORDER_HISTORY_NODE);
    }

    // Interface for callbacks
    public interface OrderHistoryCallback {
        void onSuccess(OrderHistory orderHistory);
        void onError(String error);
    }

    public interface OrderHistoryListCallback {
        void onSuccess(List<OrderHistory> orderHistories);
        void onError(String error);
    }

    // Save order history
    public void saveOrderHistory(OrderHistory orderHistory, OrderHistoryCallback callback) {
        databaseReference.child(orderHistory.id).setValue(orderHistory)
            .addOnSuccessListener(aVoid -> callback.onSuccess(orderHistory))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get order history by order ID
    public void getOrderHistoryByOrderId(String orderId, OrderHistoryListCallback callback) {
        databaseReference.orderByChild("order_id").equalTo(orderId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<OrderHistory> orderHistories = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderHistory orderHistory = snapshot.getValue(OrderHistory.class);
                        if (orderHistory != null) {
                            orderHistories.add(orderHistory);
                        }
                    }
                    callback.onSuccess(orderHistories);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Get order history by status
    public void getOrderHistoryByStatus(String status, OrderHistoryListCallback callback) {
        databaseReference.orderByChild("status").equalTo(status)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<OrderHistory> orderHistories = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderHistory orderHistory = snapshot.getValue(OrderHistory.class);
                        if (orderHistory != null) {
                            orderHistories.add(orderHistory);
                        }
                    }
                    callback.onSuccess(orderHistories);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Get all order history
    public void getAllOrderHistory(OrderHistoryListCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<OrderHistory> orderHistories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OrderHistory orderHistory = snapshot.getValue(OrderHistory.class);
                    if (orderHistory != null) {
                        orderHistories.add(orderHistory);
                    }
                }
                callback.onSuccess(orderHistories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Delete order history
    public void deleteOrderHistory(String orderHistoryId, OrderHistoryCallback callback) {
        databaseReference.child(orderHistoryId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
