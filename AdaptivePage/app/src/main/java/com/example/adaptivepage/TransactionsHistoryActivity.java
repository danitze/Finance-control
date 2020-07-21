package com.example.adaptivepage;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TransactionsHistoryActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    TextView textView;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linearLayout);
        dbHelper = new DBHelper(this);
        showTransactions();
    }

    private void showTransactions() {
        SQLiteDatabase transactionsDb = dbHelper.getReadableDatabase();
        Cursor cursor = transactionsDb.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        String string;
        TextView textView;
        if(cursor.moveToFirst()) {
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int amountIndex = cursor.getColumnIndex(DBHelper.KEY_OPERATION_AMOUNT);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_OPERATION_DESCRIPTION);
            do {
                textView = new TextView(this);
                textView.setTextSize(25);
                string = dateFormat(cursor.getInt(dateIndex)) + "                   " + cursor.getString(amountIndex) + " " + cursor.getString(descriptionIndex);
                textView.setText(string);
                this.linearLayout.addView(textView);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private String dateFormat(int unixTime) {
        Date date = new Date(unixTime * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }
}