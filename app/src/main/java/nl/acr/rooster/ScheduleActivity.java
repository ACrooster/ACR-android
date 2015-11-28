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
import android.support.design.widget.Snackbar;
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
import java.util.List;

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

        public void setWeek(int week) {

            // TODO: This is just test code
            if (rootView != null) {
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
            ClassListAdapter ca = new ClassListAdapter(createList());
            classList.setAdapter(ca);

            return rootView;
        }

        private List<ClassInfo> createList() {
            List<ClassInfo> result = new ArrayList<>();

            result.add(new ClassInfo("Maandag 23 november", Status.DATE));
            result.add(new ClassInfo("Gym", "ENH", "8:30", "9:30", "S.042", Status.NORMAL));
            result.add(new ClassInfo("Gym", "ENH", "9:30", "10:30", "S.042", Status.NORMAL));
            result.add(new ClassInfo("Wiskunde B", "BRM & DRO", "10:50", "11:50", "3.045", Status.NORMAL));
            result.add(new ClassInfo("", "", "", "", "", Status.FREE));
            result.add(new ClassInfo("Economie", "MER", "13:20", "14:20", "3.040", Status.NORMAL));

            result.add(new ClassInfo("Dinsdag 24 november", Status.DATE));
            result.add(new ClassInfo("", "", "", "", "", Status.FREE));
            result.add(new ClassInfo("", "", "", "", "", Status.FREE));
            result.add(new ClassInfo("", "", "", "", "", Status.FREE));
            result.add(new ClassInfo("Grieks", "NOE", "11:50", "12:50", "3.034", Status.NORMAL));
            result.add(new ClassInfo("Scheikunde", "VRI", "13:20", "14:20", "0.011", Status.NORMAL));

            result.add(new ClassInfo("Woensdag 25 november", Status.DATE));
            result.add(new ClassInfo("Natuurkunde", "VRI", "8:30", "9:30", "0.011", Status.CANCELED));
            result.add(new ClassInfo("Wiskunde B", "BRM & DRO", "9:30", "10:30", "3.045", Status.CHANGED));
            result.add(new ClassInfo("Economie", "MER", "10:50", "11:50", "3.040", Status.NORMAL));
            result.add(new ClassInfo("", "", "", "", "", Status.FREE));
            result.add(new ClassInfo("Nederlands", "BCE", "13:20", "14:20", "3.005", Status.CANCELED));
            result.add(new ClassInfo("Engels", "ENT", "14:20", "15:20", "3.034", Status.NORMAL));

            result.add(new ClassInfo("Donderdag 26 november", Status.DATE));
            result.add(new ClassInfo("Grieks", "NOE", "8:30", "9:30", "3.042", Status.CANCELED));
            result.add(new ClassInfo("Herkansing", "", "9:30", "10:30", "", Status.ACTIVITY));
            result.add(new ClassInfo("Herkansing", "", "10:50", "11:50", "", Status.ACTIVITY));
            result.add(new ClassInfo("Herkansing", "", "11:50", "12:50", "", Status.ACTIVITY));
            result.add(new ClassInfo("Informatica", "VBR", "13:20", "14:20", "0.036", Status.NORMAL));
            result.add(new ClassInfo("Mentoruur", "BCE & STU", "14:20", "15:20", "3038 & 3039", Status.NORMAL));

            result.add(new ClassInfo("Vrijdag 27 november", Status.DATE));
            result.add(new ClassInfo("Scheikunde", "VRI", "8:30", "9:30", "0.011", Status.NORMAL));
            result.add(new ClassInfo("Wiskunde B", "BRM & DRO", "9:30", "10:30", "3.045", Status.NORMAL));
            result.add(new ClassInfo("Informatica", "VBR", "10:50", "11:50", "0.036", Status.NORMAL));
            result.add(new ClassInfo("Natuurkunde", "VRI", "11:50", "12:50", "0.011", Status.NORMAL));
            result.add(new ClassInfo("", "", "", "", "", Status.FREE));
            result.add(new ClassInfo("Grieks", "NOE", "14:20", "15:20", "3042", Status.NORMAL));

            return result;
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
