package com.techies.bsccsit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.admin.AdminPanel;
import com.techies.bsccsit.fragments.Community;
import com.techies.bsccsit.fragments.Forum;
import com.techies.bsccsit.fragments.NewsEvents;
import com.techies.bsccsit.fragments.Projects;
import com.techies.bsccsit.fragments.TuNotices;
import com.techies.bsccsit.fragments.eLibrary;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private FragmentManager manager;
    private int previous;
    public static FloatingActionButton fab;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);

        //Login chaina vane login activity ma lanchha
        if (!pref.getBoolean("loggedIn",false)){
            if (pref.getBoolean("loggedFirstIn",false)){
                startActivity(new Intent(this,CompleteLogin.class));
                finish();
            }else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
        if(!pref.getBoolean("datas",false))
            downloadFirstDatas();


        final Toolbar toolbar=(Toolbar) findViewById(R.id.toolbarMain);

        NavigationView navigationView= (NavigationView) findViewById(R.id.naviView);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawerLayout);
        View view= navigationView.getHeaderView(0);
        final CircleImageView imageView1= (CircleImageView) view.findViewById(R.id.profilePicture);
        TextView name= (TextView) view.findViewById(R.id.nameHeader);
        TextView email= (TextView) view.findViewById(R.id.emailHeader);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,(DrawerLayout) findViewById(R.id.drawerLayout),toolbar,R.string.Open,R.string.Close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        fab= (FloatingActionButton) findViewById(R.id.mainFab);
        name.setText(getSharedPreferences("loginInfo",MODE_PRIVATE).getString("FullName",""));
        email.setText(getSharedPreferences("loginInfo",MODE_PRIVATE).getString("email",""));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle("Home");

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //todo remove
        pref.edit().putBoolean("admin",true).apply();

        //checkAdmin
        navigationView.getMenu().getItem(7).setVisible(pref.getBoolean("admin",false));

        manager = getSupportFragmentManager();
        previous=R.id.newsEvent;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id= item.getItemId();
                if(id==previous)
                    return  true;
                drawerLayout.closeDrawer(findViewById(R.id.naviView));
                fab.setVisibility(View.GONE);
                switch (id){
                    case R.id.newsEvent:
                        setTitle("Home");
                        item.setChecked(true);
                        previous=id;
                        manager.beginTransaction().replace(R.id.fragHolder,new NewsEvents()).commit();
                        break;
                    case R.id.TUNotices:
                        setTitle("TU Notices");
                        item.setChecked(true);
                        previous=id;
                        manager.beginTransaction().replace(R.id.fragHolder,new TuNotices()).commit();
                        break;
                    case R.id.elibrary:
                        setTitle("E-Library");
                        item.setChecked(true);
                        previous=id;
                        manager.beginTransaction().replace(R.id.fragHolder,new eLibrary()).commit();
                        break;
                    case R.id.projects:
                        setTitle("Projects");
                        item.setChecked(true);
                        previous=id;
                        manager.beginTransaction().replace(R.id.fragHolder,new Projects()).commit();
                        break;
                    case R.id.community:
                        setTitle("Communities");
                        item.setChecked(true);
                        previous=id;
                        manager.beginTransaction().replace(R.id.fragHolder,new Community()).commit();
                        break;
                    case R.id.fourm:
                        setTitle("Forum");
                        item.setChecked(true);
                        previous=id;
                        manager.beginTransaction().replace(R.id.fragHolder,new Forum()).commit();
                        break;

                    case R.id.setting:
                        startActivity(new Intent(MainActivity.this,Settings.class));
                        break;
                    case R.id.adminPanel:
                        startActivity(new Intent(MainActivity.this,AdminPanel.class));
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this,AboutUs.class));
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
                return true;
            }
        });
        Picasso.with(this).load("https://graph.facebook.com/"+getSharedPreferences("loginInfo",MODE_PRIVATE).getString("UserID","")+"/picture?type=large").into(imageView1);
        fab.setVisibility(View.GONE);
        manager.beginTransaction().replace(R.id.fragHolder,new NewsEvents()).commit();
    }

    private void downloadFirstDatas() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
               drawerLayout.openDrawer(findViewById(R.id.naviView));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}