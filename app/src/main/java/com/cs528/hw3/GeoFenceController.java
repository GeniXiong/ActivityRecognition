package com.cs528.hw3;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class GeoFenceController {
    private Context context;
    private static GeoFenceController instance = null;
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<Geofence>();

    private static final float GEOFENCE_RADIUS = 100.0f; // in meters

    private LatLng GORDON = new LatLng(42.274228, -71.806353);
    private LatLng FULLER = new LatLng(42.275122, -71.806497);

    public static GeoFenceController getInstance(Context context) {
        if (instance == null)
            instance = new GeoFenceController(context);
        return instance;
    }

    public GeoFenceController(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);

        //build two geo fence
        geofenceList.add(createGeofence(Constants.GEOFENCE_ID_GORDON,GORDON,GEOFENCE_RADIUS));
        geofenceList.add(createGeofence(Constants.GEOFENCE_ID_Fuller,FULLER,GEOFENCE_RADIUS));


    }

    public void startGeo(PendingIntent pendingIntent){
        Log.d("Geo", "startGeofence request");
        geofencingClient.addGeofences(getGeofencingRequest(), pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Toast.makeText(context,
                                "Geo Fence Enabled",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.w("Error", "Geo fence Not enabled");
                        Toast.makeText(context,
                                "Geo Fence Not enabled",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    // Create a Geofence
    private Geofence createGeofence(String geoID, LatLng latLng, float radius ) {
        Log.d("Geo", "createGeofence");
        return new Geofence.Builder()
                .setRequestId(geoID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(-1)
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_DWELL )
                .setLoiteringDelay (15000)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        Log.d("Geo", "createGeofence request");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

}
