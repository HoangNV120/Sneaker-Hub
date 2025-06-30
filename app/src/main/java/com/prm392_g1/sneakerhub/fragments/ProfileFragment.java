package com.prm392_g1.sneakerhub.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.repositories.UserRepository;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView textAdminName, textAdminEmail, textAdminRole;
    private Button btnEditProfile, btnChangePassword, btnSettings, btnLogout;

    private UserRepository userRepository;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupFirebase();
        setupClickListeners();
        loadProfileData();
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        textAdminName = view.findViewById(R.id.text_admin_name);
        textAdminEmail = view.findViewById(R.id.text_admin_email);
        textAdminRole = view.findViewById(R.id.text_admin_role);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
        sharedPreferences = requireContext().getSharedPreferences("SneakerHubPrefs", MODE_PRIVATE);
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnSettings.setOnClickListener(v -> showSettingsDialog());
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void loadProfileData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            textAdminEmail.setText(currentUser.getEmail());

            // Load user details from database
            userRepository.getUserById(currentUser.getUid(), new UserRepository.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        textAdminName.setText(user.name != null ? user.name : "Admin User");
                        textAdminRole.setText("Administrator");
                    } else {
                        // Set default values if user not found in database
                        textAdminName.setText("Admin User");
                        textAdminRole.setText("Administrator");
                    }
                }

                @Override
                public void onError(String error) {
                    // Set default values on error
                    textAdminName.setText("Admin User");
                    textAdminRole.setText("Administrator");
                }
            });
        } else {
            // Handle case where user is not authenticated
            textAdminName.setText("Unknown User");
            textAdminEmail.setText("No email");
            textAdminRole.setText("Not logged in");
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);

        // Initialize dialog views (you'll need to create this layout)
        builder.setTitle("Edit Profile")
               .setView(dialogView)
               .setPositiveButton("Save", (dialog, which) -> {
                   // Handle profile update
                   Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Password")
               .setMessage("You will receive an email with instructions to reset your password.")
               .setPositiveButton("Send Email", (dialog, which) -> {
                   FirebaseUser user = firebaseAuth.getCurrentUser();
                   if (user != null && user.getEmail() != null) {
                       firebaseAuth.sendPasswordResetEmail(user.getEmail())
                           .addOnSuccessListener(aVoid ->
                               Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show())
                           .addOnFailureListener(e ->
                               Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showSettingsDialog() {
        String[] settings = {
            "Notification Settings",
            "Data Management",
            "Privacy Settings",
            "Backup & Restore",
            "About App"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Settings")
               .setItems(settings, (dialog, which) -> {
                   switch (which) {
                       case 0:
                           showNotificationSettings();
                           break;
                       case 1:
                           showDataManagementSettings();
                           break;
                       case 2:
                           showPrivacySettings();
                           break;
                       case 3:
                           showBackupSettings();
                           break;
                       case 4:
                           showAboutDialog();
                           break;
                   }
               })
               .show();
    }

    private void showNotificationSettings() {
        boolean[] checkedItems = {
            sharedPreferences.getBoolean("notifications_orders", true),
            sharedPreferences.getBoolean("notifications_products", true),
            sharedPreferences.getBoolean("notifications_users", false)
        };

        String[] items = {"Order Notifications", "Product Notifications", "User Notifications"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Notification Settings")
               .setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
                   checkedItems[which] = isChecked;
               })
               .setPositiveButton("Save", (dialog, which) -> {
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putBoolean("notifications_orders", checkedItems[0]);
                   editor.putBoolean("notifications_products", checkedItems[1]);
                   editor.putBoolean("notifications_users", checkedItems[2]);
                   editor.apply();
                   Toast.makeText(getContext(), "Notification settings saved", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showDataManagementSettings() {
        String[] options = {"Export Data", "Clear Cache", "Reset Statistics"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Data Management")
               .setItems(options, (dialog, which) -> {
                   switch (which) {
                       case 0:
                           Toast.makeText(getContext(), "Data export feature coming soon", Toast.LENGTH_SHORT).show();
                           break;
                       case 1:
                           clearAppCache();
                           break;
                       case 2:
                           showResetStatisticsConfirmation();
                           break;
                   }
               })
               .show();
    }

    private void showPrivacySettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Privacy Settings")
               .setMessage("Privacy settings and data protection options")
               .setPositiveButton("OK", null)
               .show();
    }

    private void showBackupSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Backup & Restore")
               .setMessage("Backup and restore functionality")
               .setPositiveButton("OK", null)
               .show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("About SneakerHub Admin")
               .setMessage("SneakerHub Admin Panel\nVersion 1.0.0\n\nDeveloped by PRM392 Group 1\n\nThis application helps manage the SneakerHub e-commerce platform.")
               .setPositiveButton("OK", null)
               .show();
    }

    private void clearAppCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Clear Cache")
               .setMessage("Are you sure you want to clear the app cache? This will remove temporary data.")
               .setPositiveButton("Clear", (dialog, which) -> {
                   // Clear shared preferences cache
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.remove("cached_products");
                   editor.remove("cached_orders");
                   editor.apply();

                   Toast.makeText(getContext(), "Cache cleared successfully", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showResetStatisticsConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reset Statistics")
               .setMessage("This will reset all statistical data. This action cannot be undone.")
               .setPositiveButton("Reset", (dialog, which) -> {
                   // Reset statistics data
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.remove("statistics_data");
                   editor.apply();

                   Toast.makeText(getContext(), "Statistics reset successfully", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logout")
               .setMessage("Are you sure you want to logout?")
               .setPositiveButton("Logout", (dialog, which) -> {
                   performLogout();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void performLogout() {
        // Sign out from Firebase
        firebaseAuth.signOut();

        // Clear shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to login screen
        if (getActivity() != null) {
            getActivity().finish();
            // You might want to start a LoginActivity here
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
