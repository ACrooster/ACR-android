package nl.acr.rooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import go.framework.Framework;


public class StartActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "ACRPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // All this activity does is check if a token already exists
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // NOTE: This should be removed later on, only for testing
        boolean testing = false;

        // Set the school name
        Framework.SetSchool("amstelveencollege");

        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        Framework.SetTimeDiff(TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS));

        if (settings.contains("token") && !testing) {
            Framework.SetToken(settings.getString("token", ""));
            Intent goToSchedule = new Intent(getApplicationContext(), ScheduleActivity.class);
            startActivity(goToSchedule);
        } else {
            // If the not already signed in show login
            Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goToLogin);
        }
    }
}
