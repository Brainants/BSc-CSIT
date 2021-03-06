package com.brainants.bsccsit.advance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int version = 14;
    private static final String name = "bsccsitDB";

    public DatabaseHandler(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE eLibrary(Title TEXT,Source TEXT,Tag TEXT,Link TEXT,FileName TEXT);");

        db.execSQL("CREATE TABLE popularCommunities(FbID TEXT,Title TEXT,ExtraText TEXT);");

        db.execSQL("CREATE TABLE myCommunities(FbID TEXT,Title TEXT,ExtraText TEXT);");

        db.execSQL("CREATE TABLE news(names TEXT,posterId TEXT,fullImage TEXT,message TEXT,created_time TEXT);");

        db.execSQL("CREATE TABLE events(names TEXT,created_time TEXT,eventIDs TEXT,hosters TEXT,fullImage TEXT);");

        db.execSQL("CREATE TABLE projects(title TEXT,detail TEXT,tags TEXT,users TEXT,projectID TEXT);");

        db.execSQL("CREATE TABLE notices(id INT, title TEXT, short_desc TEXT, detail TEXT, date TEXT, attachment_link TEXT, attachment_title TEXT);");

        db.execSQL("CREATE TABLE tags(id INT, tag_name TEXT);");

        db.execSQL("CREATE TABLE notifications(title TEXT, desc TEXT, link TEXT, show INT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS eLibrary");

        db.execSQL("DROP TABLE IF EXISTS popularCommunities");

        db.execSQL("DROP TABLE IF EXISTS myCommunities");

        db.execSQL("DROP TABLE IF EXISTS news");

        db.execSQL("DROP TABLE IF EXISTS events");

        db.execSQL("DROP TABLE IF EXISTS projects");

        db.execSQL("DROP TABLE IF EXISTS notices");

        db.execSQL("DROP TABLE IF EXISTS tags");

        db.execSQL("DROP TABLE IF EXISTS notifications");

        onCreate(db);
    }
}
