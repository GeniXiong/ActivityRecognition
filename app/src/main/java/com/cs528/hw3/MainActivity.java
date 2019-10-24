package com.cs528.hw3;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs528.hw3.database.Action;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private BroadcastReceiver broadcastReceiver;
    private TextView userActivity;
    private TextView gordonCount;
    private TextView fullerCount;

    private UserActionRecognation userAction;
    private ImageView userActivity_img;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private  Resources res;
    private ActionBL actionBL;
    private  MediaPlayer mediaPlayer;
    private static TextView stepView;
    private static Context context;
    private StepCount stepCount;

    private static int fullerGeo = 0;
    private static int gordonGeo = 0;

    private GeoFenceController geoFenceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        actionBL = new ActionBL(this);
        stepCount = StepCount.getInstance(this);

        userActivity = findViewById(R.id.currentAction);
        stepView = findViewById(R.id.stepCount);
        fullerCount = findViewById(R.id.visitFuller);
        gordonCount = findViewById(R.id.visitLibrary);



        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beat_02);

        userActivity_img = findViewById(R.id.currentActionImage);

        res = getResources();
        userActivity_img.setImageDrawable(res.getDrawable(R.drawable.still));

        userActivity.setText(getText(R.string.activity_status)+" Still");
        stepView.setText(getText(R.string.step_count)+" 0");
        fullerCount.setText(getText(R.string.fuller_geo)+" 0");
        gordonCount.setText(getText(R.string.library_geo)+" 0");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.current_location);
        mapFragment.getMapAsync(this);

        userAction = UserActionRecognation.getInstance(this);

        geoFenceController = GeoFenceController.getInstance(this);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("this","onReceive called "+intent.getAction());
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    Log.e("this","onReceive activity called");
                    int type = intent.getIntExtra("type", -1);
                    int tran = intent.getIntExtra("transition",-1);
                    Action action = actionBL.getAction();
                    if (action == null) {
                        Log.e("this","Action is null");
                        Date date = new Date();
                        action = new Action(date.getTime(),type);
                        actionBL.addAction(action);
                    }
                    else {
                        if (action.getAction() != type) {
                            Date date = new Date();
                            Action newAct = new Action(date.getTime(), type);
                            actionBL.addAction(newAct);
                            Toast.makeText(context,
                                    "This activity as last " + actionBL.calculateDuringTime(date, new Date(action.getTime())),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    if(tran == ActivityTransition.ACTIVITY_TRANSITION_EXIT )
                        handleActivity(type);
                }
                else if (intent.getAction().equals(Constants.BROADCAST_DETECTED_GEOFENCE)){
                    String geofence = intent.getStringExtra("geoID");
                    int transition = intent.getIntExtra("transition",-1);
                    Log.e("this","onReceive Geo Fence called"+transition);
                    if(geofence.equalsIgnoreCase(Constants.GEOFENCE_ID_Fuller)&&transition == Geofence.GEOFENCE_TRANSITION_DWELL){
                        Toast.makeText(context,
                                "You have been inside the Fuller Lab Geofence for 15 seconds, incrementing counter" ,
                                Toast.LENGTH_LONG)
                                .show();
                        fullerGeo++;
                        fullerCount.setText(getText(R.string.fuller_geo)+" "+fullerGeo);
                    }
                    else if(geofence.equalsIgnoreCase(Constants.GEOFENCE_ID_GORDON)&&transition == Geofence.GEOFENCE_TRANSITION_DWELL){
                        Toast.makeText(context,
                                "You have been inside the Gordon Library Geofence for 15 seconds, incrementing counter" ,
                                Toast.LENGTH_LONG)
                                .show();
                        gordonGeo++;
                        gordonCount.setText(getText(R.string.library_geo)+" "+gordonGeo);

                    }
                    else{
                        Log.e("this","Unexpected Geo Fence");

                    }

                }
                else{
                    Log.e("this","Unexpected intent in onReceive");
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("this","onResume called");

        stepCount.startSensor();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));

        PendingIntent pendingIntent = getActivityDetectionPendingIntent();
        userAction.start(pendingIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_GEOFENCE));

        PendingIntent pendingIntentGeo = getGeoFencePendingIntent();
        geoFenceController.startGeo(pendingIntentGeo);

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

    private PendingIntent getGeoFencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void handleActivity(int type) {

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                userActivity.setText(getString(R.string.activity_status)+" In Vehicle");
                userActivity_img.setImageDrawable(res.getDrawable(R.drawable.in_vehicle));

                break;
            }
            case DetectedActivity.RUNNING: {
                userActivity.setText(getString(R.string.activity_status)+" Running");
                userActivity_img.setImageDrawable(res.getDrawable(R.drawable.running));
                mediaPlayer.start();
                break;
            }
            case DetectedActivity.STILL: {
                userActivity.setText(getString(R.string.activity_status)+" Still");
                userActivity_img.setImageDrawable(res.getDrawable(R.drawable.still));

                break;
            }
            case DetectedActivity.WALKING: {
                userActivity.setText(getString(R.string.activity_status)+" Walking");
                userActivity_img.setImageDrawable(res.getDrawable(R.drawable.walking));
                mediaPlayer.start();
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Log.e("Unknow", "User activity: Unknown");
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }

        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(15);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };

    public static void updateStep(float count){
        stepView.setText(context.getString(R.string.step_count)+" "+count);

    }

    public ActionBL getActionBL(){
        return actionBL;
    }


}
