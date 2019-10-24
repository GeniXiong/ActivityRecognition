package com.cs528.hw3;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeoFenceIntentService extends IntentService {
    private static final String TAG = GeoFenceIntentService.class.getSimpleName();
    public GeoFenceIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"on handleIntent called");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {

            Log.e(TAG, "Error in Geo Fence");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            for(Geofence geofence: triggeringGeofences){
                broadcastActivity(geofence.getRequestId(),geofenceTransition);
                Log.i(TAG, "GeoFence Hit"+geofence.getRequestId());
            }


        } else {
            // Log the error.
            Log.e(TAG, "Not working");
        }
    }


    private void broadcastActivity(String geoID,int transitionType) {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_GEOFENCE);
        intent.putExtra("geoID", geoID);
        intent.putExtra("transition", transitionType);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
