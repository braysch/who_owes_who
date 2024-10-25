package com.example.whooweswho.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.whooweswho.R;
import com.example.whooweswho.fragments.JournalsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up navigation menu
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationView navView = findViewById(R.id.nav_view);
        toolbar.setNavigationOnClickListener(view -> {
            drawerLayout.open();
        });

        // Journals is the default fragment
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, JournalsFragment.class, null)
                .commit();

        // Navigate to different fragments when items are selected
        navView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);

            if (menuItem.getItemId() == R.id.journals_item)
            {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, JournalsFragment.class, null)
                        .commit();
            }
            return true;
        });
    }
}