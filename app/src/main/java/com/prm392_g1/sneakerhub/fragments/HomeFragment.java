package com.prm392_g1.sneakerhub.fragments;

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

import com.prm392_g1.sneakerhub.R;

public class HomeFragment extends Fragment {

    private TextView textSalesAmount;
    private Button btnAddProduct;
    private Button btnViewOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        textSalesAmount = view.findViewById(R.id.text_sales_amount);
        btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnViewOrders = view.findViewById(R.id.btn_view_orders);

        // Set sample text for the sales amount
        textSalesAmount.setText("$1,234");

        // Set click listeners for buttons
        btnAddProduct.setOnClickListener(v ->
            Toast.makeText(getContext(), "Add Product clicked", Toast.LENGTH_SHORT).show()
        );

        btnViewOrders.setOnClickListener(v ->
            Toast.makeText(getContext(), "View Orders clicked", Toast.LENGTH_SHORT).show()
        );
    }
}
