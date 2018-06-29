package com.example.dummynote4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DB {

    private static final String KEY_ROWID = "_id";
    private static final String KEY_NAME = "機構名稱";
    private static final String KEY_CITY = "市";
    private static final String KEY_DISTRICT = "區";
    private static final String KEY_ADDRESS = "地址";
    private static final String KEY_TELL = "電話";
    private static final String KEY_DIVISION = "診療科別";
    private static final String DATABASE_NAME = "mydb.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Dental_Clinics";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "(_id INTEGER PRIMARY KEY, 機構名稱 TEXT, 市 TEXT, 區 TEXT, 地址 TEXT, 電話 TEXT, 診療科別 TEXT);";

    private Context mCtx;
    private DataBaseOpenHelper helper;
    private SQLiteDatabase db;

    public DB(Context context) {
        mCtx = context;
    }

    public DB open() {
        helper = new DataBaseOpenHelper(mCtx);
        db = helper.getReadableDatabase();
        return this;
    }

    private class DataBaseOpenHelper extends SQLiteOpenHelper {

        public DataBaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }


    String[] strCols = {KEY_ROWID, KEY_NAME, KEY_CITY, KEY_DISTRICT, KEY_ADDRESS, KEY_TELL, KEY_DIVISION};

    public Cursor getAll() {
        return db.query(TABLE_NAME, strCols, null, null, null, null, null);
    }

    public Cursor get(long rowId) throws SQLException {
        Cursor mCursor = db.query(true,
                TABLE_NAME,
                strCols,
                KEY_ROWID + "=" + rowId,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public long create(String noteName) {
        Date now = new Date();
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, noteName);
        args.put(KEY_ADDRESS, now.getTime()); //尚未完成
        return db.insert(TABLE_NAME, null, args);
    }

    public boolean delete(long rowId) {
        return db.delete(TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean update(long rowId, String noteName) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, noteName);
        return db.update(TABLE_NAME, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
