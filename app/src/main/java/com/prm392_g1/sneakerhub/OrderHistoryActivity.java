package com.prm392_g1.sneakerhub;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392_g1.sneakerhub.entities.OrderHistory;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.repositories.OrderHistoryRepository;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private TextView tvEmptyHistory, tvTitle;
    private OrderHistoryAdapter orderHistoryAdapter;
    
    private OrderHistoryRepository orderHistoryRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    
    private List<OrderHistory> orderHistories;
    private List<Product> products;
    private User currentUser;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        
        initializeViews();
        initializeRepositories();
        loadOrderHistory();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        tvTitle = findViewById(R.id.tvTitle);
        
        orderHistories = new ArrayList<>();
        products = new ArrayList<>();
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryAdapter = new OrderHistoryAdapter(orderHistories, products, this);
        recyclerView.setAdapter(orderHistoryAdapter);
    }
    
    private void initializeRepositories() {
        orderHistoryRepository = new OrderHistoryRepository();
        productRepository = new ProductRepository();
        userRepository = new UserRepository();
        
        // For demo purposes, using a test user
        // In real app, get current user from session
        currentUser = new User();
        currentUser.id = "test_user_001";
        currentUser.name = "Test User";
        currentUser.email = "test@example.com";
    }
    
    private void loadOrderHistory() {
        // Load all order history for current user
        orderHistoryRepository.getAllOrderHistory(new OrderHistoryRepository.OrderHistoryListCallback() {
            @Override
            public void onSuccess(List<OrderHistory> histories) {
                orderHistories.clear();
                orderHistories.addAll(histories);
                
                if (orderHistories.isEmpty()) {
                    showEmptyHistory();
                } else {
                    loadProductsForOrderHistory();
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(OrderHistoryActivity.this, "Error loading order history: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadProductsForOrderHistory() {
        products.clear();
        final int[] loadedCount = {0};
        final int totalOrders = orderHistories.size();
        
        if (totalOrders == 0) {
            updateUI();
            return;
        }
        
        for (OrderHistory orderHistory : orderHistories) {
            // Get product info from the original order
            // For now, we'll use a placeholder approach
            Product placeholderProduct = new Product();
            placeholderProduct.id = "product_" + orderHistory.order_id;
            placeholderProduct.name = "Product from Order " + orderHistory.order_id.substring(0, 8);
            placeholderProduct.price = orderHistory.total_price / orderHistory.amount;
            products.add(placeholderProduct);
            loadedCount[0]++;
            
            if (loadedCount[0] == totalOrders) {
                updateUI();
            }
        }
    }
    
    private void updateUI() {
        orderHistoryAdapter.notifyDataSetChanged();
        
        if (orderHistories.isEmpty()) {
            showEmptyHistory();
        } else {
            showOrderHistoryContent();
        }
    }
    
    private void showEmptyHistory() {
        tvEmptyHistory.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvTitle.setText("📋 Order History (0 orders)");
    }
    
    private void showOrderHistoryContent() {
        tvEmptyHistory.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        tvTitle.setText("📋 Order History (" + orderHistories.size() + " orders)");
    }
    
    public String formatDate(long timestamp) {
        if (timestamp == 0) return "N/A";
        return dateFormat.format(new Date(timestamp));
    }
    
    public String getStatusDisplayName(String status) {
        switch (status) {
            case "PENDING":
                return "⏳ Pending";
            case "IN_PROGRESS":
                return "🔄 In Progress";
            case "COMPLETED":
                return "✅ Completed";
            case "CANCELLED":
                return "❌ Cancelled";
            default:
                return status;
        }
    }
} 