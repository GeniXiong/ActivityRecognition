package com.cs528.hw3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends Activity {

    private BroadcastReceiver broadcastReceiver;
    private TextView userActivity;
    private UserActionRecognation userAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userActivity = findViewById(R.id.currentAction);

        userAction = UserActionRecognation.getInstance(this);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("this","onReceive called");
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    handleActivity(type);
                }
            }
        };

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("this","onResume called");

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
        PendingIntent pendingIntent = getActivityDetectionPendingIntent();
        userAction.start(pendingIntent);


    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void handleActivity(int type) {

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                userActivity.setText(getString(R.string.activity_status)+"In Vehicle");
                break;
            }
            case DetectedActivity.RUNNING: {
                userActivity.setText(getString(R.string.activity_status)+"Running");
                break;
            }
            case DetectedActivity.STILL: {
                userActivity.setText(getString(R.string.activity_status)+"Still");
                break;
            }
            case DetectedActivity.WALKING: {
                userActivity.setText(getString(R.string.activity_status)+"Walking");
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Log.e("Unknow", "User activity: Unknown");
                break;
            }
        }
    }
}
