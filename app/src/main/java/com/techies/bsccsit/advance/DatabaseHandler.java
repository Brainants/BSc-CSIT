package com.techies.bsccsit.advance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int version=1;
    private static final String name="bsccsitDB";

    public DatabaseHandler(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE eLibrary(Title TEXT,Source TEXT,Tag TEXT,Link TEXT,LinkLink TEXT);");

        db.execSQL("CREATE TABLE popularCommunities(FbID TEXT,Title TEXT,IsVerified INT,ImageLink TEXT,ExtraText TEXT);");

        db.execSQL("CREATE TABLE myCommunities(FbID TEXT,Title TEXT,IsVerified INT,ImageLink TEXT,ExtraText TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
