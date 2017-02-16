package com.example.administrator.smartruler.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Administrator on 2016/9/16.
 */
public class OrientationDetector implements SensorEventListener {
    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];
    private float[] R = new float[9];
    private static float[] values = new float[3];

    private static double angleOfX;  //pitch
    private static double angleOfY;  //roll
    private static double angleOfZ;
    public static float resultOfDistance;
    public static float resultOfHeight;
    private double temp_D;
    private double temp_H;


    public OrientationDetector(Context context){
        super();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                accelerometerValues = sensorEvent.values.clone();
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                magneticValues = sensorEvent.values.clone();
            }
        SensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
        SensorManager.getOrientation(R,values);

        angleOfX = values[1];
        angleOfY = values[2];
        angleOfZ = values[0];

        getDistance();
        getHeight();
    }

    private void getDistance(){
        double a = Math.cos(angleOfY);
        double b =  Math.cos(angleOfX);
        double result= Math.asin( a*b);
        temp_D = (1.5/Math.tan(result));
        resultOfDistance = (float)(Math.round(temp_D * 10) / 10.0);//保留一位小数


    }

    private void getHeight(){
//        double a = Math.cos(angleOfY);
//        double b =  Math.cos(angleOfX);
//        float result= (float)Math.asin( a*b);
        resultOfHeight = (float) 1.88;//test

    }
}
