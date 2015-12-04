package com.techies.bsccsit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Profile;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.fragments.AboutUs;
import com.techies.bsccsit.fragments.Community;
import com.techies.bsccsit.fragments.Forum;
import com.techies.bsccsit.fragments.NewsEvents;
import com.techies.bsccsit.fragments.Projects;
import com.techies.bsccsit.fragments.eLibrary;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private FragmentManager manager;
    private int previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Login chaina vane login activity ma lanchha
        if (!getSharedPreferences("loginInfo",MODE_PRIVATE).getBoolean("loggedIn",false)){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

        final Toolbar toolbar=(Toolbar) findViewById(R.id.toolbarMain);
        NavigationView navigationView= (NavigationView) findViewById(R.id.naviView);
        navigationView.getHeaderCount();
        final DrawerLayout drawerLayout= (DrawerLayout) findViewById(R.id.drawerLayout);

        setSupportActionBar(toolbar);
        setTitle("Home");

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

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        View view= navigationView.getHeaderView(0);
        final CircleImageView imageView1= (CircleImageView) view.findViewById(R.id.profilePicture);
        manager = getSupportFragmentManager();
        previous=R.id.newsEvent;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id= item.getItemId();
                if(id==previous)
                    return  true;
                previous=id;
                if(id!=R.id.rate_us)
                    item.setChecked(true);
                switch (id){
                    case R.id.newsEvent:
                        setTitle("Home");
                        manager.beginTransaction().add(R.id.fragHolder,new NewsEvents()).commit();
                        break;
                    case R.id.elibrary:
                        setTitle("E-Library");
                        manager.beginTransaction().add(R.id.fragHolder,new eLibrary()).commit();
                        break;
                    case R.id.projects:
                        setTitle("Projects");
                        manager.beginTransaction().add(R.id.fragHolder,new Projects()).commit();
                        break;
                    case R.id.community:
                        setTitle("Communities");
                        manager.beginTransaction().add(R.id.fragHolder,new Community()).commit();
                        break;
                    case R.id.fourm:
                        setTitle("Forum");
                        manager.beginTransaction().add(R.id.fragHolder,new Forum()).commit();
                        break;
                    case R.id.setting:
                        startActivity(new Intent(MainActivity.this,Settings.class));
                        break;
                    case R.id.about:
                        setTitle("About Us");
                        manager.beginTransaction().add(R.id.fragHolder,new AboutUs()).commit();
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
                drawerLayout.closeDrawer(findViewById(R.id.naviView));
                return true;
            }
        });
        Picasso.with(this).load("https://graph.facebook.com/"+getSharedPreferences("loginInfo",MODE_PRIVATE).getString("UserID","")+"/picture?type=large").into(imageView1);

        manager.beginTransaction().add(R.id.fragHolder,new NewsEvents()).commit();
    }
}