package com.prm392_g1.sneakerhub;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prm392_g1.sneakerhub.fragments.HomeAdminFragment;
import com.prm392_g1.sneakerhub.fragments.ManageFragment;
import com.prm392_g1.sneakerhub.fragments.ProfileFragment;
import com.prm392_g1.sneakerhub.fragments.StatisticsFragment;

public class AdminActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // Initialize the bottom navigation view
        bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set default fragment to Home
        if (savedInstanceState == null) {
            loadFragment(new HomeAdminFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_home) {
            fragment = new HomeAdminFragment();
        } else if (itemId == R.id.navigation_statistics) {
            fragment = new StatisticsFragment();
        } else if (itemId == R.id.navigation_manage) {
            fragment = new ManageFragment();
        } else if (itemId == R.id.navigation_profile) {
            fragment = new ProfileFragment();
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
