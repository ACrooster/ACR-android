package nl.acr.rooster;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.textservice.SpellCheckerInfo;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import go.framework.Framework;

public class UpdateSchedule extends AsyncTask<Boolean, Void, Integer> {

    static private final int[] datePosition = new int[5];
    public static boolean scroll = true;
    private static boolean landscape = false;
    public static int dayOfWeek = 0;
    public static int timeOfLastUpdate = 0;

    public static TextView datePickerWeek;
    public static TextView datePickerStudent;

    List<ClassInfo> tempList;
    List<ClassInfo> landscapeList;

    @Override
    protected void onPreExecute() {

        MainActivity.refreshSchedule.setRefreshing(true);
    }

    final Comparator<ClassInfo> classSorter = new Comparator<ClassInfo>() {
        @Override
        public int compare(ClassInfo lhs, ClassInfo rhs) {
            return (int) (lhs.timeStartUnix - rhs.timeStartUnix);
        }
    };

    @Override
    protected Integer doInBackground(Boolean... params) {
        Framework.RequestScheduleData(ScheduleFragment.weekUnix, ScheduleFragment.user);
        tempList = new ArrayList<>();
        int error = (int) Framework.GetError();
        if (error == Framework.ERROR_NONE) {

            int classCount = (int) Framework.GetClassCount();
            for (int i = 0; i < classCount; i++) {
                if (Framework.IsClassValid(i)) {
                    tempList.add(new ClassInfo(Framework.GetClassName(i), Framework.GetClassTeacher(i), Framework.GetClassStartTime(i), Framework.GetClassEndTime(i), Framework.GetClassRoom(i), Framework.GetClassStatus(i), Framework.GetClassStartUnix(i), Framework.GetClassEndUnix(i), Framework.GetClassTimeSlot(i)));
                }
            }

            Collections.sort(tempList, classSorter);

            boolean endOfDay = false;

            // TODO: I am pretty sure we are not using unixEndTime for free hours
            int size = tempList.size();
            for (int i = 0; i < size; i++) {
                int free;
                if (i == 0 || endOfDay) {
                    free = tempList.get(i).timeSlot - 1;

                    for (int j = 0; j < free; j++) {
                        tempList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, tempList.get(i).timeStartUnix - j - 1, tempList.get(i).timeStartUnix - j - 1, tempList.get(i).timeSlot - j - 1));
                    }
                }

                if (i + 1 < size) {
                    free = tempList.get(i + 1).timeSlot - tempList.get(i).timeSlot - 1;
                    endOfDay = (tempList.get(i + 1).timeStartUnix - tempList.get(i).timeStartUnix) > 10 * 3600;
                } else {
                    free = 0;
                    endOfDay = false;
                }

                for (int j = 0; j < free; j++) {
                    tempList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, tempList.get(i).timeStartUnix + j + 1, tempList.get(i).timeStartUnix + j + 1, tempList.get(i).timeSlot + j + 1));
                }
            }

            ClassInfo[] dateInfo = new ClassInfo[5];
            for (int i = 0; i < 5; i++) {
//                if (params[0]) {
                    dateInfo[i] = new ClassInfo(ScheduleFragment.getDay(i), Framework.GetDayUnix(i));
//                } else {
//                    dateInfo[i] = new ClassInfo(ScheduleFragment.getDay(i) + " " + Framework.GetDayNumber(i) + " " + ScheduleFragment.getMonth((int) Framework.GetDayMonth(i)), Framework.GetDayUnix(i));
//                }
                tempList.add(dateInfo[i]);
            }

            Collections.sort(tempList, classSorter);
            for (int i = 0; i < 5; i++) {
                datePosition[i] = tempList.indexOf(dateInfo[i]);
            }

            landscapeList = new ArrayList<>();

//            if (params[0]) {
                size = tempList.size();
                int longest = 0;

                for (int i = 0; i < 5; i++) {
                    if (i != 4) {
                        longest = Math.max(datePosition[i + 1] - datePosition[i], longest);
                    } else {
                        longest = Math.max(size - datePosition[i], longest);
                    }
                }

                for (int i = 0; i < longest * 5; i++) {
                    landscapeList.add(new ClassInfo());
                }

                int day = 0;
                int offset = 0;
                for (int i = 0; i < size; i++) {
                    landscapeList.set((i - offset) * 5 + day, tempList.get(i));
                    if (day != 4) {
                        if (i + 1 == datePosition[day + 1]) {
                            day++;
                            offset = i + 1;
                        }
                    }
                }
//            }

            timeOfLastUpdate = (int) (System.currentTimeMillis() / 1000);

            ScheduleFragment.classArrayList.clear();
            if (params[0]) {

                ScheduleFragment.classArrayList.addAll(landscapeList);
            } else {
                ScheduleFragment.classArrayList.addAll(tempList);
            }
        }

        landscape = params[0];

        return error;
    }

    @Override
    protected void onPostExecute(final Integer error) {

        if (error == Framework.ERROR_NONE || (error == Framework.ERROR_CONNECTION && ScheduleFragment.user.equals(Framework.MY_SCHEDULE))) {

            if (error == Framework.ERROR_NONE && ScheduleFragment.user.equals(Framework.MY_SCHEDULE)) {

                Gson gson = new Gson();
                String jsonScheduleLand = gson.toJson(landscapeList);
                String jsonSchedulePort = gson.toJson(tempList);

                SharedPreferences.Editor editor = MainActivity.settings.edit();

                editor.putString("classLand", jsonScheduleLand);
                editor.putString("classPort", jsonSchedulePort);
                editor.putString("classWeek", String.valueOf(Framework.GetWeek()));
                editor.apply();

            } else if (error == Framework.ERROR_CONNECTION) {

                Framework.SetSchool(StartActivity.SCHOOL);
                Framework.SetToken(MainActivity.settings.getString("token", ""));
                Calendar mCalendar = new GregorianCalendar();
                TimeZone mTimeZone = mCalendar.getTimeZone();
                int mGMTOffset = mTimeZone.getRawOffset();
                Framework.SetTimeDiff(TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS));

                if (!MainActivity.settings.getString("classLand", "").equals("") && !MainActivity.settings.getString("classPort", "").equals("")) {

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<ClassInfo>>() {
                    }.getType();

                    ScheduleFragment.classArrayList.clear();
                    List<ClassInfo> classList;
                    if (landscape) {

                        classList = gson.fromJson(MainActivity.settings.getString("classLand", ""), type);
                    } else {

                        classList = gson.fromJson(MainActivity.settings.getString("classPort", ""), type);
                    }
                    ScheduleFragment.classArrayList.addAll(classList);

                    ScheduleFragment.ca.notifyDataSetChanged();
                    if (datePickerWeek != null && datePickerStudent != null) {
                        datePickerWeek.setText(MainActivity.resources.getString(R.string.week) + " " + MainActivity.settings.getString("classWeek", "0"));
                        datePickerStudent.setText(MainActivity.resources.getString(R.string.my_schedule));
                    }
                }

                Snackbar.make(MainActivity.drawer, MainActivity.resources.getString(R.string.offline), Snackbar.LENGTH_INDEFINITE)
                        .setAction(MainActivity.resources.getString(R.string.retry), new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ScheduleFragment.createList();
                            }
                        })
                        .show();
            }

            if (error == Framework.ERROR_NONE) {

                ScheduleFragment.ca.notifyDataSetChanged();
                if (datePickerWeek != null && datePickerStudent != null) {
                    // TODO: This should go in a function
                    datePickerWeek.setText(MainActivity.resources.getString(R.string.week) + " " + Framework.GetWeek());
                    // TODO: Add system that checks whose schedule you are looking at
                    String user = Framework.GetUser();
                    datePickerStudent.setText(user.equals(Framework.MY_SCHEDULE) ? MainActivity.resources.getString(R.string.my_schedule) : user);

                    int position = datePosition[dayOfWeek];
                    if (scroll && ScheduleFragment.classArrayList.size() > position) {
                        Log.w("Scroll", "Scrolling to: " + position);
                        ScheduleFragment.classList.smoothScrollToPosition(position);
                        scroll = false;
                    }
                }
            }
        } else if (error == Framework.ERROR_RIGHTS) {
            ScheduleFragment.user = "~me";
            Snackbar.make(MainActivity.drawer, MainActivity.resources.getString(R.string.rights), Snackbar.LENGTH_SHORT)
                    .show();
        }

        MainActivity.progressBar.setVisibility(View.GONE);
        ScheduleFragment.classList.setVisibility(View.VISIBLE);
        MainActivity.refreshSchedule.setRefreshing(false);
    }
}
