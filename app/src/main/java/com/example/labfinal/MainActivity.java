package com.example.labfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
/**
 * The program allows users to enter a search term to find articles
 * in the Guardian newspaper related to that term.
 * @author Sanghyun Lee
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "lab-final";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ArrayList<Fragment> fragments;

    private int position;                       // navigation menu location

    public static final String SHARED_PREFERENCES_NAME = "finalProject";
    public static final String KEYWORD = "keyword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // display home button on the action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // remove action bar shadow (line appears on navigation bar)
        getSupportActionBar().setElevation(0);

        // get SharedPreferences
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String keyword = preferences.getString(KEYWORD, "");

        // fragment configuration
        this.fragments = new ArrayList<>();
        this.fragments.add(new SearchFragment());           // article search
        this.fragments.add(new FavoritesFragment());        // favorites

        // keyword value to pass to SearchFragment
        Bundle bundle = new Bundle();
        bundle.putString(KEYWORD, keyword);

        // Set value to Argument of SearchFragment
        this.fragments.get(0).setArguments(bundle);

        // navigation menu==================================================
        this.drawerLayout = findViewById(R.id.drawerLayout);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, this.drawerLayout, R.string.drawer_opened, R.string.drawer_closed);

        this.drawerLayout.addDrawerListener(this.actionBarDrawerToggle);
        this.actionBarDrawerToggle.syncState();

        NavigationView navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this);
        // ====================================================================

        // get navigation header view
        LinearLayout layHeader =  (LinearLayout) navView.getHeaderView(0);
        // display version number
        ((TextView) layHeader.findViewById(R.id.txtVersion)).setText(getAppVersion());

        this.position = 0;
        navView.setCheckedItem(R.id.nav_search);        // select article search menu
        setTitle(R.string.menu_search);                 // display the title as an article search

        getSupportFragmentManager().beginTransaction().add(R.id.layContent, this.fragments.get(this.position)).commit();
    }

    @Override
    public void onBackPressed() {
        // navigation menu opens.
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // close menu
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // shut down the app
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // help menu on action bar
        inflater.inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (this.actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.menu_help) {
            // help
            String message = "";
            switch (this.position) {
                case 0:
                    message = getString(R.string.help_search);
                    break;
                case 1:
                    message = getString(R.string.help_favorites);
                    break;
            }

            // display AlertDialog
            new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.dialog_ok, null)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_title_help)
                    .setMessage(message)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                // article search
                this.position = 0;
                break;
            case R.id.nav_favorites:
                // favorites
                this.position = 1;
                break;
        }

        // change fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.layContent, this.fragments.get(position)).commit();
        setTitle(item.getTitle());              // change title

        // close navigation menu
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Get app version
     */
    private String getAppVersion() {
        String versionName;

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }

        return "version " + versionName;
    }

}