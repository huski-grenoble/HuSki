package com.example.huski;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.huski.Fragments.AddFragment;
import com.example.huski.Fragments.ListFragment;
import com.example.huski.Fragments.LocaliseFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //FOR FRAGMENTS
    // 1 - Declare fragment handled by Navigation Drawer
    private Fragment fragmentAdd;
    private Fragment fragmentList;
    private Fragment fragmentLocalise;

    //FOR DATAS
    // 2 - Identify each fragment with a number
    private static final int FRAGMENT_ADD = 0;
    private static final int FRAGMENT_LIST = 1;
    private static final int FRAGMENT_LOCALISE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            this.showFragment(FRAGMENT_ADD);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            this.showFragment(FRAGMENT_LIST);

        } else if (id == R.id.nav_slideshow) {
            this.showFragment(FRAGMENT_LOCALISE);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(int fragmentIdentifier){
        switch (fragmentIdentifier){
            case FRAGMENT_ADD :
                this.showNewsFragment();
                break;
            case FRAGMENT_LIST:
                this.showProfileFragment();
                break;
            case FRAGMENT_LOCALISE:
                this.showParamsFragment();
                break;
            default:
                break;
        }
    }

    // 4 - Create each fragment page and show it

    private void showNewsFragment(){
        if (this.fragmentAdd == null) this.fragmentAdd = AddFragment.newInstance();
        this.startTransactionFragment(this.fragmentAdd);
    }

    private void showParamsFragment(){
        if (this.fragmentList == null) this.fragmentList = ListFragment.newInstance();
        this.startTransactionFragment(this.fragmentList);
    }

    private void showProfileFragment(){
        if (this.fragmentLocalise == null) this.fragmentLocalise = LocaliseFragment.newInstance();
        this.startTransactionFragment(this.fragmentLocalise);
    }

    // ---

    // 3 - Generic method that will replace and show a fragment inside the MainActivity Frame Layout
    private void startTransactionFragment(Fragment fragment){
        if (!fragment.isVisible()){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.drawer_layout, fragment).commit();
        }
    }
}
