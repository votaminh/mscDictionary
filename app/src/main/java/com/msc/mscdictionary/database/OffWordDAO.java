package com.msc.mscdictionary.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;

import java.net.URLDecoder;
import java.util.ArrayList;

public class OffWordDAO {
    DataHelper dataHelper;
    private SQLiteDatabase db;

    public OffWordDAO(Context context){
        dataHelper = new DataHelper(context);
    }
    public void getWordByEn(String en, DictionaryCrawl.TranslateCallback callback){
        new Thread(() -> {
            db = dataHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select*from word where en = '" + en + "'",null);
            if(cursor.moveToFirst()){
                int id = cursor.getInt(0);
                String enText = URLDecoder.decode(cursor.getString(1));
                String html = URLDecoder.decode(cursor.getString(2));
                String vi = URLDecoder.decode(cursor.getString(3));
                String voice = URLDecoder.decode(cursor.getString(4));
                String audio = URLDecoder.decode(cursor.getString(5));
                Word word = new Word(id, enText, html, vi, voice, audio);
                callback.success(word);
            }else {
                callback.fail("No word was found");
            }
        }).start();
    }
}
