package com.cs528.hw3;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ActivityIntentService  extends IntentService {

    protected static final String TAG = ActivityIntentService.class.getSimpleName();

    public ActivityIntentService() {
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
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);

            for (ActivityTransitionEvent event : result.getTransitionEvents()) {

                broadcastActivity(event.getActivityType(),event.getTransitionType());
            }
        }
    }

    private void broadcastActivity(int activity,int transitionType) {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity);
        intent.putExtra("transition", transitionType);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}