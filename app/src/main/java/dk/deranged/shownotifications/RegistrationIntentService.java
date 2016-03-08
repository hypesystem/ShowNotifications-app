package dk.deranged.shownotifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
    public RegistrationIntentService() {
        super("RegistrationIntentService");
        Log.d("shownotifications", "Intent service done constructed");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d("shownotifications", "intent service handling intent");

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d("shownotifications", "Madet a token " + token);
            sharedPreferences.edit().putString("dk.deranged.shownotifications.this_app_registration_id", token).apply();
            Log.d("shownotifications", "intent service did everything");
        }
        catch(IOException e) {
            Log.e("shownotifications", "Failed to register reg id " + e);
        }
        finally {
            Log.d("shownotifications", "intent service signalling");

            Intent registrationComplete = new Intent("dk.deranged.shownotifications.registration_finished");
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }
}
