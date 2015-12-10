package nl.acr.rooster;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import go.framework.Framework;

import static nl.acr.rooster.ScheduleActivity.ScheduleFragment.classList;
import static nl.acr.rooster.ScheduleActivity.ScheduleFragment.createList;

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

    static private Calendar cal;

    // TODO: Make these arrays
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

    static private boolean scroll = true;
    static private boolean landscape = false;
    static private int[] datePosition = new int[5];
    static private int timeOfLastUpdate = 0;
    static private int dayOfWeek = 0;
    static private MaterialDialog.Builder rights;

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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
//        }

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        datePickerButton = (LinearLayout)findViewById(R.id.date_picker_button);
        datePickerWeek = (TextView)findViewById(R.id.date_picker_week);
        datePickerStudent = (TextView)findViewById(R.id.date_picker_student);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        dayOfWeek = toDayNumber(cal.get(Calendar.DAY_OF_WEEK));

        // TODO: Figure out the colors
        dialog = DatePickerDialog.newInstance(new DayPicker(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.vibrate(false);

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

         rights = new MaterialDialog.Builder(this)
                .title(R.string.rights)
                .content(R.string.rights_text)
                .positiveText(R.string.ok);
    }

    public int toDayNumber(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            default:
                return 0;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (menu.findItem(R.id.menu_search).isActionViewExpanded()) {
                menu.findItem(R.id.menu_search).collapseActionView();
            }
            Log.w("Search", query);

            navigationView.getMenu().getItem(0).setChecked(false);
            ScheduleFragment.user = query;
            scroll = true;
            createList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // NOTE: Never update more than once every two minutes
        if (((System.currentTimeMillis()/1000) - timeOfLastUpdate) > 120) {
            goToday();
            Log.w("Resume", "Resuming");
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

        progressBar.setVisibility(View.GONE);

        if (id == R.id.nav_schedule) {
            ab.setDisplayShowTitleEnabled(false);
            ab.setTitle(R.string.title_activity_schedule);
//            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
//            }

            ScheduleFragment.user = Framework.MY_SCHEDULE;
            dayOfWeek = toDayNumber(cal.get(Calendar.DAY_OF_WEEK));
            sf.setWeekUnix((int) (System.currentTimeMillis() / 1000));
            scroll = true;
            createList();
            datePickerButton.setVisibility(View.VISIBLE);
            menu.findItem(R.id.menu_search).setVisible(true);

            navigationView.getMenu().getItem(0).setChecked(true);
            progressBar.setVisibility(View.VISIBLE);

            replaceFragment(sf);
        } else if (id == R.id.nav_announcements) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setTitle(R.string.title_activity_schedule_announcements);
//            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAnnouncements)));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.setStatusBarColor(getResources().getColor(R.color.colorAnnouncementsDark));
//            }

            datePickerButton.setVisibility(View.GONE);
            menu.findItem(R.id.menu_search).setVisible(false);

            replaceFragment(af);
        } else if (id == R.id.nav_friends) {
            ab.setDisplayShowTitleEnabled(true);
//            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorFriends)));
            ab.setTitle(R.string.title_activity_schedule_friends);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.setStatusBarColor(getResources().getColor(R.color.colorFriendsDark));
//            }

            datePickerButton.setVisibility(View.GONE);
            menu.findItem(R.id.menu_search).setVisible(false);

            replaceFragment(ff);
        } else if (id == R.id.nav_preferences) {

            Intent goToPreferences = new Intent(getApplicationContext(), PreferencesActivity.class);
            startActivity(goToPreferences);
        } else if (id == R.id.nav_info) {

            MaterialDialog.Builder about = new MaterialDialog.Builder(this)
                    .title(R.string.about)
                    .content(R.string.about_text)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.contact)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {

                            Uri uri = Uri.parse("mailto:amstelveencollegerooster@gmail.com")
                                    .buildUpon()
                                    .build();

                            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

                            startActivity(Intent.createChooser(intent, getString(R.string.send)));
                        }
                    });
            about.show();
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

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            createList();
        } else if (id == R.id.menu_today) {
            goToday();
        }

        return true;
    }

    public void goToday() {
        cal.setTime(new Date(System.currentTimeMillis()));
        dayOfWeek = toDayNumber(cal.get(Calendar.DAY_OF_WEEK));
        sf.setWeekUnix((int) (System.currentTimeMillis() / 1000));
        scroll = true;
        createList();
        dialog = DatePickerDialog.newInstance(new DayPicker(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public static class ScheduleFragment extends Fragment {

        private View rootView;
        static private int weekUnix = 0;

        static public String user = Framework.MY_SCHEDULE;

        static protected RecyclerView classList;
        static private List<ClassInfo> classArrayList = new ArrayList<>();
        static private ClassListAdapter ca = new ClassListAdapter(classArrayList);

        public void setWeekUnix(int weekUnix) {

            ScheduleFragment.weekUnix = weekUnix;
            Log.w("Week", String.valueOf(weekUnix));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
            rootView =  inflater.inflate(R.layout.fragment_schedule, containter, false);

            classList = (RecyclerView) rootView.findViewById(R.id.classListMon);
            classList.setHasFixedSize(true);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                classList.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
                classList.setAdapter(ca);
                landscape = false;
            } else {
                classList.setHasFixedSize(true);
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
                classList.setLayoutManager(layoutManager);
                classList.setAdapter(ca);
                landscape = true;
            }

            refreshSchedule = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_schedule);
            refreshSchedule.setColorSchemeResources(R.color.colorAccent);
            refreshSchedule.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {

                    createList();
                }
            });

            if (classArrayList.size() == 0) {
                progressBar.setVisibility(View.VISIBLE);
                classList.setVisibility(View.GONE);
            }

            if (weekUnix == 0) {
                setWeekUnix((int) (System.currentTimeMillis() / 1000));
            }

            createList();

            return rootView;
        }

        public static void createList() {
            // TODO: Add more error handling
            // TODO: Do this in the background

            UpdateSchedule updateSchedule = new UpdateSchedule();
            updateSchedule.execute(landscape);
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

    public static class UpdateSchedule extends AsyncTask<Boolean, Void, Integer> {

        @Override
        protected void onPreExecute() {

            refreshSchedule.setRefreshing(true);
        }

        @Override
        protected Integer doInBackground(Boolean... params) {
            Framework.RequestScheduleData(ScheduleFragment.weekUnix, ScheduleFragment.user);
            List<ClassInfo> tempList = new ArrayList<>();
            int error = (int) Framework.GetError();
            if (error == Framework.ERROR_NONE) {

                int classCount = (int) Framework.GetClassCount();
                for (int i = 0; i < classCount; i++) {
                    if (Framework.IsClassValid(i)) {
                        tempList.add(new ClassInfo(Framework.GetClassName(i), Framework.GetClassTeacher(i), Framework.GetClassStartTime(i), Framework.GetClassEndTime(i), Framework.GetClassRoom(i), Framework.GetClassStatus(i), Framework.GetClassStartUnix(i), Framework.GetClassTimeSlot(i)));
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
                            tempList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, tempList.get(i).timeStartUnix - j - 1, tempList.get(i).timeSlot - j - 1));
                        }
                    }

                    if (i + 1 < size) {
                        free = tempList.get(i + 1).timeSlot - tempList.get(i).timeSlot - 1;
                        endOfDay = (tempList.get(i + 1).timeStartUnix - tempList.get(i).timeStartUnix) > 10*3600;
                    } else {
                        free = 0;
                        endOfDay = false;
                    }

                    for (int j = 0; j < free; j++) {
                        tempList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, tempList.get(i).timeStartUnix+j+1,tempList.get(i).timeSlot+j+1));
                    }
                }

                ClassInfo[] dateInfo = new ClassInfo[5];
                for (int i = 0; i < 5; i++) {
//                        dateInfo[i] = new ClassInfo(getDay(i) + " " + Framework.GetDayNumber(i) + " " + getMonth((int) Framework.GetDayMonth(i)), Framework.GetDayUnix(i));
                    dateInfo[i] = new ClassInfo(getDay(i), Framework.GetDayUnix(i));
                    tempList.add(dateInfo[i]);
                }
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
                            longest = Math.max(datePosition[i+1]-datePosition[i], longest);
                        } else {
                            longest = Math.max(size-datePosition[i], longest);
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

                timeOfLastUpdate = (int) (System.currentTimeMillis()/1000);

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
                    datePickerWeek.setText("Week " + Framework.GetWeek());
                    // TODO: Add system that checks whose schedule you are looking at
                    datePickerStudent.setText(Framework.GetUser());

                    int position = datePosition[dayOfWeek];
                    if (scroll && ScheduleFragment.classArrayList.size() > position) {
                        Log.w("Scroll", "Scrolling to: " + position);
                        ScheduleFragment.classList.smoothScrollToPosition(position);
                        scroll = false;
                    }
                }
            } else if(error == Framework.ERROR_RIGHTS) {
                rights.show();
                ScheduleFragment.user = "~me";
            } else {
                Snackbar.make(drawer, "Je bent op dit moment offline", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Opnieuw proberen", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                createList();
                            }
                        })
                        .show();
            }

            progressBar.setVisibility(View.GONE);
            classList.setVisibility(View.VISIBLE);
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

    private class DayPicker implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePickerDialog view,int year,int monthOfYear,int dayOfMonth){
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date date=dateFormat.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                cal.setTime(date);
                dayOfWeek = toDayNumber(cal.get(Calendar.DAY_OF_WEEK));
                int unixTime = (int)(date.getTime()/1000 + 3600);
                sf.setWeekUnix(unixTime);
                scroll = true;
                createList();
            }catch(ParseException e){
                e.printStackTrace();
            }
        }
    }
}
