package com.prm392_g1.sneakerhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392_g1.sneakerhub.entities.OrderHistory;
import com.prm392_g1.sneakerhub.entities.Product;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {
    
    private List<OrderHistory> orderHistories;
    private List<Product> products;
    private OrderHistoryActivity orderHistoryActivity;
    
    public OrderHistoryAdapter(List<OrderHistory> orderHistories, List<Product> products, OrderHistoryActivity orderHistoryActivity) {
        this.orderHistories = orderHistories;
        this.products = products;
        this.orderHistoryActivity = orderHistoryActivity;
    }
    
    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderHistoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        OrderHistory orderHistory = orderHistories.get(position);
        Product product = getProductById(orderHistory.order_id);
        
        // Set order details - Show product name prominently
        if (product != null) {
            holder.tvOrderId.setText(product.name);
            holder.tvProductPrice.setText(String.format("$%.2f", product.price));
        } else {
            holder.tvOrderId.setText("Product: Not found");
            holder.tvProductPrice.setText("$0.00");
        }
        
        holder.tvStatus.setText(orderHistoryActivity.getStatusDisplayName(orderHistory.status));
        holder.tvAmount.setText("Qty: " + orderHistory.amount);
        holder.tvTotalPrice.setText(String.format("$%.2f", orderHistory.total_price));
        holder.tvCreatedDate.setText("Created: " + orderHistoryActivity.formatDate(orderHistory.created_date));
        holder.tvUpdatedDate.setText("Updated: " + orderHistoryActivity.formatDate(orderHistory.updated_date));
        
        // Set shipping details
        if (orderHistory.shipping_address != null && !orderHistory.shipping_address.isEmpty()) {
            holder.tvShippingAddress.setText("Address: " + orderHistory.shipping_address);
        } else {
            holder.tvShippingAddress.setText("Address: Not specified");
        }
        
        if (orderHistory.shipping_phone != null && !orderHistory.shipping_phone.isEmpty()) {
            holder.tvShippingPhone.setText("Phone: " + orderHistory.shipping_phone);
        } else {
            holder.tvShippingPhone.setText("Phone: Not specified");
        }
        
        // Set status color
        switch (orderHistory.status) {
            case "COMPLETED":
                holder.tvStatus.setTextColor(0xFF4CAF50); // Green
                break;
            case "CANCELLED":
                holder.tvStatus.setTextColor(0xFFF44336); // Red
                break;
            case "PENDING":
                holder.tvStatus.setTextColor(0xFFFF9800); // Orange
                break;
            case "IN_PROGRESS":
                holder.tvStatus.setTextColor(0xFF2196F3); // Blue
                break;
            default:
                holder.tvStatus.setTextColor(0xFF666666); // Gray
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return orderHistories.size();
    }
    
    private Product getProductById(String orderId) {
        for (Product product : products) {
            if (product.id.equals("product_" + orderId)) {
                return product;
            }
        }
        return null;
    }
    
    static class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvProductName, tvProductPrice, tvAmount, tvTotalPrice;
        TextView tvCreatedDate, tvUpdatedDate, tvShippingAddress, tvShippingPhone;
        
        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvUpdatedDate = itemView.findViewById(R.id.tvUpdatedDate);
            tvShippingAddress = itemView.findViewById(R.id.tvShippingAddress);
            tvShippingPhone = itemView.findViewById(R.id.tvShippingPhone);
        }
    }
} 