package com.prm392_g1.sneakerhub.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.prm392_g1.sneakerhub.entities.UserRole;
import java.util.ArrayList;
import java.util.List;

public class UserRoleRepository {
    private static final String DATABASE_URL = "https://sneaker-hub-70299-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private static final String USER_ROLES_NODE = "user_roles";

    private DatabaseReference databaseReference;

    public UserRoleRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = database.getReference(USER_ROLES_NODE);
    }

    // Interface for callbacks
    public interface UserRoleCallback {
        void onSuccess(UserRole userRole);
        void onError(String error);
    }

    public interface UserRoleListCallback {
        void onSuccess(List<UserRole> userRoles);
        void onError(String error);
    }

    // Save user role
    public void saveUserRole(UserRole userRole, UserRoleCallback callback) {
        databaseReference.child(userRole.getUser_id()).setValue(userRole)
            .addOnSuccessListener(aVoid -> callback.onSuccess(userRole))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get user roles by user ID
    public void getUserRolesByUserId(String userId, UserRoleListCallback callback) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserRole role = new UserRole();
                    role.setId(dataSnapshot.child("id").getValue(String.class));
                    role.setUser_id(dataSnapshot.child("user_id").getValue(String.class));
                    role.setRole_type(dataSnapshot.child("role_type").getValue(String.class));
                    role.setCreated_date(dataSnapshot.child("created_date").getValue(Long.class));
                    role.setUpdated_date(dataSnapshot.child("updated_date").getValue(Long.class));

                    List<UserRole> result = new ArrayList<>();
                    result.add(role);
                    callback.onSuccess(result);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Delete user role
    public void deleteUserRole(String userRoleId, UserRoleCallback callback) {
        databaseReference.child(userRoleId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
