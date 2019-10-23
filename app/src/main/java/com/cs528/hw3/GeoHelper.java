package com.cs528.hw3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import static java.security.AccessController.checkPermission;


public class GeoHelper {
    private final static int GEOFENCE_RADIUS_IN_METERS = 100;
    private final static long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 60 * 1000;
    double FLongitude = 42.275177;
    double FLatitude = -71.805926;
    double GLongitude = 42.274228;
    double GLatitude = -71.806353;

    private GeofencingClient geofencingClient;
    private List geofenceList;
    private PendingIntent geoFencePendingIntent;
    private GeofencingRequest request;

    public void GeoConstructor(Context context){
        geofencingClient = LocationServices.getGeofencingClient(context);
        geofenceList = new ArrayList();
        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("Fuller Labs")

                .setCircularRegion(
                        FLongitude,
                        FLatitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(15)
                .build());

        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("Gordon Library")

                .setCircularRegion(
                        GLongitude,
                        GLatitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(15)
                .build());
        request = createGeofencingRequest();
    }

    private GeofencingRequest createGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences( geofenceList);
        return builder.build();
    }


    private final int GEOFENCE_REQ_CODE = 0;
    public PendingIntent createGeofencePendingIntent(Context context) {
        Log.d("Create Goefence", "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( context, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                context, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    public void addGeofence(Context context) {
        Log.d("add", "addGeofence");
        geofencingClient.addGeofences(request, createGeofencePendingIntent(context))
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                    }
                })
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                    }
                });
    }
    public void StopGeoFence(Context context){
        geofencingClient.removeGeofences(createGeofencePendingIntent(context))
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });
    }
}
