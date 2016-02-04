package nl.acr.rooster;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.InputType;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import go.framework.Framework;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final ScheduleFragment sf = new ScheduleFragment();
//    static final AnnouncementsFragment af = new AnnouncementsFragment();
    static final FriendsFragment ff = new FriendsFragment();

    static private Fragment currentFragment = sf;

    private static DatePickerDialog dialog;
    static private LinearLayout datePickerButton;
    public static DrawerLayout drawer;
    static public NavigationView navigationView;
    public static SwipeRefreshLayout refreshSchedule;
    public static ProgressBar progressBar;

    static Calendar cal;

    public static boolean landscape = false;

    public static Resources resources = null;
    private static FragmentManager fragmentManager;
    private static ActionBar actionBar;

    public static Menu menu;

    public static MaterialDialog.Builder edit;
    public static int editPosition = 0;
    public static MaterialDialog.InputCallback editCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert (actionBar != null);
        actionBar.setDisplayShowTitleEnabled(false);

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

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        datePickerButton = (LinearLayout)findViewById(R.id.date_picker_button);
        UpdateSchedule.datePickerWeek = (TextView)findViewById(R.id.date_picker_week);
        UpdateSchedule.datePickerStudent = (TextView)findViewById(R.id.date_picker_student);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        UpdateSchedule.dayOfWeek = DayPicker.toDayNumber(cal.get(Calendar.DAY_OF_WEEK));

        // TODO: Figure out the colors
        dialog = DatePickerDialog.newInstance(new DayPicker(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.vibrate(false);

        resources = getResources();
        fragmentManager = getFragmentManager();

        replaceFragment(currentFragment);

        SharedPreferences.Editor editor = settings.edit();

        try {
            if (settings.getInt("version", 0) < getPackageManager().getPackageInfo(getPackageName(), 0).versionCode) {

                editor.putInt("version", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
                editor.apply();

                MaterialDialog.Builder update = new MaterialDialog.Builder(this)
                        .title(R.string.update)
                        .content(R.string.update_text)
                        .positiveText(R.string.ok);
                update.show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (settings.getInt("tutorial", 0) < StartActivity.TUTORIAL_VERSION) {

            editor.putInt("tutorial", StartActivity.TUTORIAL_VERSION);
            editor.apply();

            MaterialDialog.Builder tutorial = new MaterialDialog.Builder(this)
                    .title(R.string.tutorial)
                    .content(Html.fromHtml(getString(R.string.tutorial_text)))
                    .positiveText(R.string.ok);
            tutorial.show();
        }

        if (!settings.getString("friends", "").equals("")) {

            Gson gson = new Gson();
            Type type = new TypeToken<List<FriendInfo>>(){}.getType();

            FriendsFragment.friendArrayList.clear();
            List<FriendInfo> friendList = gson.fromJson(settings.getString("friends", ""), type);
            FriendsFragment.friendArrayList.addAll(friendList);

        }

        invalidateOptionsMenu();
        editCallback = new MaterialDialog.InputCallback() {

            @Override
            public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {

                FriendsFragment.friendArrayList.get(editPosition).name = charSequence.toString();
                Collections.sort(FriendsFragment.friendArrayList, MainActivity.nameSorter);
                FriendsFragment.fa.notifyDataSetChanged();

                Gson gson = new Gson();
                String jsonFriends = gson.toJson(FriendsFragment.friendArrayList);

                SharedPreferences settings = getSharedPreferences(StartActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("friends", jsonFriends);
                editor.apply();
            }
        };
        edit = new MaterialDialog.Builder(this)
                .title(getText(R.string.edit))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .neutralText(R.string.cancel)
                .negativeText(R.string.remove)
                .onNegative(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {

                        FriendsFragment.friendArrayList.remove(editPosition);
                        Collections.sort(FriendsFragment.friendArrayList, MainActivity.nameSorter);
                        FriendsFragment.fa.notifyDataSetChanged();

                        Gson gson = new Gson();
                        String jsonFriends = gson.toJson(FriendsFragment.friendArrayList);

                        SharedPreferences settings = getSharedPreferences(StartActivity.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putString("friends", jsonFriends);
                        editor.apply();
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (menu.findItem(R.id.menu_search).isActionViewExpanded()) {
                menu.findItem(R.id.menu_search).collapseActionView();
            }
            Log.w("Search", query);

            ScheduleFragment.user = query.toLowerCase();
            UpdateSchedule.scroll = true;
            ScheduleFragment.createList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // NOTE: Never update more than once every two minutes
        if (((System.currentTimeMillis()/1000) - UpdateSchedule.timeOfLastUpdate) > 120) {
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
//            moveTaskToBack(true);
            if (!ScheduleFragment.user.equals(Framework.MY_SCHEDULE) && sf.isVisible()) {

                ScheduleFragment.user = Framework.MY_SCHEDULE;
                UpdateSchedule.scroll = true;
                ScheduleFragment.createList();
            } else {

                finish();
            }
        }
    }

    // TODO: Move some off this stuff into seperate functions
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        assert(actionBar != null);

        progressBar.setVisibility(View.GONE);

        if (id == R.id.nav_schedule) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(R.string.title_activity_main);

            ScheduleFragment.user = Framework.MY_SCHEDULE;
            UpdateSchedule.dayOfWeek = DayPicker.toDayNumber(cal.get(Calendar.DAY_OF_WEEK));
            ScheduleFragment.setWeekUnix((int) (System.currentTimeMillis() / 1000));
            UpdateSchedule.scroll = true;
            ScheduleFragment.createList();
            datePickerButton.setVisibility(View.VISIBLE);
            menu.findItem(R.id.menu_search).setVisible(true);

            navigationView.getMenu().getItem(0).setChecked(true);
            if (ScheduleFragment.classArrayList.size() == 0) {
                progressBar.setVisibility(View.VISIBLE);
            }

            replaceFragment(sf);
            currentFragment = sf;
//        } else if (id == R.id.nav_announcements) {
//            ab.setDisplayShowTitleEnabled(true);
//            ab.setTitle(R.string.title_fragment_announcements);
//
//            datePickerButton.setVisibility(View.GONE);
//            menu.findItem(R.id.menu_search).setVisible(false);
//
//            replaceFragment(af);
        } else if (id == R.id.nav_friends) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.title_fragment_friends);

            datePickerButton.setVisibility(View.GONE);
            menu.findItem(R.id.menu_search).setVisible(false);

            replaceFragment(ff);
            currentFragment = ff;
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

        invalidateOptionsMenu();


        return true;
    }

    private static void replaceFragment(Fragment fragment) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            sf.setEnterTransition(new Slide(Gravity.RIGHT));
        }

        // TODO: Figure out a better transition system/speed up transition
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.schedule_fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (currentFragment == sf) {

            for (int i = 0; i < menu.size(); i++) {

                menu.getItem(i).setVisible(true);
            }
        } else {

            for (int i = 0; i < menu.size(); i++) {

                menu.getItem(i).setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MainActivity.menu = menu;

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            ScheduleFragment.createList();
        } else if (id == R.id.menu_today) {
            goToday();
        } else if (id == R.id.menu_star) {

            if (ScheduleFragment.user.equals(Framework.MY_SCHEDULE)) {

                Snackbar.make(findViewById(R.id.drawer_layout), getResources().getString(R.string.selfFriends), Snackbar.LENGTH_SHORT)
                        .show();

                return true;
            }

            for (int i = 0; i < FriendsFragment.friendArrayList.size(); i++) {
                if (FriendsFragment.friendArrayList.get(i).code.equals(ScheduleFragment.user)) {

                    Snackbar.make(findViewById(R.id.drawer_layout), getResources().getString(R.string.alreadyFriends), Snackbar.LENGTH_SHORT)
                            .show();

                    return true;
                }
            }

            MaterialDialog.Builder name = new MaterialDialog.Builder(this)
                    .title(getText(R.string.addFriend))
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.name), "", new MaterialDialog.InputCallback() {

                        @Override
                        public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {

                            FriendsFragment.friendArrayList.add(new FriendInfo(charSequence.toString(), ScheduleFragment.user));
                            Collections.sort(FriendsFragment.friendArrayList, nameSorter);
                            FriendsFragment.fa.notifyDataSetChanged();

                            Gson gson = new Gson();
                            String jsonFriends = gson.toJson(FriendsFragment.friendArrayList);

                            SharedPreferences settings = getSharedPreferences(StartActivity.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();

                            editor.putString("friends", jsonFriends);
                            editor.apply();
                        }
                    });
            name.show();
        }

        return true;
    }

    public static final Comparator<FriendInfo> nameSorter = new Comparator<FriendInfo>() {
        @Override
        public int compare(FriendInfo lhs, FriendInfo rhs) {
            return lhs.name.compareToIgnoreCase(rhs.name);
        }
    };

    public static void goToday() {
        cal.setTime(new Date(System.currentTimeMillis()));
        UpdateSchedule.dayOfWeek = DayPicker.toDayNumber(cal.get(Calendar.DAY_OF_WEEK));
        ScheduleFragment.setWeekUnix((int) (System.currentTimeMillis() / 1000));
        UpdateSchedule.scroll = true;
        ScheduleFragment.createList();
        dialog = DatePickerDialog.newInstance(new DayPicker(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public static void goTo(String code) {

        replaceFragment(sf);
        currentFragment = sf;

        assert(actionBar != null);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle(R.string.title_activity_main);

        ScheduleFragment.user = Framework.MY_SCHEDULE;
        UpdateSchedule.dayOfWeek = DayPicker.toDayNumber(cal.get(Calendar.DAY_OF_WEEK));
        ScheduleFragment.setWeekUnix((int) (System.currentTimeMillis() / 1000));
        UpdateSchedule.scroll = true;
        ScheduleFragment.createList();
        datePickerButton.setVisibility(View.VISIBLE);
        menu.findItem(R.id.menu_search).setVisible(true);

        navigationView.getMenu().getItem(0).setChecked(true);
        if (ScheduleFragment.classArrayList.size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ScheduleFragment.user = code;
        goToday();

        actionBar.invalidateOptionsMenu();
    }

}
