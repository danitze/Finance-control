package com.example.adaptivepage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    final int numberOfForms = 6;

    final int AMOUNT_ID = 0;
    final int TRANSPORT_ID = 1;
    final int NUTRITION_ID = 2;
    final int PURCHASES_ID = 3;
    final int RECREATION_ID = 4;
    final int REAL_ESTATE_ID = 5;

    final String[] fileNames = new String[] {"Amount", "Transport", "Nutrition", "Purchases", "Recreation", "Real_estate"};
    final int[] titles = new int[] {R.string.add_amount, R.string.spend_on_transport, R.string.spend_on_nutrition, R.string.spend_on_purchases, R.string.spend_on_recreation, R.string.spend_on_real_estate};
    final int[] formNames = new int[] {R.string.amount, R.string.transport, R.string.nutrition, R.string.purchases, R.string.recreation, R.string.real_estate};
    BigDecimal[] values = new BigDecimal[numberOfForms];
    TextView[] textViews;

    DBHelper dbHelper;

    Button transactionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        transactionsButton = (Button) findViewById(R.id.transactionsButton);
        transactionsButton.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        textViews = new TextView[] {findViewById(R.id.amountText), findViewById(R.id.transportText), findViewById(R.id.nutritionText), findViewById(R.id.purchasesText), findViewById(R.id.recreationText), findViewById(R.id.realEstateText)};
        setScreenNumbers();
        System.out.println(getFilesDir());
    }

    @Override
    public void onClick(View view) {
        Dialog dialog = null;
        switch (view.getId()) {
            case R.id.amountText:
                dialog = onCreateDialog(AMOUNT_ID);
                dialog.show();
                break;
            case R.id.transportText:
                dialog = onCreateDialog(TRANSPORT_ID);
                dialog.show();
                break;
            case R.id.nutritionText:
                dialog = onCreateDialog(NUTRITION_ID);
                dialog.show();
                break;
            case R.id.purchasesText:
                dialog = onCreateDialog(PURCHASES_ID);
                dialog.show();
                break;
            case R.id.recreationText:
                dialog = onCreateDialog(RECREATION_ID);
                dialog.show();
                break;
            case R.id.realEstateText:
                dialog = onCreateDialog(REAL_ESTATE_ID);
                dialog.show();
                break;
            case R.id.transactionsButton:
                Intent intent = new Intent(this, TransactionsHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final int formId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titles[id]);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.amount_transaction_dialog, null))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase transactionsDb = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        Dialog dialog = (Dialog) dialogInterface;
                        EditText descriptionEditText = (EditText) dialog.findViewById(R.id.descriptionEditText);
                        EditText amountEditText = (EditText) dialog.findViewById(R.id.amountEditText);
                        if (amountEditText.getText().toString().equals("") || descriptionEditText.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, R.string.null_edit_text_error, Toast.LENGTH_LONG).show();
                        }
                        else if(notExpectedSumFormat(new BigDecimal(amountEditText.getText().toString()))) {
                            Toast.makeText(MainActivity.this, R.string.wrong_money_sum, Toast.LENGTH_LONG).show();
                        }
                        else {
                           if(formId == AMOUNT_ID) {
                               values[AMOUNT_ID] = values[AMOUNT_ID].add(new BigDecimal(amountEditText.getText().toString()));
                               textViews[AMOUNT_ID].setText(String.valueOf(decimalFormat.format(values[AMOUNT_ID])));
                               saveData(AMOUNT_ID);
                               contentValues.put(DBHelper.KEY_DATE, System.currentTimeMillis() / 1000L);
                               contentValues.put(DBHelper.KEY_OPERATION_AMOUNT, amountEditText.getText().toString());
                               contentValues.put(DBHelper.KEY_OPERATION_DESCRIPTION, descriptionEditText.getText().toString());
                               transactionsDb.insert(DBHelper.TABLE_NAME, null, contentValues);
                           }
                           else {
                               if(values[AMOUNT_ID].compareTo(new BigDecimal(amountEditText.getText().toString())) == -1) {
                                   Toast.makeText(MainActivity.this, R.string.lack_of_money_error, Toast.LENGTH_LONG).show();
                               }
                               else {
                                   values[formId] = values[formId].add(new BigDecimal(amountEditText.getText().toString()));
                                   values[AMOUNT_ID] = values[AMOUNT_ID].subtract(new BigDecimal(amountEditText.getText().toString()));
                                   textViews[formId].setText(getString(formNames[formId], decimalFormat.format(values[formId])));
                                   textViews[AMOUNT_ID].setText(String.valueOf(decimalFormat.format(values[AMOUNT_ID])));
                                   saveData(formId);
                                   saveData(AMOUNT_ID);
                                   contentValues.put(DBHelper.KEY_DATE, System.currentTimeMillis() / 1000L);
                                   contentValues.put(DBHelper.KEY_OPERATION_AMOUNT, "-" + amountEditText.getText().toString());
                                   contentValues.put(DBHelper.KEY_OPERATION_DESCRIPTION, descriptionEditText.getText().toString());
                                   transactionsDb.insert(DBHelper.TABLE_NAME, null, contentValues);
                               }
                           }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    public void saveData(int id) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput(fileNames[id], MODE_PRIVATE)));
            bufferedWriter.write(String.valueOf(values[id]));
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            new File(fileNames[id]);
            saveData(id);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getData(int id) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(fileNames[id])));
            values[id] = new BigDecimal(bufferedReader.readLine());
        } catch (FileNotFoundException e) {
            new File(fileNames[id]);
            values[id] = new BigDecimal(0);
            saveData(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setScreenNumbers() {
        for(int i = 0; i < numberOfForms; i++) {
            getData(i);
            textViews[i].setText(getString(formNames[i], decimalFormat.format(values[i])));
            textViews[i].setOnClickListener(this);
        }
    }

    public boolean notExpectedSumFormat(BigDecimal sum) {
        BigInteger bigInteger = sum.divide(BigDecimal.valueOf(0.01)).toBigInteger();
        if(bigInteger.mod(BigInteger.valueOf(5)).compareTo(BigInteger.valueOf(0)) == 0) {
            return bigInteger.mod(BigInteger.valueOf(100)).compareTo(BigInteger.valueOf(5)) == 0 || bigInteger.mod(BigInteger.valueOf(100)).compareTo(BigInteger.valueOf(15)) == 0;
        }
        return true;
    }

    public void checkDb() {
        SQLiteDatabase transactionsDb = dbHelper.getWritableDatabase();
        Cursor cursor = transactionsDb.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int amountIndex = cursor.getColumnIndex(DBHelper.KEY_OPERATION_AMOUNT);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_OPERATION_DESCRIPTION);
            do {
                System.out.println(cursor.getInt(dateIndex) + " " + cursor.getString(amountIndex) + " " + cursor.getString(descriptionIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}