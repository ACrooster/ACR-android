package nl.acr.rooster;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.TypedValue;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.res.Resources.Theme;

public class ScheduleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    static ScheduleFragment sf = new ScheduleFragment();
    static AnnouncementsFragment af = new AnnouncementsFragment();
    static FriendsFragment ff = new FriendsFragment();

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle(R.string.title_activity_schedule);

            Spinner spinner = (Spinner)findViewById(R.id.week_spinner);
            spinner.setVisibility(View.VISIBLE);

            replaceFragment(sf);
        } else if (id == R.id.nav_announcements) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.title_activity_schedule_announcements);

            Spinner spinner = (Spinner)findViewById(R.id.week_spinner);
            spinner.setVisibility(View.GONE);

            replaceFragment(af);
        } else if (id == R.id.nav_friends) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.title_activity_schedule_friends);

            Spinner spinner = (Spinner)findViewById(R.id.week_spinner);
            spinner.setVisibility(View.GONE);

            replaceFragment(ff);
        } else if (id == R.id.nav_preferences) {

            Intent goToPreferences = new Intent(getApplicationContext(), PreferencesActivity.class);
            startActivity(goToPreferences);
        } else if (id == R.id.nav_info_details) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.schedule_fragment_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_menu, menu);

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
                TextView weekLabel = (TextView) rootView.findViewById(R.id.week_label);
                weekLabel.setText("Week " + (week + 1));
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
            rootView =  inflater.inflate(R.layout.fragment_schedule, containter, false);
            return rootView;
        }
    }

    public static class AnnouncementsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
            View rootView =  inflater.inflate(R.layout.fragment_schedule_announcements, containter, false);
            return rootView;
        }
    }

    public static class FriendsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
            View rootView =  inflater.inflate(R.layout.fragment_schedule_friends, containter, false);
            return rootView;
        }
    }
}
