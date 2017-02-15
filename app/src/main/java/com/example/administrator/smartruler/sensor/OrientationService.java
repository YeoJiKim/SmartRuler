package com.example.administrator.smartruler.sensor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by Administrator on 2016/9/16.
 */
public class OrientationService extends Service {
    public static Boolean FLAG = false;

    private SensorManager  sensorManager;
    private OrientationDetector detector;

    public static final int GETDISTANCE_MSG = 1;
    public static final int GETHEIGHT_MSG = 2;


    private final IBinder mBinder = new OrientationBinder();
    public class OrientationBinder extends Binder {
        public OrientationService getService(){
            return OrientationService.this;
        }
    }

    public OrientationService(){
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder ;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        FLAG = true;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        detector = new OrientationDetector(this);
        registerSensor();
    }

    public void registerSensor() {
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(detector,magneticSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(detector,accelerometerSensor,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy(){
        FLAG = false;
        super.onDestroy();
        if(sensorManager != null){
            sensorManager.unregisterListener(detector);
        }
    }

    public interface ICallback{
        void distanceChanged(float distance);
        void heightChanged(float height);
    }

    private ICallback mCallback;

    public void registerCallback(ICallback mCallback) {
        this.mCallback = mCallback;
    }

    public void measurementChanged(final int msg){
        new Thread(new Runnable(){
            @Override
            public void run(){
                if(msg == GETDISTANCE_MSG) {
                    mCallback.distanceChanged(OrientationDetector.resultOfDistance);
                }
                if(msg == GETHEIGHT_MSG){
                    mCallback.heightChanged(OrientationDetector.resultOfHeight);
                }
            }
        }).start();
    }


}
