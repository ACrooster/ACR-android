package nl.acr.rooster;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import go.framework.Framework;

public class ScheduleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static ScheduleFragment sf = new ScheduleFragment();
    static AnnouncementsFragment af = new AnnouncementsFragment();
    static FriendsFragment ff = new FriendsFragment();

    private DatePickerDialog dialog;
    static private LinearLayout datePickerButton;
    static private TextView datePickerWeek;
    static private TextView datePickerStudent;
    static private DrawerLayout drawer;
    static public NavigationView navigationView;
    static private SwipeRefreshLayout refreshSchedule;
    static private ProgressBar progressBar;

    static private String mon = "";
    static private String tue = "";
    static private String wed = "";
    static private String thu = "";
    static private String fri = "";

    static private String jan = "";
    static private String feb = "";
    static private String mar = "";
    static private String apr = "";
    static private String may = "";
    static private String jun = "";
    static private String jul = "";
    static private String aug = "";
    static private String sep = "";
    static private String oct = "";
    static private String nov = "";
    static private String dec = "";

    static private int timeOfLastUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert (getSupportActionBar() != null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        // Sets the name and id in the header bar
        SharedPreferences settings = getSharedPreferences(StartActivity.PREFS_NAME, 0);
        View headerLayout = navigationView.getHeaderView(0);
        TextView name = (TextView)headerLayout.findViewById(R.id.student_name);
        TextView id = (TextView)headerLayout.findViewById(R.id.student_id);
        name.setText(settings.getString("name", ""));
        id.setText(settings.getString("id", ""));

        // TODO: Find a better way that keeps transparency
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        datePickerButton = (LinearLayout)findViewById(R.id.date_picker_button);
        datePickerWeek = (TextView)findViewById(R.id.date_picker_week);
        datePickerStudent = (TextView)findViewById(R.id.date_picker_student);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.show();
            }
        });

        // TODO: Figure out the colors
        dialog = new DatePickerDialog(this, R.style.DialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                DateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
                try {
                    int unixTime = (int) (dateFormat.parse(year + "-" + (monthOfYear+1) + "-" + (dayOfMonth+1)).getTime()/1000);
                    sf.setWeekUnix(unixTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        mon = this.getString(R.string.day_mon);
        tue = this.getString(R.string.day_tue);
        wed = this.getString(R.string.day_wed);
        thu = this.getString(R.string.day_thu);
        fri = this.getString(R.string.day_fri);

        jan = this.getString(R.string.month_jan);
        feb = this.getString(R.string.month_feb);
        mar = this.getString(R.string.month_mar);
        apr = this.getString(R.string.month_apr);
        may = this.getString(R.string.month_may);
        jun = this.getString(R.string.month_jun);
        jul = this.getString(R.string.month_jul);
        aug = this.getString(R.string.month_aug);
        sep = this.getString(R.string.month_sep);
        oct = this.getString(R.string.month_oct);
        nov = this.getString(R.string.month_nov);
        dec = this.getString(R.string.month_dec);

        replaceFragment(sf);
    }

    @Override
    public void onResume() {
        super.onResume();

        // NOTE: Never update more than once every ten seconds
        if (((System.currentTimeMillis()/1000) - timeOfLastUpdate) > 10) {
            sf.createList();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search) {
            Intent goToSearch = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(goToSearch);
        }

        return true;
    }

    // TODO: Move some off this stuff into seperate functions
    // TODO: Make sure the navdrawer still renders in status bar
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        Spinner spinner = (Spinner)findViewById(R.id.week_spinner);
        ActionBar ab = getSupportActionBar();
        Window window = getWindow();
        assert(ab != null);

        if (id == R.id.nav_schedule) {
            ab.setDisplayShowTitleEnabled(false);
            ab.setTitle(R.string.title_activity_schedule);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            ScheduleFragment.user = Framework.MY_SCHEDULE;
            datePickerButton.setVisibility(View.VISIBLE);
            menu.findItem(R.id.search).setVisible(true);

            replaceFragment(sf);
        } else if (id == R.id.nav_announcements) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setTitle(R.string.title_activity_schedule_announcements);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAnnouncements)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorAnnouncementsDark));
            }

            datePickerButton.setVisibility(View.GONE);
            menu.findItem(R.id.search).setVisible(false);

            replaceFragment(af);
        } else if (id == R.id.nav_friends) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorFriends)));
            ab.setTitle(R.string.title_activity_schedule_friends);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorFriendsDark));
            }

            datePickerButton.setVisibility(View.GONE);
            menu.findItem(R.id.search).setVisible(false);

            replaceFragment(ff);
        } else if (id == R.id.nav_preferences) {

            Intent goToPreferences = new Intent(getApplicationContext(), PreferencesActivity.class);
            startActivity(goToPreferences);
        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            sf.setEnterTransition(new Slide(Gravity.RIGHT));
        }

        // TODO: Figure out a better transition system/speed up transition
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.schedule_fragment_container, fragment)
                .commit();
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_menu, menu);

        this.menu = menu;

        return true;
    }

    public static class ScheduleFragment extends Fragment {

        private View rootView;
        static private int weekUnix;

        static public String user = Framework.MY_SCHEDULE;

        static private List<ClassInfo> classArrayList = new ArrayList<>();
        static private ClassListAdapter ca = new ClassListAdapter(classArrayList);

        public void setWeekUnix(int weekUnix) {

            ScheduleFragment.weekUnix = weekUnix;
            createList();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
            rootView =  inflater.inflate(R.layout.fragment_schedule, containter, false);

            RecyclerView classList = (RecyclerView) rootView.findViewById(R.id.classListMon);
            classList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayout.VERTICAL);
            classList.setLayoutManager(llm);
            classList.setAdapter(ca);

            refreshSchedule = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_schedule);
            refreshSchedule.setColorSchemeResources(R.color.colorPrimary);
            refreshSchedule.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {

                    createList();
                }
            });

            sf.setWeekUnix((int) (System.currentTimeMillis() / 1000));

            return rootView;
        }

        public static void createList() {
            // TODO: Add more error handling
            // TODO: Do this in the background

            UpdateSchedule updateSchedule = new UpdateSchedule();
            updateSchedule.execute((Void) null);
        }

    }

    public static class AnnouncementsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_schedule_announcements, container, false);
        }
    }

    public static class FriendsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_schedule_friends, container, false);
        }
    }

    public static class UpdateSchedule extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {

            refreshSchedule.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Framework.RequestScheduleData(ScheduleFragment.weekUnix, ScheduleFragment.user);
            switch ((int) Framework.GetError()) {
                case (int) Framework.ERROR_NONE:

                    ScheduleFragment.classArrayList.clear();

                    int classCount = (int) Framework.GetClassCount();
                    for (int i = 0; i < classCount; i++) {
                        if (Framework.IsClassValid(i)) {
                            ScheduleFragment.classArrayList.add(new ClassInfo(Framework.GetClassName(i), Framework.GetClassTeacher(i), Framework.GetClassStartTime(i), Framework.GetClassEndTime(i), Framework.GetClassRoom(i), Framework.GetClassStatus(i), Framework.GetClassStartUnix(i), Framework.GetClassTimeSlot(i)));
                        }
                    }

                    Collections.sort(ScheduleFragment.classArrayList, classSorter);

                    boolean endOfDay = false;

                    int size = ScheduleFragment.classArrayList.size();
                    for (int i = 0; i < size; i++) {
                        int free;
                        if (i == 0 || endOfDay) {
                            free = ScheduleFragment.classArrayList.get(i).timeSlot - 1;

                            for (int j = 0; j < free; j++) {
                                ScheduleFragment.classArrayList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, ScheduleFragment.classArrayList.get(i).timeStartUnix - j - 1, ScheduleFragment.classArrayList.get(i).timeSlot - j - 1));
                            }
                        }

                        if (i + 1 < size) {
                            free = ScheduleFragment.classArrayList.get(i + 1).timeSlot - ScheduleFragment.classArrayList.get(i).timeSlot - 1;
                            endOfDay = (ScheduleFragment.classArrayList.get(i + 1).timeStartUnix - ScheduleFragment.classArrayList.get(i).timeStartUnix) > 10*3600;
                        } else {
                            free = 0;
                            endOfDay = false;
                        }

                        for (int j = 0; j < free; j++) {
                            ScheduleFragment.classArrayList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, ScheduleFragment.classArrayList.get(i).timeStartUnix+j+1,ScheduleFragment.classArrayList.get(i).timeSlot+j+1));
                        }
                    }

                    for (int i = 0; i < 5; i++) {
                        ScheduleFragment.classArrayList.add(new ClassInfo(getDay(i) + " " + Framework.GetDayNumber(i) + " " + getMonth((int)Framework.GetDayMonth(i)), Framework.GetDayUnix(i)));
                    }

                    Collections.sort(ScheduleFragment.classArrayList, classSorter);

                    timeOfLastUpdate = (int) (System.currentTimeMillis()/1000);
                    return true;

            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                ScheduleFragment.ca.notifyDataSetChanged();
                if (datePickerWeek != null && datePickerStudent != null) {
                    // TODO: This should go in a function
                    // TODO: This should use strings.xml
                    datePickerWeek.setText("Week " + Framework.GetWeek());
                    // TODO: Add system that checks whose schedule you are looking at
                    datePickerStudent.setText(Framework.GetUser());
                }
            } else {
                Snackbar.make(drawer, "Je bent op dit moment offline", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Opnieuw proberen", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                sf.createList();
                            }
                        })
                        .show();
            }

            progressBar.setVisibility(View.GONE);
            refreshSchedule.setRefreshing(false);
        }

        Comparator<ClassInfo> classSorter = new Comparator<ClassInfo>() {
            @Override
            public int compare(ClassInfo lhs, ClassInfo rhs) {
                return (int)(lhs.timeStartUnix - rhs.timeStartUnix);
            }
        };

            // TODO: Check if there is a built in system get this
        public String getDay(int index) {

            switch (index) {
                case 0:
                    return mon;
                case 1:
                    return tue;
                case 2:
                    return wed;
                case 3:
                    return thu;
                case 4:
                    return fri;
            }

            return "";
        }

        // TODO: Check if there is a built in system get this
        public String getMonth(int index) {

            switch (index) {
                case 1:
                    return jan;
                case 2:
                    return feb;
                case 3:
                    return mar;
                case 4:
                    return apr;
                case 5:
                    return may;
                case 6:
                    return jun;
                case 7:
                    return jul;
                case 8:
                    return aug;
                case 9:
                    return sep;
                case 10:
                    return oct;
                case 11:
                    return nov;
                case 12:
                    return dec;
            }

            return "";
        }

    }
}
