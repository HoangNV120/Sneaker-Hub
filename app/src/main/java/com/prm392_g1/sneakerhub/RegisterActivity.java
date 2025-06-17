package com.prm392_g1.sneakerhub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullnameEditText, phoneEditText, emailEditText, addressEditText, passwordEditText;
    private Button registerButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        fullnameEditText = findViewById(R.id.fullnameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String fullname = fullnameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String role = "User"; // Hardcoded role as User

                // Basic validation
                if (fullname.isEmpty() || phone.isEmpty() || email.isEmpty() ||
                        address.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate a random UUID for id
                String id = java.util.UUID.randomUUID().toString();

                // Simulate JSON-like output (without database)
                String result = "Registration Successful!\n" +
                        "ID: " + id + "\n" +
                        "Fullname: " + fullname + "\n" +
                        "Phone: " + phone + "\n" +
                        "Email: " + email + "\n" +
                        "Address: " + address + "\n" +
                        "Password: [Hidden for security]\n" +
                        "Role: " + role + "\n" +
                        "Is Active: true";

                // Display result
                resultTextView.setText(result);
                resultTextView.setVisibility(View.VISIBLE);

                // Clear input fields
                fullnameEditText.setText("");
                phoneEditText.setText("");
                emailEditText.setText("");
                addressEditText.setText("");
                passwordEditText.setText("");

                Toast.makeText(RegisterActivity.this, "Registration completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}