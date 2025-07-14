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

public class EditVariantDialogFragment extends DialogFragment {

    private Spinner spinnerSize, spinnerColor;
    private EditText editStock;
    private ProductVariant currentVariant;
    private OnVariantUpdatedListener listener;
    private List<ProductVariant> existingVariants;

    public interface OnVariantUpdatedListener {
        void onVariantUpdated(ProductVariant variant);
    }

    public void setVariant(ProductVariant variant) {
        this.currentVariant = variant;
    }

    public void setOnVariantUpdatedListener(OnVariantUpdatedListener listener) {
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
        populateCurrentData();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Product Variant")
               .setView(view)
               .setPositiveButton("Update", null)
               .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (validateInput()) {
                    updateVariant();
                    if (listener != null) {
                        listener.onVariantUpdated(currentVariant);
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

    private void populateCurrentData() {
        if (currentVariant != null) {
            // Set size spinner selection
            try {
                SizeEnum sizeEnum = SizeEnum.fromDisplayValue(currentVariant.size);
                String[] sizeValues = SizeEnum.getDisplayValues();
                for (int i = 0; i < sizeValues.length; i++) {
                    if (sizeValues[i].equals(sizeEnum.getDisplayValue())) {
                        spinnerSize.setSelection(i);
                        break;
                    }
                }
            } catch (IllegalArgumentException e) {
                // If current size is not in enum, select first item
                spinnerSize.setSelection(0);
            }

            // Set color spinner selection
            try {
                ColourEnum colorEnum = ColourEnum.fromDisplayValue(currentVariant.colour);
                String[] colorValues = ColourEnum.getDisplayValues();
                for (int i = 0; i < colorValues.length; i++) {
                    if (colorValues[i].equals(colorEnum.getDisplayValue())) {
                        spinnerColor.setSelection(i);
                        break;
                    }
                }
            } catch (IllegalArgumentException e) {
                // If current color is not in enum, select first item
                spinnerColor.setSelection(0);
            }

            editStock.setText(String.valueOf(currentVariant.stock_quantity));
        }
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

        // Check for duplicate variants (exclude current variant being edited)
        if (!validateUniqueVariant()) {
            return false;
        }

        return true;
    }

    private boolean validateUniqueVariant() {
        String selectedSize = spinnerSize.getSelectedItem().toString();
        String selectedColor = spinnerColor.getSelectedItem().toString();

        if (existingVariants != null && currentVariant != null) {
            for (ProductVariant variant : existingVariants) {
                // Skip the current variant being edited
                if (variant.id != null && variant.id.equals(currentVariant.id)) {
                    continue;
                }

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

    private void updateVariant() {
        String selectedSize = spinnerSize.getSelectedItem().toString();
        String selectedColor = spinnerColor.getSelectedItem().toString();

        // Validate that the selected values are valid enum values
        try {
            SizeEnum.fromDisplayValue(selectedSize);
            ColourEnum.fromDisplayValue(selectedColor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid size or color selected", e);
        }

        currentVariant.size = selectedSize;
        currentVariant.colour = selectedColor;
        currentVariant.stock_quantity = Integer.parseInt(editStock.getText().toString().trim());
        currentVariant.is_available = currentVariant.stock_quantity > 0;
        currentVariant.updated_date = System.currentTimeMillis();
    }
}
