package com.msc.mscdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;

import java.net.URLDecoder;
import java.util.ArrayList;

public class OffWordDAO {
    private static final String WORD_TABLE = "word";
    DataHelper dataHelper;
    private SQLiteDatabase db;
    String en;

    public OffWordDAO(Context context){
        dataHelper = new DataHelper(context);
    }

    public void getWordByEn(String en, DictionaryCrawl.TranslateCallback callback){
        this.en = en.toLowerCase();
        new Thread(() -> {
            db = dataHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select*from word where en = '" + this.en + "'",null);
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

    public Word getWordById(int id) {
        db = dataHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select*from word where idWord = '" + id + "'",null);
        if(cursor.moveToFirst()){
            String enText = URLDecoder.decode(cursor.getString(1));
            String html = URLDecoder.decode(cursor.getString(2));
            String vi = URLDecoder.decode(cursor.getString(3));
            String voice = URLDecoder.decode(cursor.getString(4));
            String audio = URLDecoder.decode(cursor.getString(5));
            Word word = new Word(id, enText, html, vi, voice, audio);
            return word;
        }else {
            return null;
        }
    }

    public int getBiggestId() {
        db = dataHelper.getReadableDatabase();
        Cursor c = db.query(WORD_TABLE, null, null, null, null, null, "idWord"+" DESC");
        if(c.moveToFirst()){
            int id = c.getInt(0);
            return id;
        }
        return 0;
    }

    public void addWord(Word word) {
        db = dataHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idWord",word.getId());
        values.put("en",word.getEnWord());
        values.put("htmlMean",word.getHtmlFullMean());
        values.put("commonMean",word.getCommonMean());
        values.put("voice",word.getVoice());
        values.put("urlSpeak",word.getUrlSpeak());
        db.insert(WORD_TABLE,null,values);
    }
}
