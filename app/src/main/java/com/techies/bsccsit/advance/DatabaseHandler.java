package com.techies.bsccsit.advance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int version=7;
    private static final String name="bsccsitDB";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS eLibrary");

        db.execSQL("DROP TABLE IF EXISTS popularCommunities");

        db.execSQL("DROP TABLE IF EXISTS myCommunities");

        db.execSQL("DROP TABLE IF EXISTS news");

        db.execSQL("DROP TABLE IF EXISTS events");

        db.execSQL("CREATE TABLE eLibrary(Title TEXT,Source TEXT,Tag TEXT,Link TEXT,LinkLink TEXT);");

        db.execSQL("CREATE TABLE popularCommunities(FbID TEXT,Title TEXT,ExtraText TEXT);");

        db.execSQL("CREATE TABLE myCommunities(FbID TEXT,Title TEXT,ExtraText TEXT);");

        db.execSQL("CREATE TABLE news(names TEXT,posterId TEXT,fullImage TEXT,message TEXT,created_time TEXT);");

        db.execSQL("CREATE TABLE events(names TEXT,created_time TEXT,eventIDs TEXT,hosters TEXT,fullImage TEXT);");
    }
}
