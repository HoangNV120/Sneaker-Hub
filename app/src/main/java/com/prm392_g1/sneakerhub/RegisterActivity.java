package com.prm392_g1.sneakerhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import com.prm392_g1.sneakerhub.repositories.UserRoleRepository;
import com.prm392_g1.sneakerhub.service.UserService;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOGIN_IMAGE_LOGO = "https://test-s3-aws-bucket-quannt.s3.ap-southeast-2.amazonaws.com//custom-nike-dunk-high-by-you-shoes.png";
    private TextView tvGoToLogin;
    private Button btnRegister;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.ivLogo);
        Glide.with(this)
                .load(LOGIN_IMAGE_LOGO)
                .into(imageView);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        btnRegister = findViewById(R.id.btnRegister);

        tvGoToLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        userService = new UserService(new UserRepository(), new UserRoleRepository());
        btnRegister.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            userService.registerUser(phone, password, confirmPassword, new UserService.RegisterCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}