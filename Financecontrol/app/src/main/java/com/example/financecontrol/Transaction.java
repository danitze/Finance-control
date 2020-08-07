package com.example.financecontrol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private String amount;
    private String description;
    private int date;
    private String balance;

    public Transaction(String amount, String description, int date, String balance) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.balance = balance;
    }

    public static List<Transaction> getTransactions(DBHelper dbHelper) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase transactionsDb = dbHelper.getReadableDatabase();
        Cursor cursor = transactionsDb.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            int amountIndex = cursor.getColumnIndex(DBHelper.KEY_OPERATION_AMOUNT);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_OPERATION_DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int balanceIndex = cursor.getColumnIndex(DBHelper.KEY_BALANCE);

            do {
                transactions.add(new Transaction(cursor.getString(amountIndex), cursor.getString(descriptionIndex), cursor.getInt(dateIndex), cursor.getString(balanceIndex)));
            } while (cursor.moveToNext());
        }
        return transactions;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
