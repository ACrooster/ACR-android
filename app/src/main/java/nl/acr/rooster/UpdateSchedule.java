package nl.acr.rooster;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import go.framework.Framework;

public class UpdateSchedule extends AsyncTask<Boolean, Void, Integer> {

    static private final int[] datePosition = new int[5];
    public static boolean scroll = true;
    public static int dayOfWeek = 0;
    public static int timeOfLastUpdate = 0;

    public static TextView datePickerWeek;
    public static TextView datePickerStudent;
    public static MaterialDialog.Builder rights;

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
        List<ClassInfo> tempList = new ArrayList<>();
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
                if (params[0]) {
                    dateInfo[i] = new ClassInfo(ScheduleFragment.getDay(i), Framework.GetDayUnix(i));
                } else {
                    dateInfo[i] = new ClassInfo(ScheduleFragment.getDay(i) + " " + Framework.GetDayNumber(i) + " " + ScheduleFragment.getMonth((int) Framework.GetDayMonth(i)), Framework.GetDayUnix(i));
                }
                tempList.add(dateInfo[i]);
            }

            // Nu balk
            tempList.add(new ClassInfo(System.currentTimeMillis() / 1000));

            Collections.sort(tempList, classSorter);
            for (int i = 0; i < 5; i++) {
                datePosition[i] = tempList.indexOf(dateInfo[i]);
            }

            List<ClassInfo> landscapeList = new ArrayList<>();

            if (params[0]) {
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
            }

            timeOfLastUpdate = (int) (System.currentTimeMillis() / 1000);

            ScheduleFragment.classArrayList.clear();
            if (params[0]) {
                ScheduleFragment.classArrayList.addAll(landscapeList);
            } else {
                ScheduleFragment.classArrayList.addAll(tempList);
            }
        }
        return error;
    }

    @Override
    protected void onPostExecute(final Integer error) {

        if (error == Framework.ERROR_NONE) {
            ScheduleFragment.ca.notifyDataSetChanged();
            if (datePickerWeek != null && datePickerStudent != null) {
                // TODO: This should go in a function
                // TODO: This should use strings.xml
                datePickerWeek.setText(MainActivity.resources.getString(R.string.week) + " " + Framework.GetWeek());
                // TODO: Add system that checks whose schedule you are looking at
                String user = Framework.GetUser();
                datePickerStudent.setText(user.equals("~me") ? MainActivity.resources.getString(R.string.my_schedule) : user);

                int position = datePosition[dayOfWeek];
                if (scroll && ScheduleFragment.classArrayList.size() > position) {
                    Log.w("Scroll", "Scrolling to: " + position);
                    ScheduleFragment.classList.smoothScrollToPosition(position);
                    scroll = false;
                }
            }
        } else if (error == Framework.ERROR_RIGHTS) {
            rights.show();
            ScheduleFragment.user = "~me";
        } else {
            // TODO: Get these strings from strings.xml
            Snackbar.make(MainActivity.drawer, "Je bent op dit moment offline", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Opnieuw proberen", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ScheduleFragment.createList();
                        }
                    })
                    .show();
        }

        MainActivity.progressBar.setVisibility(View.GONE);
        ScheduleFragment.classList.setVisibility(View.VISIBLE);
        MainActivity.refreshSchedule.setRefreshing(false);
    }
}
