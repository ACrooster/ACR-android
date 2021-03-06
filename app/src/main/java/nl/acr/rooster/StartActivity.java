package nl.acr.rooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import go.framework.Framework;


public class StartActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "ACRPrefs";
    public static long RELOGIN_DATE = 1454527909;
    public static int TUTORIAL_VERSION = 1;
    public static String SCHOOL = "amstelveencollege";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // All this activity does is check if a token already exists
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // Set the school name
        Framework.SetSchool(SCHOOL);

        if (settings.contains("token") && settings.getLong("login_date", 0) > RELOGIN_DATE) {
            Framework.SetToken(settings.getString("token", ""));
            Intent goToSchedule = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToSchedule);
        } else {
            // If the not already signed in show login
            Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goToLogin);
        }
        finish();
    }
}
