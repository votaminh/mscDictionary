package com.msc.mscdictionary.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    public static String DB_NAME ="dictionary.db"; // Database name
    private static int DB_VERSION = 1; // Database version
    private final File DB_FILE;
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DB_FILE = context.getDatabasePath(DB_NAME);
        this.mContext = context;
    }

    public void createDatabase(){
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }

            if(openDataBase()){
                String f = "create table " + OffFavouriteDAO.FAVOURITE_TABLE +"(" +
                        "_id integer PRIMARY KEY," +
                        "en text)";

                String h = "create table " + OffHistoryDAO.HISTORY_TABLE +"(" +
                        "_id integer PRIMARY KEY," +
                        "en text," +
                        "date text)";
                mDataBase.execSQL(h);
                mDataBase.execSQL(f);
            }
        }
    }

    // Check that the database file exists in databases folder
    private boolean checkDataBase() {
        return DB_FILE.exists();
    }

    // Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.openFileInput(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_FILE);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // Open the database, so we can query it
    public boolean openDataBase() throws SQLException {
        // Log.v("DB_PATH", DB_FILE.getAbsolutePath());
        mDataBase = SQLiteDatabase.openDatabase(String.valueOf(DB_FILE), null, SQLiteDatabase.CREATE_IF_NECESSARY);
        // mDataBase = SQLiteDatabase.openDatabase(DB_FILE, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}