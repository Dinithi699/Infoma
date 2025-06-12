package com.example.myapplication2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FirstFragment firstFragment; // All News
    private SportFragment sportsFragment;
    private TechnologyFragment technologyFragment;
    private EducationFragment educationFragment;
    private DevInfoFragment devInfoFragment;
    private UserInfoFragment userInfoFragment;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize Firebase
//        FirebaseApp.initializeApp(this);
//        mAuth = FirebaseAuth.getInstance();
//
//        // Initialize fragments
//        firstFragment = new FirstFragment(); // All News
//        sportsFragment = new SportFragment();
//        technologyFragment = new TechnologyFragment();
//        educationFragment = new EducationFragment();
//        devInfoFragment = new DevInfoFragment();
//        userInfoFragment = new UserInfoFragment();
//
//        // Set up bottom navigation
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//        // Set up profile image click listener
//        setupProfileImageClick();
//
//        // Load the All News fragment by default
//        loadFragment(firstFragment);
//        bottomNavigationView.setSelectedItemId(R.id.nav_all_news);
//
//        // Handle bottom navigation item selection
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            Fragment selectedFragment = null;
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_sports) {
//                selectedFragment = sportsFragment;
//            } else if (itemId == R.id.nav_technology) {
//                selectedFragment = technologyFragment;
//            } else if (itemId == R.id.nav_education) {
//                selectedFragment = educationFragment;
//            } else if (itemId == R.id.nav_all_news) {
//                selectedFragment = firstFragment;
//            }
//
//            if (selectedFragment != null) {
//                loadFragment(selectedFragment);
//                // Show bottom navigation for main fragments
//                bottomNavigationView.setVisibility(View.VISIBLE);
//                return true;
//            }
//            return false;
//        });
//
////        // Set up toolbar - UNCOMMENTED
////        Toolbar toolbar = findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
//
//        // Set up drawer layout and navigation view - UNCOMMENTED
////        drawerLayout = findViewById(R.id.drawer_layout);
////        NavigationView navigationView = findViewById(R.id.nav_view);
////        if (navigationView != null) {
////            navigationView.setNavigationItemSelectedListener(this);
////        }
//
////        // Set up drawer toggle - UNCOMMENTED
////        if (drawerLayout != null && toolbar != null) {
////            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
////                    this, drawerLayout, toolbar,
////                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
////            drawerLayout.addDrawerListener(toggle);
////            toggle.syncState();
////        }
//    }


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize Firebase
    FirebaseApp.initializeApp(this);
    mAuth = FirebaseAuth.getInstance();

    // Initialize fragments
    firstFragment = new FirstFragment(); // All News
    sportsFragment = new SportFragment();
    technologyFragment = new TechnologyFragment();
    educationFragment = new EducationFragment();
    devInfoFragment = new DevInfoFragment();
    userInfoFragment = new UserInfoFragment();

    // Set up toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Set up drawer layout and navigation view
    drawerLayout = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);
    if (navigationView != null) {
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Set up drawer toggle (hamburger icon)
    if (drawerLayout != null && toolbar != null) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Syncs the state of the hamburger icon
    }

    // Set up bottom navigation
    bottomNavigationView = findViewById(R.id.bottom_navigation);

    // Set up profile image click listener
    setupProfileImageClick();

    // Load the All News fragment by default
    loadFragment(firstFragment);
    bottomNavigationView.setSelectedItemId(R.id.nav_all_news);

    // Handle bottom navigation item selection
    bottomNavigationView.setOnItemSelectedListener(item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_sports) {
            selectedFragment = sportsFragment;
        } else if (itemId == R.id.nav_technology) {
            selectedFragment = technologyFragment;
        } else if (itemId == R.id.nav_education) {
            selectedFragment = educationFragment;
        } else if (itemId == R.id.nav_all_news) {
            selectedFragment = firstFragment;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            // Show bottom navigation for main fragments
            bottomNavigationView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    });
}
    private void setupProfileImageClick() {
        // Try to find profile image in the main layout first
        profileImageView = findViewById(R.id.profile_image);
        if (profileImageView != null) {
            profileImageView.setOnClickListener(v -> loadUserInfoFragment());
        }

        // Check if navigation view exists and is not null
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                ImageView headerProfileImage = headerView.findViewById(R.id.profile_image);
                if (headerProfileImage != null) {
                    headerProfileImage.setOnClickListener(v -> {
                        loadUserInfoFragment();
                        // Only close drawer if it exists
                        if (drawerLayout != null) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Method to load Developer Information Fragment
    public void loadDevInfoFragment() {
        loadFragment(devInfoFragment);
        // Hide bottom navigation when showing dev info
        bottomNavigationView.setVisibility(View.GONE);

        // Close drawer if open
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    // Handle navigation drawer item clicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home || id == R.id.nav_news) {
            loadNewsFragment();
        } else if (id == R.id.nav_informa) {
            loadDevInfoFragment();
        }
        // Add other menu items as needed

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    // Method to load User Information Fragment
    public void loadUserInfoFragment() {
        loadFragment(userInfoFragment);
        // Hide bottom navigation when showing user info
        bottomNavigationView.setVisibility(View.GONE);
    }

    // Method to load All News Fragment
    public void loadNewsFragment() {
        loadFragment(firstFragment);
        // Show bottom navigation
        bottomNavigationView.setVisibility(View.VISIBLE);
        // Set All News tab as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_all_news);
    }

    // Alternative method name for compatibility
    public void loadAllNewsFragment() {
        loadNewsFragment();
    }

    // Handle logout
    private void handleLogout() {
        if (mAuth != null) {
            mAuth.signOut();
            // Navigate to login activity or show login screen
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
        }
    }

    // Get current Firebase user
    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    @Override
    public void onBackPressed() {
        // Handle drawer close
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        // Handle back press for drawer in FirstFragment
        if (firstFragment != null && firstFragment.isVisible()) {
            if (firstFragment.onBackPressed()) {
                return; // Drawer was closed, don't call super
            }
        }

        // If we're in DevInfoFragment or UserInfoFragment, go back to All News
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof DevInfoFragment || currentFragment instanceof UserInfoFragment) {
            loadNewsFragment();
            return;
        }

        super.onBackPressed();
    }
}