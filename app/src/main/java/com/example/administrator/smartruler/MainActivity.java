package com.example.administrator.smartruler;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.smartruler.aboutCamera.ScannerView;
import com.example.administrator.smartruler.navigationItems.VideoActivity;
import com.example.administrator.smartruler.sensor.OrientationDetector;
import com.example.administrator.smartruler.sensor.OrientationService;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.example.administrator.smartruler.R.layout.prompts;
import static com.example.administrator.smartruler.R.layout.shareview;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private static final String TAG = "MainActivity";
    public static final int GETDISTANCE = 1;
    public static final int GETHEIGHT = 0;
    public static int changeDirection = 1;
//.........................................................
    private TextView HH;
    private TextView hh;
    private TextView H_Plus_h;

    public static double HHH;


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartRuler/";

    private final String APP_ID = "wxda5cdc6f805d5225";

    private IWXAPI wxApi;

    //........................................................
    private ScannerView scannerView;
    private OrientationService.OrientationBinder mBinder;
    private Thread thread;
    private TextView measurement_text;
    private TextView orientation_text;
    private Button changeOrientation_btn;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case GETDISTANCE:
                    float distance = msg.getData().getFloat("distance");
                    if(distance < 0){
                        measurement_text.setText(R.string.error);
                    }else {
                        measurement_text.setText(""+ distance);
                    }

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
        orientation_text = (TextView)findViewById(R.id.orientation);
        changeOrientation_btn = (Button)findViewById(R.id.changeOrientation);
        changeOrientation_btn.setOnClickListener(this);
///........................................................................................

        hh=(TextView) findViewById(R.id.h);
        HH=(TextView) findViewById(R.id.H);
        H_Plus_h =(TextView) findViewById(R.id.H_plus_h);


        preferences = getSharedPreferences("prompts", MODE_PRIVATE);
        editor = preferences.edit();
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View textEntryView = factory.inflate(prompts, null);

        String inputPwd1 = preferences.getString("H: ","");
        String inputPwd2 = preferences.getString("h: ","");


        final EditText secondPwd1 = (EditText) textEntryView.findViewById(R.id.edit_H);
        final EditText secondPwd2 = (EditText) textEntryView.findViewById(R.id.edit_h);


        if (inputPwd1 != "") {
            secondPwd1.setText(inputPwd1);
            HH.setText("H: " + Double.valueOf(inputPwd1).doubleValue()*0.01+"m");

        }
        else{
            HH.setText("H: 0.00");
        }

        if (inputPwd2 != "") {
            secondPwd2.setText(inputPwd2);
            hh.setText("h: " + Double.valueOf(inputPwd2).doubleValue()*0.01+"m");

        }
        else{
            hh.setText("h: 1.50");
        }

        if((inputPwd1 != "")&&(inputPwd2 != "")){
//            float Hh = Float.parseFloat(inputPwd1)+Float.parseFloat(inputPwd2);
            double Hh=(Double.valueOf(inputPwd1).doubleValue()+Double.valueOf(inputPwd2).doubleValue())*0.01;
            HHH=Hh;
            H_Plus_h.setText("H+h: "+Hh);
            System.out.println("222222222222222222222222222222222");
            System.out.println(HHH);
            System.out.println("222222222222222222222222222222222");
        }
        else{
            H_Plus_h.setText("H+h: 1.50");
            HHH=1.50;
        }





//............................................................................................
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
                          if(changeDirection == GETDISTANCE){
                              Message msg = Message.obtain();
                              msg.what = GETDISTANCE;
                              Bundle data = new Bundle();
                              data.putFloat("distance", OrientationDetector.resultOfDistance);
                              msg.setData(data);
                              handler.sendMessage(msg);

                          }else if(changeDirection == GETHEIGHT){
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
        changeDirection = 1;
    }

    private void stopOrientationService(){
        Intent stopServiceIntent = new Intent(this, OrientationService.class);
        stopService(stopServiceIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeOrientation:
                changeDirection = (changeDirection + 1) % 2;
                if(changeDirection == GETDISTANCE){
                    orientation_text.setText(R.string.distance);
                }else if(changeDirection == GETHEIGHT){
                    orientation_text.setText(R.string.height);
                }
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
//                CatchPicture catchPicture = new CatchPicture(MainActivity.this, ScannerView.mCamera);
//                catchPicture.capture();

                screenshot();


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

        } else {
            if (id == R.id.nav_manage) {

                final String inputPwd11 = preferences.getString("H: ", "");
                final String inputPwd22 = preferences.getString("h: ", "");
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View textEntryView = factory.inflate(prompts, null);

                final EditText secondPwd1 = (EditText) textEntryView.findViewById(R.id.edit_H);
                final EditText secondPwd2 = (EditText) textEntryView.findViewById(R.id.edit_h);

                if (inputPwd11 != null) {
                    secondPwd1.setText(inputPwd11);
                }

                if (inputPwd22 != null) {
                    secondPwd2.setText(inputPwd22);
                }

                AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Measurement Information")
                        .setView(textEntryView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                String inputPwd1 = secondPwd1.getText().toString();
                                String inputPwd2 = secondPwd2.getText().toString();

                                if (inputPwd1.equals("")||inputPwd2.equals("")) {

                                    HH.setText("H: 0" );
                                    System.out.println("1111111111111111111111111111111");
                                    hh.setText("h: 1.50" );
                                    System.out.println("22222222222222222222222222222222");
                                    double Hh =1.5;
                                    H_Plus_h.setText("H+h: " + Hh);
                                    System.out.println("333333333333333333333333333333333");
                                    HHH = Hh;
                                    OrientationDetector.h = Hh;

                                } else {
                                    editor.putString("H: ", inputPwd1);
                                    editor.putString("h: ", inputPwd2);
                                    editor.commit();
                                    HH.setText("H: " + Double.valueOf(inputPwd1).doubleValue()*0.01+"m");
                                    hh.setText("h: " + Double.valueOf(inputPwd2).doubleValue()*0.01+"m");
                                    if ((inputPwd1 != "") && (inputPwd2 != "")) {
                                        double Hh = (Double.valueOf(inputPwd1).doubleValue() + Double.valueOf(inputPwd2).doubleValue()) * 0.01;
                                        H_Plus_h.setText("H+h: " + Hh);
                                        HHH = Hh;
                                        OrientationDetector.h = Hh;
                                        System.out.println("1111111111111111111111111111111");
                                        System.out.println(HHH);
                                        System.out.println("1111111111111111111111111111111");

                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                System.out.println("-------------->2");

                            }
                        })
                        .create();
                dlg.show();

            } else if (id == R.id.nav_share) {

                wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), "wxda5cdc6f805d5225");
                wxApi.registerApp("wxda5cdc6f805d5225");


                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.show();
                Window win = alertDialog.getWindow(); 	        //设置自定义的对话框布局
                win.setContentView(R.layout.shareview);
                ImageButton friend_btn = (ImageButton)win.findViewById(R.id.sharefiend);
                friend_btn.setOnClickListener(new View.OnClickListener(){
                 @Override
                 public void onClick(View v) {
                     wechatShare(0);// 分享到微信好友
                     alertDialog.dismiss();
                 }
                });
                ImageButton quan_btn = (ImageButton)win.findViewById(R.id.sharefiendquan);
                quan_btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        wechatShare(1);//分享到微信朋友圈
                        alertDialog.dismiss();
                    }
                });



            } else if (id == R.id.nav_send) {


            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void screenshot() {

//        // 获取屏幕
        View dView = getWindow().getDecorView().getRootView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(bmp);
        System.out.println("111111111111111");
//        ivPlay.setImageBitmap(bitmap);
        dView.destroyDrawingCache();
        System.out.println("222222222222222");

        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String fileName = simpleDate.format(now.getTime());


        if (bitmap != null) {

            try {
                File dirs = new File(dir);
                if (!dirs.exists())
                    dirs.mkdir();

                File file = new File(dir + fileName + ".jpg");
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(file);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Save the Picture to:"+dir + fileName + ".jpg", Toast.LENGTH_LONG).show();


        }

    }

    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "Smart Ruler";
        msg.description = "Based on Android";
       BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(
               R.drawable.logon);

       Bitmap thumb = bmpDraw.getBitmap();
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
                : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

}
