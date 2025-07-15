package com.prm392_g1.sneakerhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.prm392_g1.sneakerhub.security.AuthService;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String LOGIN_IMAGE_LOGO = "https://test-s3-aws-bucket-quannt.s3.ap-southeast-2.amazonaws.com//custom-nike-dunk-high-by-you-shoes.png";
    private TextView tvBackToLogin;
    private Button btnGetOtp;
    private Button btnConfirmReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.ivLogo);
        Glide.with(this)
                .load(LOGIN_IMAGE_LOGO)
                .into(imageView);

        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        btnGetOtp = findViewById(R.id.btnGetOtp);
        btnConfirmReset = findViewById(R.id.btnConfirmReset);

        tvBackToLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        btnGetOtp.setOnClickListener(v -> {

        });
        btnConfirmReset.setOnClickListener(v -> {

        });
    }
}