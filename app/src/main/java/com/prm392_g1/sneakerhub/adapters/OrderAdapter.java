package com.prm392_g1.sneakerhub.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.entities.Order;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders = new ArrayList<>();
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onOrderStatusUpdate(Order order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView textOrderId, textTotalPrice, textStatus, textDate, textCustomer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textTotalPrice = itemView.findViewById(R.id.text_total_price);
            textStatus = itemView.findViewById(R.id.text_order_status);
            textDate = itemView.findViewById(R.id.text_order_date);
            textCustomer = itemView.findViewById(R.id.text_customer_id);
        }

        public void bind(Order order) {
            textOrderId.setText("Order #" + order.short_code);
            textTotalPrice.setText(NumberFormat.getCurrencyInstance(Locale.US).format(order.total_price));
            textStatus.setText(order.status);
            textCustomer.setText("Customer: " + order.user_id);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            textDate.setText(dateFormat.format(new Date(order.created_date)));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });

            textStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderStatusUpdate(order);
                }
            });
        }
    }
}
