package dk.deranged.shownotifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("shownotifications", "Message received.");
        Log.d("shownotifications", "From: " + from);
        Log.d("shownotifications", "Data: " + data.toString());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            //TODO: Replace saving as JSON with saving in database

            JSONObject thisNotification = new JSONObject();
            JSONObject payload = readBundleToJson(data);
            thisNotification.put("payload", payload);
            thisNotification.put("date", new Date().toString());

            JSONArray allNotifications = new JSONArray(sharedPreferences.getString("dk.deranged.shownotifications.all_notifications", "[]"));

            allNotifications.put(thisNotification);

            sharedPreferences.edit().putString("dk.deranged.shownotifications.all_notifications", allNotifications.toString()).apply();

            Intent registrationComplete = new Intent("dk.deranged.shownotifications.new_notification_registered");
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
        catch(JSONException e) {
            Log.e("shownotifications", "Failed to log new notification " + from + ": " + data);
        }
    }

    private JSONObject readBundleToJson(Bundle data) throws JSONException {
        //TODO: Support other data types. Like numbers, booleans, arrays.
        JSONObject thisNotification = new JSONObject();
        for(String key : data.keySet()) {
            Bundle innerBundle = data.getBundle(key);
            if(innerBundle != null) {
                thisNotification.put(key, readBundleToJson(innerBundle));
            }
            else {
                thisNotification.put(key, data.getString(key));
            }
        }
        return thisNotification;
    }
}
