package com.brainants.bsccsit.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.adapters.NotificationAdapter;
import com.brainants.bsccsit.advance.Singleton;
import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recy;
    private RobotoTextView noNotifications;

    ArrayList<String> title = new ArrayList<>(),
            desc = new ArrayList<>(),
            link = new ArrayList<>();

    ArrayList<Integer> show = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = (Toolbar) findViewById(R.id.notifToolbar);
        recy = (RecyclerView) findViewById(R.id.recyNotif);
        noNotifications = (RobotoTextView) findViewById(R.id.noNotifIcon);

        setSupportActionBar(toolbar);

        setTitle("Notifications");
        Singleton.setNotificationStatus(false);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadFromDb();

    }

    private void loadFromDb() {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM notifications", null);
        title.clear();
        desc.clear();
        show.clear();
        link.clear();
        int i = 0;
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("show")) == 1) {
                i++;
                title.add(cursor.getString(cursor.getColumnIndex("title")));
                desc.add(cursor.getString(cursor.getColumnIndex("desc")));
                show.add(cursor.getInt(cursor.getColumnIndex("show")));
                link.add(cursor.getString(cursor.getColumnIndex("link")));
            }
        }
        cursor.close();
        if (i == 0) {
            noNotifications.setVisibility(View.VISIBLE);
        } else {
            fillRecy();
        }
    }


    private void fillRecy() {
        recy.setVisibility(View.VISIBLE);
        final NotificationAdapter adapter = new NotificationAdapter(this, title, desc, link);
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coodNotif), "Notification deleted.", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.notifyItemInserted(viewHolder.getAdapterPosition());
                        ContentValues values = new ContentValues();
                        values.put("title", title.get(viewHolder.getAdapterPosition()));
                        values.put("desc", desc.get(viewHolder.getAdapterPosition()));
                        values.put("link", link.get(viewHolder.getAdapterPosition()));
                        values.put("show", show.get(viewHolder.getAdapterPosition()));
                        Singleton.getInstance().getDatabase().insert("notifications", null, values);
                    }
                });
                snackbar.show();
                Singleton.getInstance().getDatabase().rawQuery("DELETE FROM notifications WHERE title='" + title.get(viewHolder.getAdapterPosition()) + "'", null);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recy);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}
