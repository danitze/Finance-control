package com.example.financecontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "operationsDb";
    public static final String TABLE_NAME = "operations";

    public static final String KEY_DATE = "date";
    public static final String KEY_OPERATION_DESCRIPTION = "operationDescription";
    public static final String KEY_OPERATION_AMOUNT = "operationAmount";
    public static final String KEY_BALANCE = "balance";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (" + KEY_OPERATION_AMOUNT + " text," + KEY_OPERATION_DESCRIPTION + " real," + KEY_DATE
                + " integer primary key," + KEY_BALANCE + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
