package com.prm392_g1.sneakerhub.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.adapters.OrderAdapter;
import com.prm392_g1.sneakerhub.entities.Order;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.repositories.OrderRepository;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeAdminFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private TextView textSalesAmount, textTotalOrders, textTotalProducts, textPendingOrders;
    private Button btnAddProduct, btnViewOrders;
    private RecyclerView recyclerRecentOrders;
    private OrderAdapter orderAdapter;

    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    private List<Order> allOrders = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRepositories();
        setupRecyclerView();
        loadDashboardData();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        textSalesAmount = view.findViewById(R.id.text_sales_amount);
        textTotalOrders = view.findViewById(R.id.text_total_orders);
        textTotalProducts = view.findViewById(R.id.text_total_products);
        textPendingOrders = view.findViewById(R.id.text_pending_orders);
        btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnViewOrders = view.findViewById(R.id.btn_view_orders);
        recyclerRecentOrders = view.findViewById(R.id.recycler_recent_orders);
    }

    private void setupRepositories() {
        orderRepository = new OrderRepository();
        productRepository = new ProductRepository();
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter();
        orderAdapter.setOnOrderClickListener(this);
        recyclerRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRecentOrders.setAdapter(orderAdapter);
    }

    private void loadDashboardData() {
        loadOrders();
        loadProducts();
    }

    private void loadOrders() {
        orderRepository.getAllOrders(new OrderRepository.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                allOrders = orders;
                updateOrderStatistics();
                displayRecentOrders();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading orders: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        productRepository.getAllProducts(new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts = products;
                updateProductStatistics();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading products: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatistics() {
        double totalSales = 0;
        int pendingCount = 0;

        for (Order order : allOrders) {
            totalSales += order.total_price;
            if ("pending".equalsIgnoreCase(order.status)) {
                pendingCount++;
            }
        }

        textSalesAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(totalSales));
        textTotalOrders.setText(String.valueOf(allOrders.size()));
        textPendingOrders.setText(String.valueOf(pendingCount));
    }

    private void updateProductStatistics() {
        textTotalProducts.setText(String.valueOf(allProducts.size()));
    }

    private void displayRecentOrders() {
        // Show last 5 orders
        List<Order> recentOrders = new ArrayList<>();
        int count = Math.min(5, allOrders.size());

        // Sort by created_date descending and take first 5
        allOrders.sort((o1, o2) -> Long.compare(o2.created_date, o1.created_date));

        for (int i = 0; i < count; i++) {
            recentOrders.add(allOrders.get(i));
        }

        orderAdapter.setOrders(recentOrders);
    }

    private void setupClickListeners() {
        btnAddProduct.setOnClickListener(v -> {
            // Switch to Manage tab and trigger add product
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    getActivity().findViewById(R.id.admin_bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.navigation_manage);
                }
            }
        });

        btnViewOrders.setOnClickListener(v -> {
            // Show all orders dialog or navigate to orders screen
            showAllOrdersDialog();
        });
    }

    private void showAllOrdersDialog() {
        // Create a dialog to show all orders
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("All Orders (" + allOrders.size() + ")");

        String[] orderInfo = new String[allOrders.size()];
        for (int i = 0; i < allOrders.size(); i++) {
            Order order = allOrders.get(i);
            orderInfo[i] = "Order #" + order.short_code + " - " +
                          NumberFormat.getCurrencyInstance(Locale.US).format(order.total_price) +
                          " (" + order.status + ")";
        }

        builder.setItems(orderInfo, (dialog, which) -> {
            // Handle order selection
            Order selectedOrder = allOrders.get(which);
            showOrderDetails(selectedOrder);
        });

        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private void showOrderDetails(Order order) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Order Details");

        String details = "Order ID: " + order.short_code + "\n" +
                        "Customer: " + order.user_id + "\n" +
                        "Product ID: " + order.product_id + "\n" +
                        "Amount: " + order.amount + "\n" +
                        "Total: " + NumberFormat.getCurrencyInstance(Locale.US).format(order.total_price) + "\n" +
                        "Status: " + order.status + "\n" +
                        "Address: " + order.shipping_address + "\n" +
                        "Phone: " + order.shipping_phone + "\n" +
                        "Note: " + (order.note != null ? order.note : "None");

        builder.setMessage(details);
        builder.setPositiveButton("Update Status", (dialog, which) -> showStatusUpdateDialog(order));
        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private void showStatusUpdateDialog(Order order) {
        String[] statuses = {"pending", "confirmed", "shipping", "delivered", "cancelled"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update Order Status");
        builder.setItems(statuses, (dialog, which) -> {
            String newStatus = statuses[which];
            updateOrderStatus(order, newStatus);
        });
        builder.show();
    }

    private void updateOrderStatus(Order order, String newStatus) {
        orderRepository.updateOrderStatus(order.id, newStatus, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order updatedOrder) {
                Toast.makeText(getContext(), "Order status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                loadDashboardData(); // Refresh data
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error updating order: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(Order order) {
        showOrderDetails(order);
    }

    @Override
    public void onOrderStatusUpdate(Order order) {
        showStatusUpdateDialog(order);
    }
}
