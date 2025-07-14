package com.prm392_g1.sneakerhub.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.AddEditProductActivity;
import com.prm392_g1.sneakerhub.adapters.ProductAdapter;
import com.prm392_g1.sneakerhub.adapters.UserAdapter;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.entities.Order;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.entities.ProductVariant;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;
import com.prm392_g1.sneakerhub.repositories.OrderRepository;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import com.prm392_g1.sneakerhub.adapters.OrderAdapter;
import com.prm392_g1.sneakerhub.utils.ProductUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ManageFragment extends Fragment implements ProductAdapter.OnProductClickListener, OrderAdapter.OnOrderClickListener, UserAdapter.OnUserClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    private ProductAdapter productAdapter;
    private OrderAdapter orderAdapter;
    private UserAdapter userAdapter;

    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    private List<Product> allProducts = new ArrayList<>();
    private List<Order> allOrders = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();

    private String currentTab = "Products";

    // Activity Result Launchers for handling add/edit product
    private final ActivityResultLauncher<Intent> addProductLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Refresh products list
                loadProducts();
                Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
            }
        });

    private final ActivityResultLauncher<Intent> editProductLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Refresh products list
                loadProducts();
                Toast.makeText(getContext(), "Product updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRepositories();
        setupAdapters();
        setupTabLayout();
        setupFab();
        loadInitialData();
    }

    private void initializeViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        recyclerView = view.findViewById(R.id.recycler_products);
        fabAdd = view.findViewById(R.id.fab_add);
    }

    private void setupRepositories() {
        productRepository = new ProductRepository();
        orderRepository = new OrderRepository();
        userRepository = new UserRepository();
    }

    private void setupAdapters() {
        productAdapter = new ProductAdapter();
        productAdapter.setOnProductClickListener(this);

        orderAdapter = new OrderAdapter();
        orderAdapter.setOnOrderClickListener(this);

        userAdapter = new UserAdapter();
        userAdapter.setOnUserClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productAdapter);
    }

    private void setupTabLayout() {
        // Clear existing tabs and add new ones
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        tabLayout.addTab(tabLayout.newTab().setText("Orders"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getText().toString();
                switchTabContent();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            switch (currentTab) {
                case "Products":
                    // Launch AddEditProductActivity instead of dialog
                    Intent intent = new Intent(getContext(), AddEditProductActivity.class);
                    addProductLauncher.launch(intent);
                    break;
                case "Orders":
                    Toast.makeText(getContext(), "Orders are created by customers", Toast.LENGTH_SHORT).show();
                    break;
                case "Users":
                    Toast.makeText(getContext(), "Users register themselves", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void loadInitialData() {
        loadProducts();
        loadOrders();
        loadUsers();
    }

    private void loadProducts() {
        productRepository.getAllProducts(new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts = products;
                if ("Products".equals(currentTab)) {
                    productAdapter.setProducts(products);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading products: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrders() {
        orderRepository.getAllOrders(new OrderRepository.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                allOrders = orders;
                if ("Orders".equals(currentTab)) {
                    orderAdapter.setOrders(orders);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading orders: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers() {
        userRepository.getAllUsers(new UserRepository.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers = users;
                if ("Users".equals(currentTab)) {
                    userAdapter.setUsers(users);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading users: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchTabContent() {
        switch (currentTab) {
            case "Products":
                recyclerView.setAdapter(productAdapter);
                productAdapter.setProducts(allProducts);
                fabAdd.show();
                break;
            case "Orders":
                recyclerView.setAdapter(orderAdapter);
                orderAdapter.setOrders(allOrders);
                fabAdd.hide();
                break;
            case "Users":
                recyclerView.setAdapter(userAdapter);
                userAdapter.setUsers(allUsers);
                fabAdd.hide();
                break;
        }
    }

    // ProductAdapter.OnProductClickListener implementation
    @Override
    public void onProductClick(Product product) {
        showProductDetailsDialog(product);
    }

    @Override
    public void onProductEdit(Product product) {
        showEditProductDialog(product);
    }

    @Override
    public void onProductDelete(Product product) {
        showDeleteProductConfirmation(product);
    }

    // OrderAdapter.OnOrderClickListener implementation
    @Override
    public void onOrderClick(Order order) {
        showOrderDetailsDialog(order);
    }

    @Override
    public void onOrderStatusUpdate(Order order) {
        showOrderStatusUpdateDialog(order);
    }

    // UserAdapter.OnUserClickListener implementation
    @Override
    public void onUserClick(User user) {
        showUserDetailsDialog(user);
    }

    @Override
    public void onUserBan(User user) {
        showBanUserConfirmation(user);
    }

    @Override
    public void onUserUnban(User user) {
        showUnbanUserConfirmation(user);
    }

    @Override
    public void onUserDelete(User user) {
        showDeleteUserConfirmation(user);
    }

    private void showProductDetailsDialog(Product product) {
        // Load variants for this product first
        productRepository.getVariantsByProductId(product.id, new ProductRepository.VariantListCallback() {
            @Override
            public void onSuccess(List<ProductVariant> variants) {
                StringBuilder details = new StringBuilder();
                details.append("Name: ").append(product.name).append("\n");
                details.append("Description: ").append(product.description).append("\n");
                details.append("Price: $").append(product.price).append("\n");
                details.append("Image: ").append(product.image != null ? product.image : "None").append("\n\n");

                // Add variant information
                if (variants.isEmpty()) {
                    details.append("No variants available");
                } else {
                    details.append("Available Variants:\n");
                    for (ProductVariant variant : variants) {
                        details.append("• ").append(ProductUtils.getVariantFullInfo(variant)).append("\n");
                    }
                    details.append("\nTotal Stock: ").append(ProductUtils.getTotalStock(variants));
                    details.append("\nIn Stock: ").append(ProductUtils.isInStock(variants) ? "Yes" : "No");
                }

                new AlertDialog.Builder(getContext())
                    .setTitle("Product Details")
                    .setMessage(details.toString())
                    .setPositiveButton("Edit", (dialog, which) -> showEditProductDialog(product))
                    .setNegativeButton("Close", null)
                    .show();
            }

            @Override
            public void onError(String error) {
                // Show basic product info if variants can't be loaded
                String details = "Name: " + product.name + "\n" +
                               "Description: " + product.description + "\n" +
                               "Price: $" + product.price + "\n" +
                               "Image: " + (product.image != null ? product.image : "None") + "\n\n" +
                               "Error loading variants: " + error;

                new AlertDialog.Builder(getContext())
                    .setTitle("Product Details")
                    .setMessage(details)
                    .setPositiveButton("Edit", (dialog, which) -> showEditProductDialog(product))
                    .setNegativeButton("Close", null)
                    .show();
            }
        });
    }

    private void showEditProductDialog(Product product) {
        // Launch AddEditProductActivity in edit mode
        Intent intent = new Intent(getContext(), AddEditProductActivity.class);
        intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, product.id);
        editProductLauncher.launch(intent);
    }

    private void showDeleteProductConfirmation(Product product) {
        new AlertDialog.Builder(getContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete " + product.name + "?\n\nThis will also delete all its variants.")
            .setPositiveButton("Delete", (dialog, which) -> deleteProduct(product))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteProduct(Product product) {
        // First delete all variants, then delete the product
        productRepository.deleteVariantsByProductId(product.id, new ProductRepository.VariantListCallback() {
            @Override
            public void onSuccess(List<ProductVariant> deletedVariants) {
                // Now delete the product
                productRepository.deleteProduct(product.id, new ProductRepository.ProductCallback() {
                    @Override
                    public void onSuccess(Product deletedProduct) {
                        Toast.makeText(getContext(), "Product and " + deletedVariants.size() + " variants deleted successfully", Toast.LENGTH_SHORT).show();
                        loadProducts(); // Refresh the list
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error deleting product: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error deleting variants: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderDetailsDialog(Order order) {
        String details = "Order ID: " + order.short_code + "\n" +
                        "Customer: " + order.user_id + "\n" +
                        "Product ID: " + order.product_id + "\n" +
                        "Amount: " + order.amount + "\n" +
                        "Total: $" + order.total_price + "\n" +
                        "Status: " + order.status + "\n" +
                        "Address: " + order.shipping_address + "\n" +
                        "Phone: " + order.shipping_phone;

        new AlertDialog.Builder(getContext())
            .setTitle("Order Details")
            .setMessage(details)
            .setPositiveButton("Update Status", (dialog, which) -> showOrderStatusUpdateDialog(order))
            .setNegativeButton("Close", null)
            .show();
    }

    private void showOrderStatusUpdateDialog(Order order) {
        String[] statuses = {"pending", "confirmed", "shipping", "delivered", "cancelled"};

        new AlertDialog.Builder(getContext())
            .setTitle("Update Order Status")
            .setItems(statuses, (dialog, which) -> {
                String newStatus = statuses[which];
                updateOrderStatus(order, newStatus);
            })
            .show();
    }

    private void updateOrderStatus(Order order, String newStatus) {
        orderRepository.updateOrderStatus(order.id, newStatus, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order updatedOrder) {
                Toast.makeText(getContext(), "Order status updated", Toast.LENGTH_SHORT).show();
                loadOrders(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error updating order: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserDetailsDialog(User user) {
        String details = "Name: " + (user.name != null ? user.name : "N/A") + "\n" +
                        "Email: " + (user.email != null ? user.email : "N/A") + "\n" +
                        "Phone: " + (user.phone_number != null ? user.phone_number : "N/A") + "\n" +
                        "Address: " + (user.address != null ? user.address : "N/A") + "\n" +
                        "Status: " + (user.is_banned ? "Banned" : "Active") + "\n" +
                        "User ID: " + user.id;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("User Details")
               .setMessage(details)
               .setPositiveButton("Manage", (dialog, which) -> showUserManagementOptions(user))
               .setNegativeButton("Close", null)
               .show();
    }

    private void showUserManagementOptions(User user) {
        String[] options;
        if (user.is_banned) {
            options = new String[]{"Unban User", "Delete User", "View Orders History"};
        } else {
            options = new String[]{"Ban User", "Delete User", "View Orders History"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Manage User: " + (user.name != null ? user.name : "Unknown"))
               .setItems(options, (dialog, which) -> {
                   switch (which) {
                       case 0:
                           if (user.is_banned) {
                               showUnbanUserConfirmation(user);
                           } else {
                               showBanUserConfirmation(user);
                           }
                           break;
                       case 1:
                           showDeleteUserConfirmation(user);
                           break;
                       case 2:
                           showUserOrdersHistory(user);
                           break;
                   }
               })
               .show();
    }

    private void showBanUserConfirmation(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ban User")
               .setMessage("Are you sure you want to ban user: " + (user.name != null ? user.name : user.email) + "?")
               .setPositiveButton("Ban", (dialog, which) -> banUser(user))
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showUnbanUserConfirmation(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Unban User")
               .setMessage("Are you sure you want to unban user: " + (user.name != null ? user.name : user.email) + "?")
               .setPositiveButton("Unban", (dialog, which) -> unbanUser(user))
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showDeleteUserConfirmation(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete User")
               .setMessage("WARNING: This will permanently delete user: " + (user.name != null ? user.name : user.email) +
                          " and all their data. This action cannot be undone!\n\nAre you sure?")
               .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showUserOrdersHistory(User user) {
        orderRepository.getOrdersByUserId(user.id, new OrderRepository.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (orders.isEmpty()) {
                    Toast.makeText(getContext(), "No orders found for this user", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] orderInfo = new String[orders.size()];
                for (int i = 0; i < orders.size(); i++) {
                    Order order = orders.get(i);
                    orderInfo[i] = "Order #" + order.short_code + " - $" +
                                  String.format(Locale.getDefault(), "%.2f", order.total_price) + " (" + order.status + ")";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Orders History (" + orders.size() + " orders)")
                       .setItems(orderInfo, (dialog, which) -> {
                           // Show order details when clicked
                           Order selectedOrder = orders.get(which);
                           showOrderDetailsDialog(selectedOrder);
                       })
                       .setNegativeButton("Close", null)
                       .show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading user orders: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void banUser(User user) {
        userRepository.updateUserBanStatus(user.id, true, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                Toast.makeText(getContext(), "User banned successfully", Toast.LENGTH_SHORT).show();
                loadUsers(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error banning user: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unbanUser(User user) {
        userRepository.updateUserBanStatus(user.id, false, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                Toast.makeText(getContext(), "User unbanned successfully", Toast.LENGTH_SHORT).show();
                loadUsers(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error unbanning user: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(User user) {
        userRepository.deleteUser(user.id, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User deletedUser) {
                Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                loadUsers(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error deleting user: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
