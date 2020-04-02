package com.msc.mscdictionary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class OffFavouriteDAO {
    public static final String FAVOURITE_TABLE = "favourite";
    DataHelper dataHelper;
    SQLiteDatabase db;

    public OffFavouriteDAO(Context context){
        this.dataHelper = new DataHelper(context);
    }

    public void createTableFavourite(){
        db = dataHelper.getReadableDatabase();
        String s = "create table " + FAVOURITE_TABLE +"(" +
                "_id integer PRIMARY KEY," +
                "en text)";
        db.rawQuery(s, null);
    }
}
