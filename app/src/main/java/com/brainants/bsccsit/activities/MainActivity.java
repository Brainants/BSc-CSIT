package com.brainants.bsccsit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.admin.AdminPanel;
import com.brainants.bsccsit.advance.BackgroundTaskHandler;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.fragments.Community;
import com.brainants.bsccsit.fragments.Forum;
import com.brainants.bsccsit.fragments.NewsEvents;
import com.brainants.bsccsit.fragments.Projects;
import com.brainants.bsccsit.fragments.TuNotices;
import com.brainants.bsccsit.fragments.eLibrary;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    public static FloatingActionButton fab;
    public static DrawerLayout drawerLayout;
    public static CoordinatorLayout coordinatorLayout;
    public static String current = "Home";
    private FragmentManager manager;
    private int previous;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        manager = getSupportFragmentManager();

        //Login chaina vane login activity ma lanchha
        if (!pref.getBoolean("loggedIn", false)) {
            if (pref.getBoolean("loggedFirstIn", false)) {
                startActivity(new Intent(this, CompleteLogin.class));
                finish();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return;
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.mainAppBar);
            toolbar.setElevation(0);
            appBarLayout.setElevation(0);
        }
        navigationView = (NavigationView) findViewById(R.id.naviView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCood);
        View view = navigationView.getHeaderView(0);
        final CircleImageView imageView1 = (CircleImageView) view.findViewById(R.id.profilePicture);
        TextView name = (TextView) view.findViewById(R.id.nameHeader);
        TextView email = (TextView) view.findViewById(R.id.emailHeader);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, R.string.Open, R.string.Close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        fab = (FloatingActionButton) findViewById(R.id.mainFab);
        name.setText(getSharedPreferences("loginInfo", MODE_PRIVATE).getString("FullName", ""));
        email.setText(getSharedPreferences("loginInfo", MODE_PRIVATE).getString("email", ""));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle("Home");

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //checkAdmin
        navigationView.getMenu().getItem(7).setVisible(pref.getBoolean("admin", false));

        constructJob();

        previous = R.id.newsEvent;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                navigate(item);
                return true;
            }
        });
        Picasso.with(this).load("https://graph.facebook.com/" + getSharedPreferences("loginInfo", MODE_PRIVATE).getString("UserID", "") + "/picture?type=large").into(imageView1);
        fab.hide();
        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserProfile.class).putExtra("userID", getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", "")));
            }
        });
        final Intent intent = getIntent();
        final Uri uri = intent.getData();

        if (uri != null) {
            if ("brainants".equals(uri.getScheme()) && "bsccsit".equals(uri.getHost())) {
                // Cool, we have a URI addressed to this activity!
                String mQuery = uri.getQueryParameter("fragment");
                Log.d("Debug", mQuery);
                if (mQuery.equals("home"))
                    navigate(navigationView.getMenu().findItem(R.id.newsEvent));
                else if (mQuery.equals("notice"))
                    navigate(navigationView.getMenu().findItem(R.id.TUNotices));
                else if (mQuery.equals("elibrary"))
                    navigate(navigationView.getMenu().findItem(R.id.elibrary));
                else if (mQuery.equals("projects"))
                    navigate(navigationView.getMenu().findItem(R.id.projects));
                else if (mQuery.equals("community"))
                    navigate(navigationView.getMenu().findItem(R.id.community));
                else if (mQuery.equals("forum"))
                    navigate(navigationView.getMenu().findItem(R.id.forum));
            }
        } else {
            manager.beginTransaction().replace(R.id.fragHolder, new NewsEvents()).commit();
        }
    }

    private void navigate(MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(findViewById(R.id.naviView));
        fab.hide();
        switch (id) {
            case R.id.newsEvent:
                setTitle("Home");
                current = "Home";
                item.setChecked(true);
                previous = id;
                manager.beginTransaction().replace(R.id.fragHolder, new NewsEvents()).commit();
                break;
            case R.id.TUNotices:
                setTitle("TU Notices");
                current = "TU Notices";
                item.setChecked(true);
                previous = id;
                manager.beginTransaction().replace(R.id.fragHolder, new TuNotices()).commit();
                break;
            case R.id.elibrary:
                setTitle("E-Library");
                item.setChecked(true);
                current = "E-Library";
                previous = id;
                manager.beginTransaction().replace(R.id.fragHolder, new eLibrary()).commit();
                break;
            case R.id.projects:
                setTitle("Projects");
                item.setChecked(true);
                current = "Projects";
                previous = id;
                manager.beginTransaction().replace(R.id.fragHolder, new Projects()).commit();
                break;
            case R.id.community:
                setTitle("Communities");
                item.setChecked(true);
                current = "Communities";
                previous = id;
                manager.beginTransaction().replace(R.id.fragHolder, new Community()).commit();
                break;
            case R.id.forum:
                setTitle("Forum");
                item.setChecked(true);
                current = "Forum";
                previous = id;
                manager.beginTransaction().replace(R.id.fragHolder, new Forum()).commit();
                break;

            case R.id.setting:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;
            case R.id.adminPanel:
                startActivity(new Intent(MainActivity.this, AdminPanel.class));
                break;
            case R.id.logout:
                new MaterialDialog.Builder(this)
                        .title("Logout")
                        .content("Are you sure you want to logout?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                getSharedPreferences("loginInfo", MODE_PRIVATE).edit().clear().apply();
                                getSharedPreferences("misc", MODE_PRIVATE).edit().clear().apply();
                                getSharedPreferences("community", MODE_PRIVATE).edit().clear().apply();
                                getSharedPreferences("notifications", MODE_PRIVATE).edit().clear().apply();
                                Singleton.getInstance().getGcmScheduler().cancelTask("periodic", BackgroundTaskHandler.class);
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM popularCommunities");
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM eLibrary");
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM myCommunities");
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM news");
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM events");
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM projects");
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM notices");
                                Singleton.getInstance().getDatabase().execSQL("DELETE FROM notifications");
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .show();
                break;
            case R.id.about:
                startActivity(new Intent(MainActivity.this, AboutUs.class));
                break;
            case R.id.rate_us:
                new MaterialDialog.Builder(MainActivity.this)
                        .title("Rate us 5 star")
                        .content("Help us in development by rating us 5 star on play store.")
                        .positiveText("Rate")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //todo call Play store
                            }
                        })
                        .build()
                        .show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(findViewById(R.id.naviView));
            return true;
        } else if (item.getItemId() == R.id.action_notif) {
            startActivity(new Intent(MainActivity.this, Notification.class));
            finish();
        } else if (item.getItemId()==R.id.action_feedback) {
            startActivity(new Intent(MainActivity.this, Feedback.class));
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_main, menu);

        if (Singleton.hasNewNotifications())
            menu.getItem(0).setIcon(R.drawable.bell_fill);
        else
            menu.getItem(0).setIcon(R.drawable.bell_outline);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else if (!current.equals("Home")) {
            setTitle("Home");
            current = "Home";
            navigationView.getMenu().getItem(0).setChecked(true);
            previous = navigationView.getMenu().getItem(0).getItemId();
            fab.hide();
            manager.beginTransaction().replace(R.id.fragHolder, new NewsEvents()).commit();
        } else {
            super.onBackPressed();
        }
    }

    private void constructJob() {

        String tag = "periodic";

        GcmNetworkManager mScheduler = Singleton.getInstance().getGcmScheduler();

        long periodSecs = 1800L;

        PeriodicTask periodic = new PeriodicTask.Builder()
                .setService(BackgroundTaskHandler.class)
                .setPeriod(periodSecs)
                .setTag(tag)
                .setFlex(periodSecs)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED)
                .build();
        mScheduler.schedule(periodic);
    }

}