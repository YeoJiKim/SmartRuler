package com.example.administrator.smartruler;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.administrator.smartruler.aboutCamera.*;
import com.example.administrator.smartruler.navigationItems.*;
import com.example.administrator.smartruler.sensor.*;

public class MainActivity extends AppCompatActivity
      implements  NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener{

    ScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ScannerView(this);
        scannerView.setContentView(R.layout.activity_main);
        setContentView(scannerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener( this);

        Intent startServiceIntent = new Intent(this,OrientationService.class);
        startService(startServiceIntent);
        //Log.d("MainActivity","!!!!!"+OrientationDetector.getD());
    }

    @Override
    protected  void  onResume(){
        super.onResume();
        scannerView.startCamera(-1);
    }


    @Override
    protected  void onPause(){
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        Intent stopServiceIntent = new Intent(this,OrientationService.class);
        stopService(stopServiceIntent);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.fab:
                CatchPicture catchPicture = new CatchPicture(MainActivity.this,scannerView.mCamera);
                catchPicture.capture();
                break;
            default:
                break;
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MainActivity.this,VideoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
