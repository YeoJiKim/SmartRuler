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
}
