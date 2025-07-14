package com.prm392_g1.sneakerhub.utils;

import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.entities.ProductVariant;
import com.prm392_g1.sneakerhub.enums.SizeEnum;
import com.prm392_g1.sneakerhub.enums.ColourEnum;

import java.util.ArrayList;
import java.util.List;

public class ProductUtils {

    // Validate ProductVariant
    public static boolean isValidVariant(ProductVariant variant) {
        if (variant == null) return false;
        return isValidSize(variant.size) &&
               isValidColour(variant.colour) &&
               variant.stock_quantity >= 0;
    }

    // Check if size is valid
    public static boolean isValidSize(String size) {
        try {
            SizeEnum.fromDisplayValue(size);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Check if colour is valid
    public static boolean isValidColour(String colour) {
        try {
            ColourEnum.fromDisplayValue(colour);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Get total stock from variants
    public static int getTotalStock(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) return 0;
        int total = 0;
        for (ProductVariant variant : variants) {
            total += variant.stock_quantity;
        }
        return total;
    }

    // Check if product is in stock
    public static boolean isInStock(List<ProductVariant> variants) {
        return getTotalStock(variants) > 0;
    }

    // Get available sizes from variants
    public static List<String> getAvailableSizes(List<ProductVariant> variants) {
        List<String> sizes = new ArrayList<>();
        if (variants != null) {
            for (ProductVariant variant : variants) {
                if (variant.is_available && !sizes.contains(variant.size)) {
                    sizes.add(variant.size);
                }
            }
        }
        return sizes;
    }

    // Get available colors from variants
    public static List<String> getAvailableColors(List<ProductVariant> variants) {
        List<String> colors = new ArrayList<>();
        if (variants != null) {
            for (ProductVariant variant : variants) {
                if (variant.is_available && !colors.contains(variant.colour)) {
                    colors.add(variant.colour);
                }
            }
        }
        return colors;
    }

    // Find specific variant by size and color
    public static ProductVariant findVariant(List<ProductVariant> variants, String size, String color) {
        if (variants == null) return null;
        for (ProductVariant variant : variants) {
            if (variant.size.equals(size) && variant.colour.equals(color)) {
                return variant;
            }
        }
        return null;
    }

    // Get variant price (base price + adjustment)
    public static double getVariantPrice(Product product, String size, String color, List<ProductVariant> variants) {
        ProductVariant variant = findVariant(variants, size, color);
        if (variant != null) {
            return product.price + variant.price_adjustment;
        }
        return product.price;
    }

    // Check for duplicate variants
    public static boolean hasDuplicateVariant(List<ProductVariant> variants, String size, String color, String excludeId) {
        if (variants == null) return false;
        for (ProductVariant variant : variants) {
            if (excludeId != null && excludeId.equals(variant.id)) {
                continue; // Skip the variant being excluded (for edit mode)
            }
            if (variant.size.equals(size) && variant.colour.equals(color)) {
                return true;
            }
        }
        return false;
    }

    // Update variant stock
    public static void updateVariantStock(ProductVariant variant, int newStock) {
        if (variant != null) {
            variant.stock_quantity = newStock;
            variant.is_available = newStock > 0;
            variant.updated_date = System.currentTimeMillis();
        }
    }

    // Decrease variant stock
    public static boolean decreaseVariantStock(ProductVariant variant, int amount) {
        if (variant != null && amount > 0 && variant.stock_quantity >= amount) {
            updateVariantStock(variant, variant.stock_quantity - amount);
            return true;
        }
        return false;
    }

    // Increase variant stock
    public static void increaseVariantStock(ProductVariant variant, int amount) {
        if (variant != null && amount > 0) {
            updateVariantStock(variant, variant.stock_quantity + amount);
        }
    }

    // Get variant display name
    public static String getVariantDisplayName(ProductVariant variant) {
        if (variant == null) return "Unknown Variant";
        return "Size " + variant.size + " - " + variant.colour;
    }

    // Get variant full info string
    public static String getVariantFullInfo(ProductVariant variant) {
        if (variant == null) return "Unknown Variant";
        return getVariantDisplayName(variant) + " (Stock: " + variant.stock_quantity + ")";
    }
}
