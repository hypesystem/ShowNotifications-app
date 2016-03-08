package dk.deranged.shownotifications;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;


public class ListActivity extends ActionBarActivity {
    //TODO: In onCreate and onResume, ensure receiver is registered

    private String _regId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        final ListView listView = new ListView(this);
        container.addView(listView);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        refreshNotificationList(sharedPreferences, listView);

        Log.d("shownotifications", "starting reg id setup");

        final Intent registerServiceIntent = new Intent(this, RegistrationIntentService.class);

        //Listen for reg id registration response
        BroadcastReceiver registrationDoneNoticeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("shownotifications", "did receive registration id ready notification");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String registrationId = sharedPreferences.getString("dk.deranged.shownotifications.this_app_registration_id", null);
                if(registrationId == null) {
                    Log.d("shownotifications", "no token");
                    startService(registerServiceIntent);
                    return;
                }
                Log.d("shownotifications", "token good " + registrationId);
                _regId = registrationId;
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(registrationDoneNoticeReceiver, new IntentFilter("dk.deranged.shownotifications.registration_finished"));

        Log.d("shownotifications", "starting registration intent service");

        //Register reg id
        startService(registerServiceIntent);

        //Listen for new notification
        Log.d("shownotifications", "starting new notification registration setup");

        BroadcastReceiver newNotificationNoticeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("shownotifications", "did receive new notification notice");
                refreshNotificationList(sharedPreferences, listView);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationNoticeReceiver, new IntentFilter("dk.deranged.shownotifications.new_notification_registered"));
    }

    private void refreshNotificationList(SharedPreferences sharedPreferences, ListView listView) {
        try {
            JSONArray allNotifications = new JSONArray(sharedPreferences.getString("dk.deranged.shownotifications.all_notifications", "[]"));
            NotificationEntryAdapter adapter = new NotificationEntryAdapter(this, allNotifications);
            listView.setAdapter(adapter);
        }
        catch(JSONException e) {
            Log.e("shownotifications", "Failed to load saved notifications" + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_show_api_key) {
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            final String apiKey = getString(R.string.api_key);
            final Toast copiedToast = Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(apiKey);
            builder.setTitle("API Key");
            builder.setPositiveButton("Copy to clipboard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipData clipData = ClipData.newPlainText("API Key for ShowNotifications App", apiKey);
                    clipboard.setPrimaryClip(clipData);
                    copiedToast.show();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        if(id == R.id.action_show_registration_id) {
            if(_regId == null) {
                Toast.makeText(this, "Currently no reg id.", Toast.LENGTH_LONG).show();
                return true;
            }

            final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            final String registrationId = _regId;
            final Toast copiedToast = Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(registrationId);
            builder.setTitle("Registration ID");
            builder.setPositiveButton("Copy to clipboard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipData clipData = ClipData.newPlainText("API Key for ShowNotifications App", registrationId);
                    clipboard.setPrimaryClip(clipData);
                    copiedToast.show();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
