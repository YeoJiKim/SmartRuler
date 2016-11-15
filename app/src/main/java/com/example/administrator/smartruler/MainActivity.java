package com.example.administrator.smartruler;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    ScannerView scannerView;
    public static final int GETDISTANCE = 1;
    public static final int GETHEIGHT = 2;
    public int directionMeasure = 0;

    private TextView resultOfMeasure;
    private Button changDirectionOfMeasure;

    private OrientationService mService = new OrientationService();

    private Thread thread;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETDISTANCE:
                     resultOfMeasure.setText("" + OrientationDetector.resultOfDistance);
               break;
                case GETHEIGHT:
                    resultOfMeasure.setText("Height");
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(this);

        resultOfMeasure = (TextView) findViewById(R.id.resultOfMeasure);
        changDirectionOfMeasure = (Button) findViewById(R.id.changDirectionOfMeasure);
        assert changDirectionOfMeasure!= null;
        changDirectionOfMeasure.setOnClickListener(this);

        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (OrientationService.FLAG) {

                            Message message = new Message();
                            if (directionMeasure % 2 == 0) {
                                message.what = GETDISTANCE;
                            } else {
                                message.what = GETHEIGHT;
                            }
                            handler.sendMessage(message);
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

        startOrientationService();
    }

    private void startOrientationService(){
        Intent startServiceIntent = new Intent(this, OrientationService.class);
        startService(startServiceIntent);
    }
//private ServiceConnection connection = new ServiceConnection() {
//    @Override
//    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//
//        mService = ((OrientationService.OrientationBinder)iBinder).getService();
//        mService.registerCallback(mCallback);
//    }
//
//    @Override
//    public void onServiceDisconnected(ComponentName componentName) {
//        mService = null;
//    }
//};

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopOrientationService();
    }

    private void stopOrientationService(){
        Intent stopServiceIntent = new Intent(this, OrientationService.class);
        stopService(stopServiceIntent);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                CatchPicture catchPicture = new CatchPicture(MainActivity.this, scannerView.mCamera);
                catchPicture.capture();
                break;

            case R.id.changDirectionOfMeasure:
                directionMeasure++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        if (directionMeasure % 2 == 0) {
                            message.what = GETDISTANCE;
                        } else {
                            message.what = GETHEIGHT;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
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
