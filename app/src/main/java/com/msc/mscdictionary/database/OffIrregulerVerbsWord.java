package com.msc.mscdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.msc.mscdictionary.model.IrregulerVerbsWord;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;

import java.net.URLDecoder;
import java.util.ArrayList;

public class OffIrregulerVerbsWord {
    private static final String IRREGULER_TABLE = "irreguler";
    DataHelper dataHelper;
    private SQLiteDatabase db;
    String en;

    public OffIrregulerVerbsWord(Context context){
        dataHelper = new DataHelper(context);
    }

    public IrregulerVerbsWord getWordByEn(String en){
        this.en = en.toLowerCase();
        db = dataHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select*from " + IRREGULER_TABLE + " where v1 = '" + this.en + "'",null);
        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            String v1 = URLDecoder.decode(cursor.getString(1));
            String v2 = URLDecoder.decode(cursor.getString(2));
            String v3 = URLDecoder.decode(cursor.getString(3));

            IrregulerVerbsWord irregulerVerbsWord = new IrregulerVerbsWord(v1, v2, v3);
            irregulerVerbsWord.id = id;
            return irregulerVerbsWord;
        }else {
            return null;
        }
    }

    public interface TaskIrregulerListener{
        void success(IrregulerVerbsWord irregulerVerbsWord);
        void fail();
    }
}
