package com.prm392_g1.sneakerhub.service;

import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.entities.UserRole;
import com.prm392_g1.sneakerhub.enums.RoleEnum;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import com.prm392_g1.sneakerhub.repositories.UserRoleRepository;

import java.util.UUID;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserService {
    private static final String KEY_FIREBASE_PHONE = "phone_number";
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public interface RegisterCallback {
        void onSuccess();
        void onError(String message);
    }

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public void registerUser(String phone, String password, String confirmPassword, RegisterCallback callback) {
        if (!password.equals(confirmPassword)) {
            callback.onError("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            callback.onError("Password must be at least 6 characters");
            return;
        }

        userRepository.getChildByKey(KEY_FIREBASE_PHONE, phone, User.class, new UserRepository.DataCallback<User>() {
            @Override
            public void onSuccess(User existingUser) {
                callback.onError("Phone number already registered");
            }

            @Override
            public void onError(String error) {
                User user = new User();
                user.id = UUID.randomUUID().toString();
                user.name = "";
                user.address = "";
                user.email = "";
                user.setPassword(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
                user.phone_number = phone;
                user.created_date = System.currentTimeMillis();
                user.updated_date = System.currentTimeMillis();
                user.avatar = "";
                user.is_banned = false;

                userRepository.saveUser(user, new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        UserRole role = new UserRole();
                        role.setId(UUID.randomUUID().toString());
                        role.setUser_id(user.id);
                        role.setRole_type(RoleEnum.CUSTOMER.getValue());
                        role.setCreated_date(System.currentTimeMillis());
                        role.setUpdated_date(System.currentTimeMillis());

                        userRoleRepository.saveUserRole(role, new UserRoleRepository.UserRoleCallback() {
                            @Override
                            public void onSuccess(UserRole userRole) {
                                callback.onSuccess();
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError("User saved but failed to save role: " + error);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Failed to register user: " + error);
                    }
                });
            }
        });
    }
}
