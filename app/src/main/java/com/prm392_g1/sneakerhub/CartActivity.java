package com.prm392_g1.sneakerhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392_g1.sneakerhub.entities.Order;
import com.prm392_g1.sneakerhub.entities.OrderHistory;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.enums.StatusEnum;
import com.prm392_g1.sneakerhub.repositories.OrderRepository;
import com.prm392_g1.sneakerhub.repositories.OrderHistoryRepository;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private TextView tvTotalPrice, tvEmptyCart;
    private Button btnCheckout, btnViewHistory;
    private CartAdapter cartAdapter;
    
    private OrderRepository orderRepository;
    private OrderHistoryRepository orderHistoryRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    
    private List<Order> cartItems;
    private List<Product> products;
    private User currentUser;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        initializeViews();
        initializeRepositories();
        loadCartItems();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        
        cartItems = new ArrayList<>();
        products = new ArrayList<>();
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, products, this);
        recyclerView.setAdapter(cartAdapter);
        
        btnCheckout.setOnClickListener(v -> checkoutOrder());
        btnViewHistory.setOnClickListener(v -> viewOrderHistory());
        
        // Add test data button for development
        Button btnAddTestData = findViewById(R.id.btnAddTestData);
        if (btnAddTestData != null) {
            btnAddTestData.setOnClickListener(v -> addTestDataToCart());
        }
    }
    
    private void initializeRepositories() {
        orderRepository = new OrderRepository();
        orderHistoryRepository = new OrderHistoryRepository();
        productRepository = new ProductRepository();
        userRepository = new UserRepository();
        
        // For demo purposes, using a test user
        // In real app, get current user from session
        currentUser = new User();
        currentUser.id = "340959d9-0c73-482a-ae92-23beb9c2e908";
        currentUser.name = "TestRegister";
        currentUser.email = "Test123@gmail.com";
    }
    
    private void loadCartItems() {
        // Load cart items with status IS_IN_CART for current user
        orderRepository.getOrdersByUserId(currentUser.id, new OrderRepository.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                cartItems.clear();
                for (Order order : orders) {
                    if (StatusEnum.IS_IN_CART.name().equals(order.status)) {
                        cartItems.add(order);
                    }
                }
                
                if (cartItems.isEmpty()) {
                    showEmptyCart();
                } else {
                    loadProductsForOrders();
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(CartActivity.this, "Error loading cart: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadProductsForOrders() {
        products.clear();
        final int[] loadedCount = {0};
        
        for (Order order : cartItems) {
            productRepository.getProductById(order.product_id, new ProductRepository.ProductCallback() {
                @Override
                public void onSuccess(Product product) {
                    products.add(product);
                    loadedCount[0]++;
                    
                    if (loadedCount[0] == cartItems.size()) {
                        updateUI();
                    }
                }
                
                @Override
                public void onError(String error) {
                    loadedCount[0]++;
                    if (loadedCount[0] == cartItems.size()) {
                        updateUI();
                    }
                }
            });
        }
    }
    
    private void updateUI() {
        cartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
        updateTotalPriceDisplay();
        
        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartContent();
        }
    }
    
    private void calculateTotalPrice() {
        totalPrice = 0.0;
        for (Order order : cartItems) {
            totalPrice += order.total_price;
        }
    }
    
    private void updateTotalPriceDisplay() {
        tvTotalPrice.setText(String.format("Total: $%.2f", totalPrice));
    }
    
    private void showEmptyCart() {
        tvEmptyCart.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvTotalPrice.setVisibility(View.GONE);
        btnCheckout.setVisibility(View.GONE);
    }
    
    private void showCartContent() {
        tvEmptyCart.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        tvTotalPrice.setVisibility(View.VISIBLE);
        btnCheckout.setVisibility(View.VISIBLE);
    }
    
    public void updateOrderQuantity(Order order, int newAmount) {
        order.amount = newAmount;
        order.total_price = order.amount * getProductPrice(order.product_id);
        order.updated_date = System.currentTimeMillis();
        
        orderRepository.saveOrder(order, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order updatedOrder) {
                Toast.makeText(CartActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                updateUI();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(CartActivity.this, "Error updating quantity: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void cancelOrder(Order order) {
        // Move to order history with CANCELLED status
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.id = UUID.randomUUID().toString();
        orderHistory.order_id = order.id;
        orderHistory.status = StatusEnum.CANCELLED.name();
        orderHistory.amount = order.amount;
        orderHistory.total_price = order.total_price;
        orderHistory.shipping_address = order.shipping_address;
        orderHistory.shipping_phone = order.shipping_phone;
        orderHistory.created_date = System.currentTimeMillis();
        orderHistory.updated_date = System.currentTimeMillis();
        
        orderHistoryRepository.saveOrderHistory(orderHistory, new OrderHistoryRepository.OrderHistoryCallback() {
            @Override
            public void onSuccess(OrderHistory savedHistory) {
                // Delete from cart
                orderRepository.deleteOrder(order.id, new OrderRepository.OrderCallback() {
                    @Override
                    public void onSuccess(Order deletedOrder) {
                        Toast.makeText(CartActivity.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                        loadCartItems();
                    }
                    
                    @Override
                    public void onError(String error) {
                        Toast.makeText(CartActivity.this, "Error cancelling order: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(CartActivity.this, "Error saving order history: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void checkoutOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Move all cart items to order history with PENDING status
        final int[] processedCount = {0};
        final int totalItems = cartItems.size();
        
        for (Order order : cartItems) {
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.id = UUID.randomUUID().toString();
            orderHistory.order_id = order.id;
            orderHistory.status = StatusEnum.PENDING.name();
            orderHistory.amount = order.amount;
            orderHistory.total_price = order.total_price;
            orderHistory.shipping_address = order.shipping_address;
            orderHistory.shipping_phone = order.shipping_phone;
            orderHistory.created_date = System.currentTimeMillis();
            orderHistory.updated_date = System.currentTimeMillis();
            
            orderHistoryRepository.saveOrderHistory(orderHistory, new OrderHistoryRepository.OrderHistoryCallback() {
                @Override
                public void onSuccess(OrderHistory savedHistory) {
                    // Delete from cart
                    orderRepository.deleteOrder(order.id, new OrderRepository.OrderCallback() {
                        @Override
                        public void onSuccess(Order deletedOrder) {
                            processedCount[0]++;
                            if (processedCount[0] == totalItems) {
                                Toast.makeText(CartActivity.this, "Checkout completed!", Toast.LENGTH_SHORT).show();
                                loadCartItems();
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            processedCount[0]++;
                            if (processedCount[0] == totalItems) {
                                Toast.makeText(CartActivity.this, "Checkout completed with some errors", Toast.LENGTH_SHORT).show();
                                loadCartItems();
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    processedCount[0]++;
                    if (processedCount[0] == totalItems) {
                        Toast.makeText(CartActivity.this, "Checkout completed with some errors", Toast.LENGTH_SHORT).show();
                        loadCartItems();
                    }
                }
            });
        }
    }
    
    private void viewOrderHistory() {
        Intent intent = new Intent(CartActivity.this, OrderHistoryActivity.class);
        startActivity(intent);
    }
    
    private double getProductPrice(String productId) {
        for (Product product : products) {
            if (product.id.equals(productId)) {
                return product.price;
            }
        }
        return 0.0;
    }
    
    private void addTestDataToCart() {
        // Create test products first
        createTestProducts();
    }
    
    private void createTestProducts() {
        // Create test product 1 - Nike Air Jordan
        Product product1 = new Product();
        product1.id = "test_product_1";
        product1.name = "Nike Air Jordan 1 Retro High OG";
        product1.description = "Classic Air Jordan 1 in Chicago colorway";
        product1.size = "US 10";
        product1.colour = "White/Black/Red";
        product1.price = 170.0;
        product1.image = "https://example.com/air-jordan-1.jpg";
        product1.created_date = System.currentTimeMillis();
        product1.updated_date = System.currentTimeMillis();
        
        // Create test product 2 - Adidas Ultraboost
        Product product2 = new Product();
        product2.id = "test_product_2";
        product2.name = "Adidas Ultraboost 22";
        product2.description = "Premium running shoes with Boost technology";
        product2.size = "US 9";
        product2.colour = "Black/White";
        product2.price = 190.0;
        product2.image = "https://example.com/ultraboost-22.jpg";
        product2.created_date = System.currentTimeMillis();
        product2.updated_date = System.currentTimeMillis();
        
        // Create test product 3 - Converse Chuck Taylor
        Product product3 = new Product();
        product3.id = "test_product_3";
        product3.name = "Converse Chuck Taylor All Star";
        product3.description = "Timeless classic canvas sneakers";
        product3.size = "US 8";
        product3.colour = "White";
        product3.price = 65.0;
        product3.image = "https://example.com/converse-chuck.jpg";
        product3.created_date = System.currentTimeMillis();
        product3.updated_date = System.currentTimeMillis();
        
        // Save products to database
        final int[] savedCount = {0};
        final int totalProducts = 3;
        
        productRepository.saveProduct(product1, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                savedCount[0]++;
                if (savedCount[0] == totalProducts) {
                    createTestOrders();
                }
            }
            
            @Override
            public void onError(String error) {
                savedCount[0]++;
                if (savedCount[0] == totalProducts) {
                    createTestOrders();
                }
            }
        });
        
        productRepository.saveProduct(product2, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                savedCount[0]++;
                if (savedCount[0] == totalProducts) {
                    createTestOrders();
                }
            }
            
            @Override
            public void onError(String error) {
                savedCount[0]++;
                if (savedCount[0] == totalProducts) {
                    createTestOrders();
                }
            }
        });
        
        productRepository.saveProduct(product3, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                savedCount[0]++;
                if (savedCount[0] == totalProducts) {
                    createTestOrders();
                }
            }
            
            @Override
            public void onError(String error) {
                savedCount[0]++;
                if (savedCount[0] == totalProducts) {
                    createTestOrders();
                }
            }
        });
    }
    
    private void createTestOrders() {
        // Create test order 1
        Order order1 = new Order();
        order1.id = UUID.randomUUID().toString();
        order1.product_id = "test_product_1";
        order1.user_id = "340959d9-0c73-482a-ae92-23beb9c2e908";
        order1.amount = 1;
        order1.total_price = 170.0;
        order1.status = StatusEnum.IS_IN_CART.name();
        order1.short_code = "TEST001";
        order1.shipping_address = "123 Test Street, Test City, 12345";
        order1.shipping_phone = "0123456789";
        order1.note = "Test order for checkout";
        order1.created_date = System.currentTimeMillis();
        order1.updated_date = System.currentTimeMillis();
        
        // Create test order 2
        Order order2 = new Order();
        order2.id = UUID.randomUUID().toString();
        order2.product_id = "test_product_2";
        order2.user_id = "340959d9-0c73-482a-ae92-23beb9c2e908";
        order2.amount = 2;
        order2.total_price = 380.0; // 2 * 190
        order2.status = StatusEnum.IS_IN_CART.name();
        order2.short_code = "TEST002";
        order2.shipping_address = "123 Test Street, Test City, 12345";
        order2.shipping_phone = "0123456789";
        order2.note = "Test order for checkout";
        order2.created_date = System.currentTimeMillis();
        order2.updated_date = System.currentTimeMillis();
        
        // Create test order 3
        Order order3 = new Order();
        order3.id = UUID.randomUUID().toString();
        order3.product_id = "test_product_3";
        order3.user_id = "340959d9-0c73-482a-ae92-23beb9c2e908";
        order3.amount = 1;
        order3.total_price = 65.0;
        order3.status = StatusEnum.IS_IN_CART.name();
        order3.short_code = "TEST003";
        order3.shipping_address = "123 Test Street, Test City, 12345";
        order3.shipping_phone = "0123456789";
        order3.note = "Test order for checkout";
        order3.created_date = System.currentTimeMillis();
        order3.updated_date = System.currentTimeMillis();
        
        // Save orders to database
        final int[] savedCount = {0};
        final int totalOrders = 3;
        
        orderRepository.saveOrder(order1, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                savedCount[0]++;
                if (savedCount[0] == totalOrders) {
                    Toast.makeText(CartActivity.this, "Test data added successfully!", Toast.LENGTH_SHORT).show();
                    loadCartItems(); // Reload cart to show new items
                }
            }
            
            @Override
            public void onError(String error) {
                savedCount[0]++;
                if (savedCount[0] == totalOrders) {
                    Toast.makeText(CartActivity.this, "Test data added with some errors", Toast.LENGTH_SHORT).show();
                    loadCartItems(); // Reload cart to show new items
                }
            }
        });
        
        orderRepository.saveOrder(order2, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                savedCount[0]++;
                if (savedCount[0] == totalOrders) {
                    Toast.makeText(CartActivity.this, "Test data added successfully!", Toast.LENGTH_SHORT).show();
                    loadCartItems(); // Reload cart to show new items
                }
            }
            
            @Override
            public void onError(String error) {
                savedCount[0]++;
                if (savedCount[0] == totalOrders) {
                    Toast.makeText(CartActivity.this, "Test data added with some errors", Toast.LENGTH_SHORT).show();
                    loadCartItems(); // Reload cart to show new items
                }
            }
        });
        
        orderRepository.saveOrder(order3, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                savedCount[0]++;
                if (savedCount[0] == totalOrders) {
                    Toast.makeText(CartActivity.this, "Test data added successfully!", Toast.LENGTH_SHORT).show();
                    loadCartItems(); // Reload cart to show new items
                }
            }
            
            @Override
            public void onError(String error) {
                savedCount[0]++;
                if (savedCount[0] == totalOrders) {
                    Toast.makeText(CartActivity.this, "Test data added with some errors", Toast.LENGTH_SHORT).show();
                    loadCartItems(); // Reload cart to show new items
                }
            }
        });
    }
} 