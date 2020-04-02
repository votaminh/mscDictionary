package com.msc.mscdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.Constant;

public class OffHistoryDAO {
    public static final String HISTORY_TABLE = "history";
    DataHelper dataHelper;
    SQLiteDatabase db;

    public OffHistoryDAO(Context context){
        dataHelper = new DataHelper(context);
    }

    public void createTableHistory(){

    }

    public void add(Word currentWord) {
        db = dataHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("en",currentWord.getId());
        db.insert(HISTORY_TABLE,null,values);
    }
}
