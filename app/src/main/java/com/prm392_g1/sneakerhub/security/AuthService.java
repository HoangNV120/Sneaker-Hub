package com.prm392_g1.sneakerhub.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.entities.UserRole;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import com.prm392_g1.sneakerhub.repositories.UserRoleRepository;

import java.util.Date;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthService {
    private static final String JWT_SECRET = "quanuirhfehvh78222h3fh2398fhfwgeffu2378fh82h8723hf9wevub8w";
    private static final String PREF_NAME = "Authentication";
    private static final String KEY_FIREBASE_LOGIN = "phone";
    private final Context context;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public AuthService(Context context) {
        this.context = context;
        this.userRepository = new UserRepository();
        this.userRoleRepository = new UserRoleRepository();
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String error);
    }

    public void authenticate(String phone, String password, AuthCallback callback) {
        userRepository.getChildByKey(KEY_FIREBASE_LOGIN, phone, User.class, new UserRepository.DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                if (result.verified) {
                    userRoleRepository.getUserRolesByUserId(user.id, new UserRoleRepository.UserRoleListCallback() {
                        @Override
                        public void onSuccess(List<UserRole> userRoles) {
                            String role = userRoles.get(0).getRole_type();
                            String email = user.email;

                            String jwt = JWT.create()
                                    .withClaim("phone", phone)
                                    .withClaim("role", role)
                                    .withClaim("email", email)
                                    .withIssuedAt(new Date())
                                    .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                                    .sign(Algorithm.HMAC256(JWT_SECRET));

                            saveToken(jwt);
                            Log.d("AuthService", "Login success, token saved: " + jwt);
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String error) {
                            callback.onError("Get role failed: " + error);
                        }
                    });
                } else {
                    callback.onError("Incorrect phone or password");
                }
            }

            @Override
            public void onError(String message) {
                callback.onError("Incorrect phone or password");
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("Bearer ", token).apply();
    }

    public String getSavedToken() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("Bearer ", null);
    }

    public void logout() {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().remove("Bearer ").apply();
    }

    public boolean isLoggedIn() {
        String token = getSavedToken();
        if (token == null) return false;

        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            return jwt.getExpiresAt().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getPhoneFromToken() {
        String token = getSavedToken();
        if (token == null) return null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            return jwt.getClaim("phone").asString();
        } catch (Exception e) {
            return null;
        }
    }
}
