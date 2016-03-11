package dk.deranged.shownotifications;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class NotificationEntryAdapter implements ListAdapter {
    private Context context;
    private JSONArray notifications;
    private String[] nonDataKeys = { "collapse_key", "notification" };

    public NotificationEntryAdapter(Context context, JSONArray notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public int getCount() {
        return this.notifications.length();
    }

    public View getView(int position, View view, ViewGroup parent) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.notification_list_item, null);
        }

        TextView dateView = (TextView) view.findViewById(R.id.date);
        TextView descriptionView = (TextView) view.findViewById(R.id.description);

        try {
            JSONObject o = notifications.getJSONObject(notifications.length() - 1 - position);
            String dateString = o.getString("date");
            JSONObject payload = o.getJSONObject("payload");
            boolean hasNotification = payload.has("notification");
            boolean hasData = false;

            Iterator<String> keys = payload.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                Log.d("shownotifications", "Found key " + key);
                boolean isNonDataKey = true;
                for(String nonDataKey : nonDataKeys) {
                    if(nonDataKey.equals(key)) {
                        isNonDataKey = false;
                    }
                }
                if(isNonDataKey) {
                    hasData = true;
                }
            }

            Log.d("shownotifications", "This entry has " + (hasData ? "data" : "no data") + " & " + (hasNotification ? "notif" : "no notif"));

            dateView.setText(dateString);

            String description = "";
            if(hasNotification) {
                description += "notification";
            }
            if(hasData) {
                if(description != "") {
                    description += " + ";
                }
                description += "data";
            }
            if(description == "") {
                description = "-";
            }

            descriptionView.setText(description);
        }
        catch(JSONException e) {
            Log.e("shownotifications", "Failed to read json object for this entry " + position + ": " + e);
            dateView.setText("-");
            descriptionView.setText("<error loading>");
            return view;
        }

        return view;
    }

    public JSONObject getItem(int position) {
        try {
            return notifications.getJSONObject(notifications.length() - 1 - position);
        }
        catch(JSONException e) {
            Log.e("shownotifications", "Failed to get item " + position + " from notifications: " + notifications);
            return new JSONObject();
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isEmpty() {
        return notifications.length() == 0;
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public void registerDataSetObserver(DataSetObserver o) {

    }

    public void unregisterDataSetObserver(DataSetObserver o) {

    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }
}
