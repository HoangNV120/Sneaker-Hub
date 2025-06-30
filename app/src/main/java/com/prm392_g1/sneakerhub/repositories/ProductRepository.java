package com.prm392_g1.sneakerhub.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.entities.ProductVariant;
import com.prm392_g1.sneakerhub.utils.ProductUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductRepository {
    private static final String DATABASE_URL = "https://sneaker-hub-70299-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private static final String PRODUCTS_NODE = "products";
    private static final String VARIANTS_NODE = "product_variants";

    private DatabaseReference productsRef;
    private DatabaseReference variantsRef;

    public ProductRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        productsRef = database.getReference(PRODUCTS_NODE);
        variantsRef = database.getReference(VARIANTS_NODE);
    }

    // Interface for callbacks
    public interface ProductCallback {
        void onSuccess(Product product);
        void onError(String error);
    }

    public interface ProductListCallback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }

    public interface VariantCallback {
        void onSuccess(ProductVariant variant);
        void onError(String error);
    }

    public interface VariantListCallback {
        void onSuccess(List<ProductVariant> variants);
        void onError(String error);
    }

    // Save product only (without variants)
    public void saveProduct(Product product, ProductCallback callback) {
        // Ensure product has an ID
        if (product.id == null || product.id.isEmpty()) {
            product.id = UUID.randomUUID().toString();
        }

        // Update timestamp
        product.updated_date = System.currentTimeMillis();
        if (product.created_date == 0) {
            product.created_date = System.currentTimeMillis();
        }

        productsRef.child(product.id).setValue(product)
            .addOnSuccessListener(aVoid -> callback.onSuccess(product))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Save product variant
    public void saveProductVariant(ProductVariant variant, VariantCallback callback) {
        // Validate variant
        if (!ProductUtils.isValidVariant(variant)) {
            callback.onError("Invalid variant data");
            return;
        }

        // Ensure variant has an ID
        if (variant.id == null || variant.id.isEmpty()) {
            variant.id = UUID.randomUUID().toString();
        }

        // Update timestamp
        variant.updated_date = System.currentTimeMillis();
        if (variant.created_date == 0) {
            variant.created_date = System.currentTimeMillis();
        }

        variantsRef.child(variant.id).setValue(variant)
            .addOnSuccessListener(aVoid -> callback.onSuccess(variant))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Save multiple variants
    public void saveProductVariants(List<ProductVariant> variants, VariantListCallback callback) {
        if (variants == null || variants.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        Map<String, Object> variantUpdates = new HashMap<>();
        List<ProductVariant> processedVariants = new ArrayList<>();

        for (ProductVariant variant : variants) {
            // Validate variant
            if (!ProductUtils.isValidVariant(variant)) {
                callback.onError("Invalid variant: " + ProductUtils.getVariantDisplayName(variant));
                return;
            }

            // Ensure variant has an ID
            if (variant.id == null || variant.id.isEmpty()) {
                variant.id = UUID.randomUUID().toString();
            }

            // Update timestamp
            variant.updated_date = System.currentTimeMillis();
            if (variant.created_date == 0) {
                variant.created_date = System.currentTimeMillis();
            }

            variantUpdates.put(variant.id, variant);
            processedVariants.add(variant);
        }

        // Batch update all variants
        variantsRef.updateChildren(variantUpdates)
            .addOnSuccessListener(aVoid -> callback.onSuccess(processedVariants))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get product by ID (without variants)
    public void getProductById(String productId, ProductCallback callback) {
        productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    callback.onSuccess(product);
                } else {
                    callback.onError("Product not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Get all products (without variants)
    public void getAllProducts(ProductListCallback callback) {
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        products.add(product);
                    }
                }
                callback.onSuccess(products);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Get variants by product ID
    public void getVariantsByProductId(String productId, VariantListCallback callback) {
        variantsRef.orderByChild("product_id").equalTo(productId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<ProductVariant> variants = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductVariant variant = snapshot.getValue(ProductVariant.class);
                        if (variant != null) {
                            variants.add(variant);
                        }
                    }
                    callback.onSuccess(variants);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Delete product
    public void deleteProduct(String productId, ProductCallback callback) {
        productsRef.child(productId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Delete variant
    public void deleteVariant(String variantId, VariantCallback callback) {
        variantsRef.child(variantId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Delete all variants of a product
    public void deleteVariantsByProductId(String productId, VariantListCallback callback) {
        getVariantsByProductId(productId, new VariantListCallback() {
            @Override
            public void onSuccess(List<ProductVariant> variants) {
                if (variants.isEmpty()) {
                    callback.onSuccess(variants);
                    return;
                }

                Map<String, Object> deletions = new HashMap<>();
                for (ProductVariant variant : variants) {
                    deletions.put(variant.id, null);
                }

                variantsRef.updateChildren(deletions)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(variants))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // Search products by name
    public void searchProductsByName(String name, ProductListCallback callback) {
        Query query = productsRef.orderByChild("name")
            .startAt(name.toLowerCase())
            .endAt(name.toLowerCase() + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && product.name != null &&
                        product.name.toLowerCase().contains(name.toLowerCase())) {
                        products.add(product);
                    }
                }
                callback.onSuccess(products);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Get products by size (using variants)
    public void getProductsBySize(String size, ProductListCallback callback) {
        variantsRef.orderByChild("size").equalTo(size)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> productIds = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductVariant variant = snapshot.getValue(ProductVariant.class);
                        if (variant != null && variant.product_id != null &&
                            !productIds.contains(variant.product_id)) {
                            productIds.add(variant.product_id);
                        }
                    }

                    getProductsByIds(productIds, callback);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Get products by colour (using variants)
    public void getProductsByColour(String colour, ProductListCallback callback) {
        variantsRef.orderByChild("colour").equalTo(colour)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> productIds = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductVariant variant = snapshot.getValue(ProductVariant.class);
                        if (variant != null && variant.product_id != null &&
                            !productIds.contains(variant.product_id)) {
                            productIds.add(variant.product_id);
                        }
                    }

                    getProductsByIds(productIds, callback);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
    }

    // Helper method to get products by IDs
    private void getProductsByIds(List<String> productIds, ProductListCallback callback) {
        if (productIds.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        List<Product> products = new ArrayList<>();
        int[] completed = {0};

        for (String productId : productIds) {
            getProductById(productId, new ProductCallback() {
                @Override
                public void onSuccess(Product product) {
                    synchronized (products) {
                        if (product != null) {
                            products.add(product);
                        }
                        completed[0]++;
                        if (completed[0] == productIds.size()) {
                            callback.onSuccess(products);
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    synchronized (products) {
                        completed[0]++;
                        if (completed[0] == productIds.size()) {
                            callback.onSuccess(products);
                        }
                    }
                }
            });
        }
    }
}
