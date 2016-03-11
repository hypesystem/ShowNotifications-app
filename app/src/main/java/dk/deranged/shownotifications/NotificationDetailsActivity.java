package dk.deranged.shownotifications;

import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class NotificationDetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);

        LinearLayout container = (LinearLayout) findViewById(R.id.notification_detail_container);

        try {
            JSONObject data = new JSONObject(getIntent().getExtras().getString("dk.deranged.shownotifications.notification_detail"));
            String date = data.getString("date");

            TextView dateView = new TextView(this);
            dateView.setText(date);
            dateView.setTextSize(18);
            dateView.setTypeface(null, Typeface.BOLD);
            dateView.setPadding(0, 0, 0, 20);
            container.addView(dateView);

            JSONObject payload = data.getJSONObject("payload");
            setUpViewFromJson(container, payload);
        }
        catch(JSONException e) {
            Log.e("pooetry", "Failed to render details view " + e);
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpViewFromJson(LinearLayout container, JSONObject detail) throws JSONException {
        //TODO: Support other data types. Like numbers, booleans, arrays.

        Iterator<String> keys = detail.keys();
        while(keys.hasNext()) {
            String key = keys.next();

            TextView labelView = new TextView(this);
            labelView.setText(key);
            labelView.setTextSize(12);
            labelView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
            container.addView(labelView);

            JSONObject nextObj = detail.optJSONObject(key);
            if(nextObj != null) {
                LinearLayout subContainer = new LinearLayout(this);
                subContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                subContainer.setOrientation(LinearLayout.VERTICAL);
                subContainer.setPadding(40, 0, 0, 0);
                container.addView(subContainer);
                setUpViewFromJson(subContainer, nextObj);
                continue;
            }

            String nextStr = detail.getString(key);

            TextView valueView = new TextView(this);
            valueView.setText(nextStr);
            valueView.setPadding(0, 0, 0, 15);
            container.addView(valueView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
