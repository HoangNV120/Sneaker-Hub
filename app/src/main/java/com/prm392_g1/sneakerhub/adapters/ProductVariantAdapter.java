package com.prm392_g1.sneakerhub.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.entities.ProductVariant;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantAdapter extends RecyclerView.Adapter<ProductVariantAdapter.VariantViewHolder> {
    private List<ProductVariant> variants = new ArrayList<>();
    private OnVariantClickListener listener;

    public interface OnVariantClickListener {
        void onVariantEdit(ProductVariant variant, int position);
        void onVariantDelete(ProductVariant variant, int position);
    }

    public void setOnVariantClickListener(OnVariantClickListener listener) {
        this.listener = listener;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_variant, parent, false);
        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        ProductVariant variant = variants.get(position);
        holder.bind(variant, position);
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    class VariantViewHolder extends RecyclerView.ViewHolder {
        private TextView textSize, textColor, textStock, textAvailability;
        private ImageView btnEdit, btnDelete;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
            textSize = itemView.findViewById(R.id.text_variant_size);
            textColor = itemView.findViewById(R.id.text_variant_color);
            textStock = itemView.findViewById(R.id.text_variant_stock);
            textAvailability = itemView.findViewById(R.id.text_variant_availability);
            btnEdit = itemView.findViewById(R.id.btn_edit_variant);
            btnDelete = itemView.findViewById(R.id.btn_delete_variant);
        }

        public void bind(ProductVariant variant, int position) {
            textSize.setText("Size: " + variant.size);
            textColor.setText("Color: " + variant.colour);
            textStock.setText("Stock: " + variant.stock_quantity);

            if (variant.stock_quantity > 0) {
                textAvailability.setText("Available");
                textAvailability.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                textAvailability.setText("Out of Stock");
                textAvailability.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVariantEdit(variant, position);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVariantDelete(variant, position);
                }
            });
        }
    }
}
