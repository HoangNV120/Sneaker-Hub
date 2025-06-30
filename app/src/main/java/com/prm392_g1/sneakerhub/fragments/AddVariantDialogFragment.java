package com.prm392_g1.sneakerhub.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.entities.ProductVariant;
import com.prm392_g1.sneakerhub.enums.ColourEnum;
import com.prm392_g1.sneakerhub.enums.SizeEnum;

import java.util.List;

public class AddVariantDialogFragment extends DialogFragment {

    private Spinner spinnerSize, spinnerColor;
    private EditText editStock;
    private OnVariantAddedListener listener;
    private List<ProductVariant> existingVariants;

    public interface OnVariantAddedListener {
        void onVariantAdded(ProductVariant variant);
    }

    public void setOnVariantAddedListener(OnVariantAddedListener listener) {
        this.listener = listener;
    }

    public void setExistingVariants(List<ProductVariant> existingVariants) {
        this.existingVariants = existingVariants;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_variant, null);

        spinnerSize = view.findViewById(R.id.spinner_size);
        spinnerColor = view.findViewById(R.id.spinner_color);
        editStock = view.findViewById(R.id.edit_stock);

        setupSpinners();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Product Variant")
               .setView(view)
               .setPositiveButton("Add", null)
               .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (validateInput()) {
                    ProductVariant variant = createVariant();
                    if (listener != null) {
                        listener.onVariantAdded(variant);
                    }
                    dialog.dismiss();
                }
            });
        });

        return dialog;
    }

    private void setupSpinners() {
        // Setup size spinner using SizeEnum
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_spinner_item, SizeEnum.getDisplayValues());
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);

        // Setup color spinner using ColourEnum
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_spinner_item, ColourEnum.getDisplayValues());
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);
    }

    private boolean validateInput() {
        String stockText = editStock.getText().toString().trim();
        if (TextUtils.isEmpty(stockText)) {
            editStock.setError("Stock quantity is required");
            return false;
        }

        try {
            int stock = Integer.parseInt(stockText);
            if (stock < 0) {
                editStock.setError("Stock quantity cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            editStock.setError("Invalid stock quantity");
            return false;
        }

        // Check for duplicate variants
        if (!validateUniqueVariant()) {
            return false;
        }

        return true;
    }

    private boolean validateUniqueVariant() {
        String selectedSize = spinnerSize.getSelectedItem().toString();
        String selectedColor = spinnerColor.getSelectedItem().toString();

        if (existingVariants != null) {
            for (ProductVariant variant : existingVariants) {
                if (variant.size.equals(selectedSize) && variant.colour.equals(selectedColor)) {
                    // Show error message
                    new AlertDialog.Builder(requireContext())
                        .setTitle("Duplicate Variant")
                        .setMessage("A variant with size " + selectedSize + " and color " + selectedColor + " already exists!")
                        .setPositiveButton("OK", null)
                        .show();
                    return false;
                }
            }
        }
        return true;
    }

    private ProductVariant createVariant() {
        String selectedSize = spinnerSize.getSelectedItem().toString();
        String selectedColor = spinnerColor.getSelectedItem().toString();
        int stock = Integer.parseInt(editStock.getText().toString().trim());

        // Validate that the selected values are valid enum values
        try {
            SizeEnum.fromDisplayValue(selectedSize);
            ColourEnum.fromDisplayValue(selectedColor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid size or color selected", e);
        }

        return new ProductVariant(selectedSize, selectedColor, stock);
    }
}
