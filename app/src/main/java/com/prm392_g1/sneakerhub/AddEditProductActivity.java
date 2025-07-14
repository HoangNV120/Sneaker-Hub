package com.prm392_g1.sneakerhub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prm392_g1.sneakerhub.adapters.ProductVariantAdapter;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.entities.ProductVariant;
import com.prm392_g1.sneakerhub.fragments.AddVariantDialogFragment;
import com.prm392_g1.sneakerhub.fragments.EditVariantDialogFragment;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;
import com.prm392_g1.sneakerhub.utils.ProductUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddEditProductActivity extends AppCompatActivity implements ProductVariantAdapter.OnVariantClickListener {

    private EditText editProductName, editProductDescription, editProductPrice, editProductImage;
    private ImageView imagePreview;
    private RecyclerView recyclerVariants;
    private FloatingActionButton fabAddVariant;
    private Button btnSave, btnCancel;

    private ProductVariantAdapter variantAdapter;
    private List<ProductVariant> productVariants;
    private ProductRepository productRepository;

    private Product currentProduct; // null nếu là add, có giá trị nếu là edit
    private boolean isEditMode = false;

    public static final String EXTRA_PRODUCT_ID = "product_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_edit_product_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        setupRepositories();

        // Check if editing existing product
        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId != null) {
            isEditMode = true;
            loadProductForEdit(productId);
        } else {
            productVariants = new ArrayList<>();
            variantAdapter.setVariants(productVariants);
        }
    }

    private void initializeViews() {
        editProductName = findViewById(R.id.edit_product_name);
        editProductDescription = findViewById(R.id.edit_product_description);
        editProductPrice = findViewById(R.id.edit_product_price);
        editProductImage = findViewById(R.id.edit_product_image);
        imagePreview = findViewById(R.id.image_preview);
        recyclerVariants = findViewById(R.id.recycler_variants);
        fabAddVariant = findViewById(R.id.fab_add_variant);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupToolbar() {
        // Remove toolbar setup since we're using custom header
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView headerTitle = findViewById(R.id.header_title);

        btnBack.setOnClickListener(v -> finish());
        headerTitle.setText(isEditMode ? "Edit Product" : "Add New Product");
    }

    private void setupRecyclerView() {
        variantAdapter = new ProductVariantAdapter();
        variantAdapter.setOnVariantClickListener(this);
        recyclerVariants.setLayoutManager(new LinearLayoutManager(this));
        recyclerVariants.setAdapter(variantAdapter);
    }

    private void setupClickListeners() {
        fabAddVariant.setOnClickListener(v -> showAddVariantDialog());
        btnSave.setOnClickListener(v -> saveProduct());
        btnCancel.setOnClickListener(v -> finish());

        editProductImage.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                loadImagePreview();
            }
        });
    }

    private void setupRepositories() {
        productRepository = new ProductRepository();
    }

    private void loadProductForEdit(String productId) {
        productRepository.getProductById(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                currentProduct = product;
                populateProductData(product);
                loadProductVariants(productId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditProductActivity.this, "Error loading product: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateProductData(Product product) {
        editProductName.setText(product.name);
        editProductDescription.setText(product.description);
        editProductPrice.setText(String.valueOf(product.price));
        editProductImage.setText(product.image);
        loadImagePreview();
    }

    private void loadProductVariants(String productId) {
        productRepository.getVariantsByProductId(productId, new ProductRepository.VariantListCallback() {
            @Override
            public void onSuccess(List<ProductVariant> variants) {
                productVariants = variants;
                variantAdapter.setVariants(productVariants);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditProductActivity.this, "Error loading variants: " + error, Toast.LENGTH_SHORT).show();
                productVariants = new ArrayList<>();
                variantAdapter.setVariants(productVariants);
            }
        });
    }

    private void loadImagePreview() {
        String imageUrl = editProductImage.getText().toString().trim();
        if (!TextUtils.isEmpty(imageUrl)) {
            imagePreview.setVisibility(View.VISIBLE);

            // Use Glide to load image preview
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder_image)
                .error(R.drawable.ic_error_image)
                .centerCrop()
                .into(imagePreview);
        } else {
            imagePreview.setVisibility(View.GONE);
        }
    }

    private void showAddVariantDialog() {
        AddVariantDialogFragment dialog = new AddVariantDialogFragment();
        dialog.setExistingVariants(productVariants); // Pass existing variants for duplicate check
        dialog.setOnVariantAddedListener(variant -> {
            // Set product ID for the variant
            variant.product_id = currentProduct != null ? currentProduct.id : null;

            // Add to local list first
            productVariants.add(variant);
            variantAdapter.notifyItemInserted(productVariants.size() - 1);

            Toast.makeText(AddEditProductActivity.this, "Variant added", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "AddVariantDialog");
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(editProductName.getText().toString().trim())) {
            editProductName.setError("Product name is required");
            editProductName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editProductDescription.getText().toString().trim())) {
            editProductDescription.setError("Description is required");
            editProductDescription.requestFocus();
            return false;
        }

        String priceText = editProductPrice.getText().toString().trim();
        if (TextUtils.isEmpty(priceText)) {
            editProductPrice.setError("Price is required");
            editProductPrice.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                editProductPrice.setError("Price must be greater than 0");
                editProductPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editProductPrice.setError("Invalid price format");
            editProductPrice.requestFocus();
            return false;
        }

        if (productVariants.isEmpty()) {
            Toast.makeText(this, "Please add at least one product variant (size/color/stock)", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate all variants using ProductUtils
        for (ProductVariant variant : productVariants) {
            if (!ProductUtils.isValidVariant(variant)) {
                Toast.makeText(this, "Invalid variant: " + ProductUtils.getVariantDisplayName(variant), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void saveProduct() {
        if (!validateInput()) {
            return;
        }

        Product product = isEditMode ? currentProduct : new Product();

        // Set product data
        product.name = editProductName.getText().toString().trim();
        product.description = editProductDescription.getText().toString().trim();
        product.price = Double.parseDouble(editProductPrice.getText().toString().trim());
        product.image = editProductImage.getText().toString().trim();

        // Save product first
        productRepository.saveProduct(product, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product savedProduct) {
                // Update product ID for all variants
                for (ProductVariant variant : productVariants) {
                    variant.product_id = savedProduct.id;
                }

                // Save all variants
                productRepository.saveProductVariants(productVariants, new ProductRepository.VariantListCallback() {
                    @Override
                    public void onSuccess(List<ProductVariant> savedVariants) {
                        Toast.makeText(AddEditProductActivity.this,
                            isEditMode ? "Product updated successfully" : "Product added successfully",
                            Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AddEditProductActivity.this, "Error saving variants: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditProductActivity.this, "Error saving product: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ProductVariantAdapter.OnVariantClickListener implementation
    @Override
    public void onVariantEdit(ProductVariant variant, int position) {
        EditVariantDialogFragment dialog = new EditVariantDialogFragment();
        dialog.setVariant(variant);
        dialog.setExistingVariants(productVariants); // Pass existing variants for duplicate check
        dialog.setOnVariantUpdatedListener(updatedVariant -> {
            productVariants.set(position, updatedVariant);
            variantAdapter.notifyItemChanged(position);
            Toast.makeText(AddEditProductActivity.this, "Variant updated", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "EditVariantDialog");
    }

    @Override
    public void onVariantDelete(ProductVariant variant, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Variant")
            .setMessage("Are you sure you want to delete this variant (" + ProductUtils.getVariantDisplayName(variant) + ")?")
            .setPositiveButton("Delete", (dialog, which) -> {
                // Remove from local list
                productVariants.remove(position);
                variantAdapter.notifyItemRemoved(position);

                // If variant has ID (already saved), delete from database
                if (variant.id != null && !variant.id.isEmpty()) {
                    productRepository.deleteVariant(variant.id, new ProductRepository.VariantCallback() {
                        @Override
                        public void onSuccess(ProductVariant deletedVariant) {
                            Toast.makeText(AddEditProductActivity.this, "Variant deleted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddEditProductActivity.this, "Error deleting variant: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
