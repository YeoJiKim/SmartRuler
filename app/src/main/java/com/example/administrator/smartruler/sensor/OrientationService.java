package com.example.administrator.smartruler.sensor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;

/**
 * Created by Administrator on 2016/9/16.
 */
public class OrientationService extends Service {
    private SensorManager  sensorManager;
    private OrientationDetector detector;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        detector = new OrientationDetector(this);

        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(detector,magneticSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(detector,accelerometerSensor,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(sensorManager != null){
            sensorManager.unregisterListener(detector);
        }
    }
}
