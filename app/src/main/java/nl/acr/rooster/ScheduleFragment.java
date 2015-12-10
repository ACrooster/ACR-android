package nl.acr.rooster;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import go.framework.Framework;

public class ScheduleFragment extends Fragment {

    static private final String[] days = new String[5];
    static private final String[] months = new String[12];

    public static int weekUnix = 0;

    static public String user = Framework.MY_SCHEDULE;

    static protected RecyclerView classList;
    public static final List<ClassInfo> classArrayList = new ArrayList<>();
    public static final ClassListAdapter ca = new ClassListAdapter(classArrayList);

    public static void setWeekUnix(int weekUnix) {

        ScheduleFragment.weekUnix = weekUnix;
        Log.w("Week", String.valueOf(weekUnix));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, containter, false);

        initDayMonthNames();

        classList = (RecyclerView) rootView.findViewById(R.id.classListMon);
        classList.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            classList.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
            classList.setAdapter(ca);
            MainActivity.landscape = false;
        } else {
            classList.setHasFixedSize(true);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
            classList.setLayoutManager(layoutManager);
            classList.setAdapter(ca);
            MainActivity.landscape = true;
        }

        MainActivity.refreshSchedule = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_schedule);
        MainActivity.refreshSchedule.setColorSchemeResources(R.color.colorAccent);
        MainActivity.refreshSchedule.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                createList();
            }
        });

        if (classArrayList.size() == 0) {
            MainActivity.progressBar.setVisibility(View.VISIBLE);
            classList.setVisibility(View.GONE);
        }

        if (weekUnix == 0) {
            setWeekUnix((int) (System.currentTimeMillis() / 1000));
        }

        createList();

        UpdateSchedule.rights = new MaterialDialog.Builder(getActivity())
                .title(R.string.rights)
                .content(R.string.rights_text)
                .positiveText(R.string.ok);

        return rootView;
    }

    public static void createList() {
        // TODO: Add more error handling
        // TODO: Do this in the background

        UpdateSchedule updateSchedule = new UpdateSchedule();
        updateSchedule.execute(MainActivity.landscape);
    }


    private void initDayMonthNames() {
        days[0] = this.getString(R.string.day_mon);
        days[1] = this.getString(R.string.day_tue);
        days[2] = this.getString(R.string.day_wed);
        days[3] = this.getString(R.string.day_thu);
        days[4] = this.getString(R.string.day_fri);

        months[0] = this.getString(R.string.month_jan);
        months[1] = this.getString(R.string.month_feb);
        months[2] = this.getString(R.string.month_mar);
        months[3] = this.getString(R.string.month_apr);
        months[4] = this.getString(R.string.month_may);
        months[5] = this.getString(R.string.month_jun);
        months[6] = this.getString(R.string.month_jul);
        months[7] = this.getString(R.string.month_aug);
        months[8] = this.getString(R.string.month_sep);
        months[9] = this.getString(R.string.month_oct);
        months[10] = this.getString(R.string.month_nov);
        months[11] = this.getString(R.string.month_dec);
    }


    // TODO: Check if there is a built in system get this
    public static String getDay(int index) {

        switch (index) {
            case 0:
                return days[0];
            case 1:
                return days[1];
            case 2:
                return days[2];
            case 3:
                return days[3];
            case 4:
                return days[4];
        }

        return "";
    }

    // TODO: Check if there is a built in system get this
    public static String getMonth(int index) {

        switch (index) {
            case 1:
                return months[0];
            case 2:
                return months[1];
            case 3:
                return months[2];
            case 4:
                return months[3];
            case 5:
                return months[4];
            case 6:
                return months[5];
            case 7:
                return months[6];
            case 8:
                return months[7];
            case 9:
                return months[8];
            case 10:
                return months[9];
            case 11:
                return months[10];
            case 12:
                return months[11];
        }

        return "";
    }
}

