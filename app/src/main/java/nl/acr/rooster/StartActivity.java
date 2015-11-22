package nl.acr.rooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class StartActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "ACRPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // All this activity does is check if a token already exists
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // NOTE: This should be removed later on, only for testing
        boolean testing = false;

        if (settings.contains("token") && !testing) {
            Intent goToSchedule = new Intent(getApplicationContext(), ScheduleActivity.class);
            startActivity(goToSchedule);
        } else {
            // If the not already signed in show login
            Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goToLogin);
        }
    }
}
