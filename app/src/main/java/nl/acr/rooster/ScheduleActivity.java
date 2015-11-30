package nl.acr.rooster;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.transition.Slide;
import android.util.TypedValue;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.res.Resources.Theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import go.framework.Framework;

public class ScheduleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static ScheduleFragment sf = new ScheduleFragment();
    static AnnouncementsFragment af = new AnnouncementsFragment();
    static FriendsFragment ff = new FriendsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert (getSupportActionBar() != null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String weekList[] = new String[53];
        for (int i = 0; i < weekList.length; i++) {
            weekList[i] = "Week " + (i + 1);
        }

        Spinner weekSpinner = (Spinner)findViewById(R.id.week_spinner);
        weekSpinner.setAdapter(new ScheduleAdapter(toolbar.getContext(), weekList));
        weekSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sf.setWeek(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // TODO: Make this pick the correct week
        weekSpinner.setSelection(47);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        replaceFragment(sf);
        sf.setWeek(47);

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

        // TODO: Check if the user is online/offline
//        Snackbar.make(findViewById(R.id.drawer_layout), getResources().getString(R.string.offline), Snackbar.LENGTH_INDEFINITE)
//                .setAction(getResources().getString(R.string.connect), new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        Log.w("Connect", "Trying to reconnect");
//                    }
//                })
//                .show();
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

        Spinner spinner = (Spinner)findViewById(R.id.week_spinner);
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

            spinner.setVisibility(View.VISIBLE);
            menu.findItem(R.id.search).setVisible(true);

            replaceFragment(sf);
        } else if (id == R.id.nav_announcements) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setTitle(R.string.title_activity_schedule_announcements);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAnnouncements)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorAnnouncementsDark));
            }

            spinner.setVisibility(View.GONE);
            menu.findItem(R.id.search).setVisible(false);

            replaceFragment(af);
        } else if (id == R.id.nav_friends) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorFriends)));
            ab.setTitle(R.string.title_activity_schedule_friends);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorFriendsDark));
            }

            spinner.setVisibility(View.GONE);
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

    private static class ScheduleAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public ScheduleAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_2, android.R.id.text1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = super.getView(position, convertView, parent);
            TextView weekView = (TextView) view.findViewById(android.R.id.text1);
            TextView nameView = (TextView) view.findViewById(android.R.id.text2);

            // TODO: This needs to be done in code
            weekView.setTypeface(null, Typeface.BOLD);
            weekView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            nameView.setText(R.string.title_activity_schedule);

            // TODO: Check if this code also works on other screens
            Rect weekBounds = new Rect();
            Rect nameBounds = new Rect();
            Paint weekPaint = nameView.getPaint();
            Paint namePaint = weekView.getPaint();
            weekPaint.getTextBounds(weekView.getText().toString(), 0, weekView.getText().length(), weekBounds);
            namePaint.getTextBounds(nameView.getText().toString(), 0, nameView.getText().length(), nameBounds);
            view.getLayoutParams().width = Math.max(nameBounds.width(), weekBounds.width()) + 100;

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    public static class ScheduleFragment extends Fragment {

        View rootView;
        int week;

        List<ClassInfo> classArrayList = new ArrayList<>();
        ClassListAdapter ca = new ClassListAdapter(classArrayList);

        public void setWeek(int week) {

            // TODO: This is just test code
            if (rootView != null) {
                this.week = week+1;
                createList();
            }
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

            return rootView;
        }

        private void createList() {
            // TODO: Add more error handling
            // TODO: Do this in the background
            Framework.RequestScheduleData(2015, week);
            switch ((int) Framework.GetError()) {
                case (int) Framework.ERROR_NONE:
                    classArrayList.clear();

                    int classCount = (int) Framework.GetClassCount();
                    for (int i = 0; i < classCount; i++) {
                        if (Framework.IsClassValid(i)) {
                            classArrayList.add(new ClassInfo(Framework.GetClassName(i), Framework.GetClassTeacher(i), Framework.GetClassStartTime(i), Framework.GetClassEndTime(i), Framework.GetClassRoom(i), Framework.GetClassStatus(i), Framework.GetClassStartUnix(i), Framework.GetClassTimeSlot(i)));
                        }
                    }

                    Collections.sort(classArrayList, classSorter);

                    boolean endOfDay = false;

                    int size = classArrayList.size();
                    for (int i = 0; i < size; i++) {
                        int free = 0;
                        if (!endOfDay) {
                            if (i + 1 < size) {
                                free = classArrayList.get(i + 1).timeSlot - classArrayList.get(i).timeSlot - 1;
                            }

                            for (int j = 0; j < free; j++) {
                                classArrayList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, classArrayList.get(i).timeStartUnix+j+1,classArrayList.get(i).timeSlot+j+1));
                            }
                        } else {
                            free = classArrayList.get(i).timeSlot - 1;

                            for (int j = 0; j < free; j++) {
                                classArrayList.add(new ClassInfo("", "", "", "", "", Framework.STATUS_FREE, classArrayList.get(i).timeStartUnix-j-1,classArrayList.get(i).timeSlot-j-1));
                            }
                        }

                        endOfDay = free<0;

                    }

                    for (int i = 0; i < 5; i++) {
                        classArrayList.add(new ClassInfo(getDay(i) + " " + Framework.GetDayNumber(i) + " " + getMonth((int)Framework.GetDayMonth(i)), Framework.GetDayUnix(i)));
                    }

                    Collections.sort(classArrayList, classSorter);

                    ca.notifyDataSetChanged();
                    break;
            }
        }

        Comparator<ClassInfo> classSorter = new Comparator<ClassInfo>() {
            @Override
            public int compare(ClassInfo lhs, ClassInfo rhs) {
                return (int)(lhs.timeStartUnix - rhs.timeStartUnix);
            }
        };

        private String mon = "";
        private String tue = "";
        private String wed = "";
        private String thu = "";
        private String fri = "";

        private String jan = "";
        private String feb = "";
        private String mar = "";
        private String apr = "";
        private String may = "";
        private String jun = "";
        private String jul = "";
        private String aug = "";
        private String sep = "";
        private String oct = "";
        private String nov = "";
        private String dec = "";

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
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
        }

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
}
