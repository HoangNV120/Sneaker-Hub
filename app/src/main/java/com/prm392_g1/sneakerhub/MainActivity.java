package com.prm392_g1.sneakerhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the database test button
        Button btnDatabaseTest = findViewById(R.id.btnDatabaseTest);
        btnDatabaseTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DatabaseTestActivity.class);
            startActivity(intent);
        });

        // Automatically launch CartActivity when app starts
        Intent intent = new Intent(MainActivity.this, CartActivity.class);
        startActivity(intent);
        
        // Optional: Close MainActivity after launching CartActivity
        // finish();
    }
}