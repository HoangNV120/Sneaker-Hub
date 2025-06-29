package com.prm392_g1.sneakerhub.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.prm392_g1.sneakerhub.entities.User;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String DATABASE_URL = "https://sneaker-hub-70299-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private static final String USERS_NODE = "users";

    private DatabaseReference databaseReference;

    public UserRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = database.getReference(USERS_NODE);
    }

    // Interface for callbacks
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UserListCallback {
        void onSuccess(List<User> users);
        void onError(String error);
    }

    // Create or update user
    public void saveUser(User user, UserCallback callback) {
        databaseReference.child(user.id).setValue(user)
            .addOnSuccessListener(aVoid -> callback.onSuccess(user))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get user by ID
    public void getUserById(String userId, UserCallback callback) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    callback.onSuccess(user);
                } else {
                    callback.onError("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Get all users
    public void getAllUsers(UserListCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Delete user
    public void deleteUser(String userId, UserCallback callback) {
        databaseReference.child(userId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Update user ban status
    public void updateUserBanStatus(String userId, boolean isBanned, UserCallback callback) {
        databaseReference.child(userId).child("is_banned").setValue(isBanned)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
