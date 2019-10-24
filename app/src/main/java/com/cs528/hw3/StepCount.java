package com.cs528.hw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class StepCount implements SensorEventListener {
    private Context context;
    private static StepCount instance = null;
    private SensorManager sensorManager;



    public static StepCount getInstance(Context context) {
        if (instance == null)
            instance = new StepCount(context);
        return instance;
    }

    public StepCount(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startSensor(){
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null){
            sensorManager.registerListener(this,countSensor,SensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(context,"Step Cont sensor not available!",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float step = sensorEvent.values[0];
        MainActivity.updateStep(step);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
