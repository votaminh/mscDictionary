package com.msc.mscdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class OffHistoryDAO {
    public static final String HISTORY_TABLE = "history";
    DataHelper dataHelper;
    SQLiteDatabase db;
    Context context;

    public OffHistoryDAO(Context context){
        this.context = context;
        dataHelper = new DataHelper(context);
    }

    public void add(Word currentWord) {
        db = dataHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("en",currentWord.getId());
        db.insert(HISTORY_TABLE,null,values);
    }

    public List<Word> getAllHistory() {
        List<Word> list = new ArrayList<>();
        db = dataHelper.getReadableDatabase();
        String s = "select * from " + HISTORY_TABLE;
        Cursor cursor = db.rawQuery(s, null);

        OffWordDAO wordDAO = new OffWordDAO(context);

        if(cursor.moveToFirst()){
            do {
                int id = cursor.getInt(1);
                Word word = wordDAO.getWordById(id);
                list.add(word);
            }
            while (cursor.moveToNext());
        }else {

        }
        return list;
    }

    public int getIdWord(int id) {
        db = dataHelper.getReadableDatabase();
        String s = "select * from " + HISTORY_TABLE + " where en = '" + id + "'";
        Cursor cursor = db.rawQuery(s, null);
        if(cursor.moveToFirst()){
            return cursor.getInt(0);
        }else {
            return 0;
        }
    }

    public int getEnById(int i) {
        db = dataHelper.getReadableDatabase();
        String s = "select * from " + HISTORY_TABLE + " where _id = '" + i + "'";
        Cursor cursor = db.rawQuery(s, null);
        if(cursor.moveToFirst()){
            return cursor.getInt(1);
        }else {
            return 0;
        }
    }
}
