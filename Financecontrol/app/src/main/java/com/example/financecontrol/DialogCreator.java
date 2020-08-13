package com.example.financecontrol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.financecontrol.ui.home.HomeFragment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class DialogCreator {

    private int[] titles;
    private int[] formNames;
    private TextView[] textViews;
    Data data;
    DecimalFormat decimalFormat;
    private Context context;

    public  DialogCreator(int[] titles, int[] formNames, TextView[] textViews, Data data, DecimalFormat decimalFormat, Context context) {
        this.titles = titles;
        this.formNames = formNames;
        this.textViews = textViews;
        this.data = data;
        this.decimalFormat = decimalFormat;
        this.context = context;
    }

    public Dialog createDialog(int id) {
        final int formId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titles[id]);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setView(inflater.inflate(R.layout.amount_transaction_dialog, null))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBHelper dbHelper = new DBHelper(context);
                        SQLiteDatabase transactionsDb = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        Dialog dialog = (Dialog) dialogInterface;
                        EditText descriptionEditText = (EditText) dialog.findViewById(R.id.descriptionEditText);
                        EditText amountEditText = (EditText) dialog.findViewById(R.id.amountEditText);
                        if (amountEditText.getText().toString().equals("") || descriptionEditText.getText().toString().equals("")) {
                            Toast.makeText(context, R.string.null_edit_text_error, Toast.LENGTH_LONG).show();
                        }
                        else if(notExpectedSumFormat(new BigDecimal(amountEditText.getText().toString()))) {
                            Toast.makeText(context, R.string.wrong_money_sum, Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(formId == HomeFragment.AMOUNT_ID) {
                                data.setValue(HomeFragment.AMOUNT_ID, data.getValue(HomeFragment.AMOUNT_ID).add(new BigDecimal(amountEditText.getText().toString())));
                                textViews[HomeFragment.AMOUNT_ID].setText(String.valueOf(decimalFormat.format(data.getValue(HomeFragment.AMOUNT_ID))));
                                data.saveData(HomeFragment.AMOUNT_ID);
                                contentValues.put(DBHelper.KEY_OPERATION_AMOUNT, amountEditText.getText().toString());
                                contentValues.put(DBHelper.KEY_OPERATION_DESCRIPTION, descriptionEditText.getText().toString());
                                contentValues.put(DBHelper.KEY_DATE, System.currentTimeMillis() / 1000L);
                                contentValues.put(DBHelper.KEY_BALANCE, data.getValue(HomeFragment.AMOUNT_ID).toString());
                                transactionsDb.insert(DBHelper.TABLE_NAME, null, contentValues);
                            }
                            else {
                                if(data.getValue(HomeFragment.AMOUNT_ID).compareTo(new BigDecimal(amountEditText.getText().toString())) == -1) {
                                    Toast.makeText(context, R.string.lack_of_money_error, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    data.setValue(formId, data.getValue(formId).add(new BigDecimal(amountEditText.getText().toString())));
                                    data.setValue(HomeFragment.AMOUNT_ID, data.getValue(HomeFragment.AMOUNT_ID).subtract(new BigDecimal(amountEditText.getText().toString())));
                                    textViews[formId].setText(context.getString(formNames[formId], decimalFormat.format(data.getValue(formId))));
                                    textViews[HomeFragment.AMOUNT_ID].setText(String.valueOf(decimalFormat.format(data.getValue(HomeFragment.AMOUNT_ID))));
                                    data.saveData(formId);
                                    data.saveData(HomeFragment.AMOUNT_ID);
                                    contentValues.put(DBHelper.KEY_OPERATION_AMOUNT, "-" + amountEditText.getText().toString());
                                    contentValues.put(DBHelper.KEY_OPERATION_DESCRIPTION, descriptionEditText.getText().toString());
                                    contentValues.put(DBHelper.KEY_DATE, System.currentTimeMillis() / 1000L);
                                    contentValues.put(DBHelper.KEY_BALANCE, data.getValue(HomeFragment.AMOUNT_ID).toString());
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

    private boolean notExpectedSumFormat(BigDecimal sum) {
        BigInteger bigInteger = sum.divide(BigDecimal.valueOf(0.01)).toBigInteger();
        if(bigInteger.mod(BigInteger.valueOf(5)).compareTo(BigInteger.valueOf(0)) == 0) {
            return bigInteger.mod(BigInteger.valueOf(100)).compareTo(BigInteger.valueOf(5)) == 0 || bigInteger.mod(BigInteger.valueOf(100)).compareTo(BigInteger.valueOf(15)) == 0;
        }
        return true;
    }

}
