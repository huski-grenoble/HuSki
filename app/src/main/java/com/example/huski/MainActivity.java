package com.example.huski;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragmentList;
    private Fragment fragmentTool;
    private Fragment fragmentAdd;

    protected static final int  FRAGMENT_LIST = 0;
    protected static final int  FRAGMENT_TOOL = 1;
    protected static final int  FRAGMENT_ADD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        showFragment(FRAGMENT_LIST);
    }




   /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    } */

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().getBackStackEntryCount();
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
            this.showListFragment();
        }else if (id == R.id.nav_manage) {
            //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            this.showToolFragment();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFragment(int fragmentIdentifier){
        switch (fragmentIdentifier){
            case FRAGMENT_LIST :
                this.showListFragment();
                break;
            case FRAGMENT_TOOL :
                this.showToolFragment();
                break;
            case FRAGMENT_ADD :
                this.showAddFragment();
            default:
                break;
        }
    }
    private void showListFragment(){
        if (this.fragmentList == null) this.fragmentList = ListFragment.newInstance();
        this.startTransactionFragment(this.fragmentList);
    }

    private void showToolFragment(){
        if (this.fragmentTool == null) this.fragmentTool = ToolFragment.newInstance();
        this.startTransactionFragment(this.fragmentTool);
    }

    private void showAddFragment(){
        if (this.fragmentAdd == null) this.fragmentAdd = AddFragment.newInstance();
        this.startTransactionFragment(this.fragmentAdd);
    }

    public void startTransactionFragment(Fragment fragment){
        if (!fragment.isVisible()){
            //Toast.makeText(this, ""+fragment, Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_view, fragment, fragment.getClass().toString()).addToBackStack(fragment.getTag()).commit();
        }
    }

}
