package com.prm392_g1.sneakerhub;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prm392_g1.sneakerhub.fragments.HomeFragment;
import com.prm392_g1.sneakerhub.fragments.ManageFragment;
import com.prm392_g1.sneakerhub.fragments.ProfileFragment;
import com.prm392_g1.sneakerhub.fragments.StatisticsFragment;

public class AdminActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize the bottom navigation view
        bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Set default fragment to Home
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_home) {
            fragment = new HomeFragment();
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
