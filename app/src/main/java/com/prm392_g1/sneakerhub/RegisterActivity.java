package com.prm392_g1.sneakerhub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import java.util.List;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullnameEditText, phoneEditText, emailEditText, addressEditText, passwordEditText;
    private Button registerButton;
    private TextView resultTextView;
    private UserRepository userRepository;

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
        userRepository = new UserRepository();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = fullnameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (fullname.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra trùng email
                userRepository.getAllUsers(new UserRepository.UserListCallback() {
                    @Override
                    public void onSuccess(List<User> users) {
                        for (User user : users) {
                            if (user.email != null && user.email.equalsIgnoreCase(email)) {
                                runOnUiThread(() -> {
                                    Toast.makeText(RegisterActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                                    resultTextView.setText("Email đã tồn tại!");
                                    resultTextView.setVisibility(View.VISIBLE);
                                });
                                return;
                            }
                        }
                        // Nếu không trùng email, tạo user mới
                        User newUser = new User();
                        newUser.id = UUID.randomUUID().toString();
                        newUser.name = fullname;
                        newUser.address = address;
                        newUser.email = email;
                        newUser.phone_number = phone;
                        newUser.created_date = System.currentTimeMillis();
                        newUser.updated_date = System.currentTimeMillis();
                        newUser.avatar = "";
                        newUser.is_banned = false;
                        userRepository.saveUser(newUser, new UserRepository.UserCallback() {
                            @Override
                            public void onSuccess(User savedUser) {
                                runOnUiThread(() -> {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    resultTextView.setText("Đăng ký thành công!");
                                    resultTextView.setVisibility(View.VISIBLE);
                                    // Clear input fields
                                    fullnameEditText.setText("");
                                    phoneEditText.setText("");
                                    emailEditText.setText("");
                                    addressEditText.setText("");
                                    passwordEditText.setText("");
                                });
                            }
                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(RegisterActivity.this, "Lỗi khi lưu user: " + error, Toast.LENGTH_SHORT).show();
                                    resultTextView.setText("Lỗi khi lưu user: " + error);
                                    resultTextView.setVisibility(View.VISIBLE);
                                });
                            }
                        });
                    }
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Lỗi khi kiểm tra email: " + error, Toast.LENGTH_SHORT).show();
                            resultTextView.setText("Lỗi khi kiểm tra email: " + error);
                            resultTextView.setVisibility(View.VISIBLE);
                        });
                    }
                });
            }
        });
    }
}
