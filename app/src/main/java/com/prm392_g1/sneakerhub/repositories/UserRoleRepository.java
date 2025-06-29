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
        databaseReference.child(userRole.id).setValue(userRole)
            .addOnSuccessListener(aVoid -> callback.onSuccess(userRole))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get user roles by user ID
    public void getUserRolesByUserId(String userId, UserRoleListCallback callback) {
        databaseReference.orderByChild("user_id").equalTo(userId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<UserRole> userRoles = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserRole userRole = snapshot.getValue(UserRole.class);
                        if (userRole != null) {
                            userRoles.add(userRole);
                        }
                    }
                    callback.onSuccess(userRoles);
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
