package com.prm392_g1.sneakerhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392_g1.sneakerhub.entities.Order;
import com.prm392_g1.sneakerhub.entities.Product;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    
    private List<Order> cartItems;
    private List<Product> products;
    private CartActivity cartActivity;
    
    public CartAdapter(List<Order> cartItems, List<Product> products, CartActivity cartActivity) {
        this.cartItems = cartItems;
        this.products = products;
        this.cartActivity = cartActivity;
    }
    
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Order order = cartItems.get(position);
        Product product = getProductById(order.product_id);
        
        if (product != null) {
            holder.tvProductName.setText(product.name);
            holder.tvProductPrice.setText(String.format("$%.2f", product.price));
            holder.tvQuantity.setText(String.valueOf(order.amount));
            holder.tvTotalPrice.setText(String.format("$%.2f", order.total_price));
        } else {
            holder.tvProductName.setText("Product not found");
            holder.tvProductPrice.setText("$0.00");
            holder.tvQuantity.setText(String.valueOf(order.amount));
            holder.tvTotalPrice.setText(String.format("$%.2f", order.total_price));
        }
        
        // Quantity controls
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (currentQuantity > 1) {
                cartActivity.updateOrderQuantity(order, currentQuantity - 1);
            }
        });
        
        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            cartActivity.updateOrderQuantity(order, currentQuantity + 1);
        });
        
        // Cancel order
        holder.btnCancel.setOnClickListener(v -> {
            cartActivity.cancelOrder(order);
        });
    }
    
    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    
    private Product getProductById(String productId) {
        for (Product product : products) {
            if (product.id.equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvQuantity, tvTotalPrice;
        ImageButton btnDecrease, btnIncrease;
        Button btnCancel;
        
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
} 