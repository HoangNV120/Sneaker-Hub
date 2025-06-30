package com.prm392_g1.sneakerhub.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.entities.Product;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onProductEdit(Product product);
        void onProductDelete(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView textName, textPrice;
        private ImageView imageProduct, btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_product_price);
            imageProduct = itemView.findViewById(R.id.image_product);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
        }

        public void bind(Product product) {
            // Display basic product info only
            textName.setText(product.name);
            textPrice.setText(NumberFormat.getCurrencyInstance(Locale.US).format(product.price));

            // Load product image using Glide
            loadProductImage(product.image);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductEdit(product);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductDelete(product);
                }
            });
        }

        private void loadProductImage(String imageUrl) {
            RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_placeholder_image)
                .error(R.drawable.ic_error_image)
                .transform(new CenterCrop(), new RoundedCorners(16));

            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imageProduct);
            } else {
                Glide.with(itemView.getContext())
                    .load(R.drawable.ic_placeholder_image)
                    .apply(requestOptions)
                    .into(imageProduct);
            }
        }
    }
}
