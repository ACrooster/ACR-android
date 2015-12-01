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

        // NOTE: This function is only here while we are not in the Play Store
        UpdateTask updateTask = new UpdateTask();
        updateTask.execute("https://www.dropbox.com/s/tue3bcvot2ou5z6/app-debug.apk?raw=1");

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

    public class UpdateTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... sUrl) {
            URLConnection feedUrl;
            try {
                feedUrl = new URL("https://www.dropbox.com/s/pybz6xih5piop68/version?raw=1").openConnection();
                InputStream is = feedUrl.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "");
                }
                is.close();

                PackageInfo packageInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                int curVersionCode = packageInfo.versionCode;
                int newVersionCode = Integer.parseInt(sb.toString());

                Log.w("Update", "Current version: " + curVersionCode + ", Newest version: " + newVersionCode);
                if (newVersionCode > curVersionCode) {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/acrooster.apk";
                    try {
                        URL url = new URL(sUrl[0]);
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        int fileLength = connection.getContentLength();

                        // download the file
                        InputStream input = new BufferedInputStream(url.openStream());
                        OutputStream output = new FileOutputStream(path);

                        byte data[] = new byte[1024];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            publishProgress((int) (total * 100 / fileLength));
                            output.write(data, 0, count);
                        }

                        output.flush();
                        output.close();
                        input.close();

                        return path;
                    } catch (Exception e) {
                        Log.e("YourApp", "Well that didn't work out so well...");
                        Log.e("YourApp", e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.e("Get Url", "Error in downloading: " + e.toString());
            }

            return "";
        }

        // begin the installation by opening the resulting file
        @Override
        protected void onPostExecute(String path) {
            if (!path.equals("")) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                Log.d("Lofting", "About to install new .apk");
                startActivity(i);
            }
        }
    }
}
