package com.prm392_g1.sneakerhub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;
import java.util.List;
import java.util.UUID;

public class DatabaseTestActivity extends AppCompatActivity {

    // UI Components
    private TextInputEditText etUserId, etUserName, etUserEmail, etUserPhone;
    private TextInputEditText etProductId, etProductName, etProductPrice, etProductSize;
    private Button btnAddUser, btnGetUser, btnDeleteUser, btnGetAllUsers;
    private Button btnAddProduct, btnGetProduct, btnDeleteProduct, btnGetAllProducts;
    private Button btnClearAll;
    private TextView tvResults;

    // Repository instances
    private UserRepository userRepository;
    private ProductRepository productRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        initializeViews();
        initializeRepositories();
        setupClickListeners();

        appendResult("=== Firebase Realtime Database Test Started ===\n");
    }

    private void initializeViews() {
        // User input fields
        etUserId = findViewById(R.id.etUserId);
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserPhone = findViewById(R.id.etUserPhone);

        // Product input fields
        etProductId = findViewById(R.id.etProductId);
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductSize = findViewById(R.id.etProductSize);

        // User buttons
        btnAddUser = findViewById(R.id.btnAddUser);
        btnGetUser = findViewById(R.id.btnGetUser);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnGetAllUsers = findViewById(R.id.btnGetAllUsers);

        // Product buttons
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnGetProduct = findViewById(R.id.btnGetProduct);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);
        btnGetAllProducts = findViewById(R.id.btnGetAllProducts);

        // Other buttons
        btnClearAll = findViewById(R.id.btnClearAll);

        // Result display
        tvResults = findViewById(R.id.tvResults);
    }

    private void initializeRepositories() {
        userRepository = new UserRepository();
        productRepository = new ProductRepository();
    }

    private void setupClickListeners() {
        // User operations
        btnAddUser.setOnClickListener(v -> addUser());
        btnGetUser.setOnClickListener(v -> getUser());
        btnDeleteUser.setOnClickListener(v -> deleteUser());
        btnGetAllUsers.setOnClickListener(v -> getAllUsers());

        // Product operations
        btnAddProduct.setOnClickListener(v -> addProduct());
        btnGetProduct.setOnClickListener(v -> getProduct());
        btnDeleteProduct.setOnClickListener(v -> deleteProduct());
        btnGetAllProducts.setOnClickListener(v -> getAllProducts());

        // Clear all
        btnClearAll.setOnClickListener(v -> clearAllData());
    }

    private void addUser() {
        String userId = etUserId.getText().toString().trim();
        String userName = etUserName.getText().toString().trim();
        String userEmail = etUserEmail.getText().toString().trim();
        String userPhone = etUserPhone.getText().toString().trim();

        if (userId.isEmpty()) {
            userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
            etUserId.setText(userId);
        }

        if (userName.isEmpty()) {
            showToast("Please enter user name");
            return;
        }

        User user = new User();
        user.id = userId;
        user.name = userName;
        user.email = userEmail.isEmpty() ? userName.toLowerCase() + "@test.com" : userEmail;
        user.phone_number = userPhone;
        user.address = "Test Address";
        user.created_date = System.currentTimeMillis();
        user.updated_date = System.currentTimeMillis();
        user.avatar = "";
        user.is_banned = false;

        appendResult("\n--- Adding User ---");
        appendResult("User ID: " + user.id);
        appendResult("User Name: " + user.name);

        userRepository.saveUser(user, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User savedUser) {
                runOnUiThread(() -> {
                    appendResult("✅ User added successfully!");
                    showToast("User added successfully!");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error adding user: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void getUser() {
        String userId = etUserId.getText().toString().trim();

        if (userId.isEmpty()) {
            showToast("Please enter User ID");
            return;
        }

        appendResult("\n--- Getting User ---");
        appendResult("Searching for User ID: " + userId);

        userRepository.getUserById(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    appendResult("✅ User found:");
                    appendResult("ID: " + user.id);
                    appendResult("Name: " + user.name);
                    appendResult("Email: " + user.email);
                    appendResult("Phone: " + user.phone_number);
                    appendResult("Address: " + user.address);
                    appendResult("Banned: " + user.is_banned);

                    // Fill the form with retrieved data
                    etUserName.setText(user.name);
                    etUserEmail.setText(user.email);
                    etUserPhone.setText(user.phone_number);

                    showToast("User retrieved successfully!");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error getting user: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void deleteUser() {
        String userId = etUserId.getText().toString().trim();

        if (userId.isEmpty()) {
            showToast("Please enter User ID");
            return;
        }

        appendResult("\n--- Deleting User ---");
        appendResult("Deleting User ID: " + userId);

        userRepository.deleteUser(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    appendResult("✅ User deleted successfully!");
                    clearUserFields();
                    showToast("User deleted successfully!");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error deleting user: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void getAllUsers() {
        appendResult("\n--- Getting All Users ---");

        userRepository.getAllUsers(new UserRepository.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                runOnUiThread(() -> {
                    appendResult("✅ Found " + users.size() + " users:");
                    for (int i = 0; i < users.size(); i++) {
                        User user = users.get(i);
                        appendResult((i + 1) + ". " + user.name + " (" + user.id + ")");
                    }
                    showToast("Retrieved " + users.size() + " users");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error getting users: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void addProduct() {
        String productId = etProductId.getText().toString().trim();
        String productName = etProductName.getText().toString().trim();
        String productPrice = etProductPrice.getText().toString().trim();
        String productSize = etProductSize.getText().toString().trim();

        if (productId.isEmpty()) {
            productId = "product_" + UUID.randomUUID().toString().substring(0, 8);
            etProductId.setText(productId);
        }

        if (productName.isEmpty()) {
            showToast("Please enter product name");
            return;
        }

        Product product = new Product();
        product.id = productId;
        product.name = productName;
        product.description = "Test sneaker description";
        product.price = productPrice.isEmpty() ? 100.0 : Double.parseDouble(productPrice);
        product.image = "";
        product.created_date = System.currentTimeMillis();
        product.updated_date = System.currentTimeMillis();

        appendResult("\n--- Adding Product ---");
        appendResult("Product ID: " + product.id);
        appendResult("Product Name: " + product.name);
        appendResult("Price: $" + product.price);

        productRepository.saveProduct(product, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product savedProduct) {
                runOnUiThread(() -> {
                    appendResult("✅ Product added successfully!");
                    showToast("Product added successfully!");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error adding product: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void getProduct() {
        String productId = etProductId.getText().toString().trim();

        if (productId.isEmpty()) {
            showToast("Please enter Product ID");
            return;
        }

        appendResult("\n--- Getting Product ---");
        appendResult("Searching for Product ID: " + productId);

        productRepository.getProductById(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                runOnUiThread(() -> {
                    appendResult("✅ Product found:");
                    appendResult("ID: " + product.id);
                    appendResult("Name: " + product.name);
                    appendResult("Description: " + product.description);
                    appendResult("Price: $" + product.price);

                    // Fill the form with retrieved data
                    etProductName.setText(product.name);
                    etProductPrice.setText(String.valueOf(product.price));

                    showToast("Product retrieved successfully!");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error getting product: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void deleteProduct() {
        String productId = etProductId.getText().toString().trim();

        if (productId.isEmpty()) {
            showToast("Please enter Product ID");
            return;
        }

        appendResult("\n--- Deleting Product ---");
        appendResult("Deleting Product ID: " + productId);

        productRepository.deleteProduct(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                runOnUiThread(() -> {
                    appendResult("✅ Product deleted successfully!");
                    clearProductFields();
                    showToast("Product deleted successfully!");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error deleting product: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void getAllProducts() {
        appendResult("\n--- Getting All Products ---");

        productRepository.getAllProducts(new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                runOnUiThread(() -> {
                    appendResult("✅ Found " + products.size() + " products:");
                    for (int i = 0; i < products.size(); i++) {
                        Product product = products.get(i);
                        appendResult((i + 1) + ". " + product.name + " - $" + product.price + " (" + product.id + ")");
                    }
                    showToast("Retrieved " + products.size() + " products");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    appendResult("❌ Error getting products: " + error);
                    showToast("Error: " + error);
                });
            }
        });
    }

    private void clearAllData() {
        tvResults.setText("No operations performed yet...");
        clearUserFields();
        clearProductFields();
        appendResult("=== All data cleared ===\n");
        showToast("Display cleared");
    }

    private void clearUserFields() {
        etUserId.setText("");
        etUserName.setText("");
        etUserEmail.setText("");
        etUserPhone.setText("");
    }

    private void clearProductFields() {
        etProductId.setText("");
        etProductName.setText("");
        etProductPrice.setText("");
        etProductSize.setText("");
    }

    private void appendResult(String text) {
        runOnUiThread(() -> {
            String currentText = tvResults.getText().toString();
            tvResults.setText(currentText + "\n" + text);
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
