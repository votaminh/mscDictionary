package com.msc.mscdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.msc.mscdictionary.model.Word;

import java.util.ArrayList;
import java.util.List;

public class OffFavouriteDAO {
    public static final String FAVOURITE_TABLE = "favourite";
    DataHelper dataHelper;
    SQLiteDatabase db;
    Context context;

    public OffFavouriteDAO(Context context){
        this.context = context;
        this.dataHelper = new DataHelper(context);
    }

    public void createTableFavourite(){
        db = dataHelper.getReadableDatabase();
        String s = "create table " + FAVOURITE_TABLE +"(" +
                "_id integer PRIMARY KEY," +
                "en text)";
        db.rawQuery(s, null);
    }

    public void add(Word currentWord) {
        db = dataHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("en",currentWord.getId());
        db.insert(FAVOURITE_TABLE,null,values);
    }

    public void remove(Word currentWord) {
        db = dataHelper.getWritableDatabase();
        db.delete(FAVOURITE_TABLE, "en = ?", new String[]{String.valueOf(currentWord.getId())});
    }

    public boolean checkHas(Word word){
        db = dataHelper.getReadableDatabase();
        String s = "select * from " + FAVOURITE_TABLE + " where en = '" + word.getId() + "'";
        Cursor cursor = db.rawQuery(s, null);
        if(cursor.moveToFirst()){
            return true;
        }else {
            return false;
        }
    }

    public List<Word> getAllWordFavourite() {
        List<Word> list = new ArrayList<>();
        db = dataHelper.getReadableDatabase();
        String s = "select * from " + FAVOURITE_TABLE;
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
}
