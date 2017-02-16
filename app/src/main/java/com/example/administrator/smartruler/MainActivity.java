package com.example.administrator.smartruler;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.smartruler.aboutCamera.CatchPicture;
import com.example.administrator.smartruler.aboutCamera.ScannerView;
import com.example.administrator.smartruler.navigationItems.VideoActivity;
import com.example.administrator.smartruler.sensor.OrientationDetector;
import com.example.administrator.smartruler.sensor.OrientationService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int GETDISTANCE = 1;
    private static final int GETHEIGHT = 2;
    private int changeDirection = 1;

    private ScannerView scannerView;
    private OrientationService.OrientationBinder mBinder;
    private Thread thread;
    private TextView measurement_text;
    private Button changeOrientation_btn;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case GETDISTANCE:
                    measurement_text.setText(""+ msg.getData().getFloat("distance"));
                    break;
                case GETHEIGHT:
                    measurement_text.setText("" + msg.getData().getFloat("height"));
                    break;
                default:
                    break;
            }
        }
    };

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

        measurement_text = (TextView)findViewById(R.id.measurement);
        changeOrientation_btn = (Button)findViewById(R.id.changeOrientation);
        changeOrientation_btn.setOnClickListener(this);

        startOrientationService();

        if(thread == null){
            thread = new Thread(){
              @Override
                public void run(){

                  while(true){
                      try{
                          Thread.sleep(500);
                      }catch(InterruptedException e){
                          e.printStackTrace();
                      }

                      if(OrientationService.STARTSERVICE){
                          if(changeDirection % 2 == 1){
                              Message msg = Message.obtain();
                              msg.what = GETDISTANCE;
                              Bundle data = new Bundle();
                              data.putFloat("distance", OrientationDetector.resultOfDistance);
                              msg.setData(data);
                              handler.sendMessage(msg);

                          }else{
                              Message msg = Message.obtain();
                              msg.what = GETHEIGHT;
                              Bundle data = new Bundle();
                              data.putFloat("height", OrientationDetector.resultOfHeight);
                              msg.setData(data);
                              handler.sendMessage(msg);
                          }
                      }
                  }
              }
            };
            thread.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera(-1);
    }

    private void startOrientationService(){
        Intent startServiceIntent = new Intent(this, OrientationService.class);
        startService(startServiceIntent);
        bindService(startServiceIntent,connection,BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (OrientationService.OrientationBinder)iBinder;

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder = null;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopOrientationService();
        unbindService(connection);
    }

    private void stopOrientationService(){
        Intent stopServiceIntent = new Intent(this, OrientationService.class);
        stopService(stopServiceIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeOrientation:
                changeDirection++;
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.screenshots:
                CatchPicture catchPicture = new CatchPicture(MainActivity.this, ScannerView.mCamera);
                catchPicture.capture();
                break;
            default:
        }
        return true;
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
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
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
