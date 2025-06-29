package com.prm392_g1.sneakerhub.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.prm392_g1.sneakerhub.entities.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String DATABASE_URL = "https://sneaker-hub-70299-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private static final String PRODUCTS_NODE = "products";

    private DatabaseReference databaseReference;

    public ProductRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = database.getReference(PRODUCTS_NODE);
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

    // Save product
    public void saveProduct(Product product, ProductCallback callback) {
        databaseReference.child(product.id).setValue(product)
            .addOnSuccessListener(aVoid -> callback.onSuccess(product))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get product by ID
    public void getProductById(String productId, ProductCallback callback) {
        databaseReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Get all products
    public void getAllProducts(ProductListCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Search products by name
    public void searchProductsByName(String name, ProductListCallback callback) {
        Query query = databaseReference.orderByChild("name")
            .startAt(name)
            .endAt(name + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Filter products by size
    public void getProductsBySize(String size, ProductListCallback callback) {
        databaseReference.orderByChild("size").equalTo(size)
            .addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Filter products by colour
    public void getProductsByColour(String colour, ProductListCallback callback) {
        databaseReference.orderByChild("colour").equalTo(colour)
            .addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Delete product
    public void deleteProduct(String productId, ProductCallback callback) {
        databaseReference.child(productId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
