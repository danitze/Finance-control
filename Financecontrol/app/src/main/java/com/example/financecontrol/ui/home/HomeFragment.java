package com.example.financecontrol.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.financecontrol.DBHelper;
import com.example.financecontrol.FileOperations;
import com.example.financecontrol.MainActivity;
import com.example.financecontrol.R;

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

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public final static int numberOfForms = 6;

    final int AMOUNT_ID = 0;
    final int TRANSPORT_ID = 1;
    final int NUTRITION_ID = 2;
    final int PURCHASES_ID = 3;
    final int RECREATION_ID = 4;
    final int REAL_ESTATE_ID = 5;
    final int[] titles = new int[] {R.string.add_amount, R.string.spend_on_transport, R.string.spend_on_nutrition, R.string.spend_on_purchases, R.string.spend_on_recreation, R.string.spend_on_real_estate};
    final int[] formNames = new int[] {R.string.amount, R.string.transport, R.string.nutrition, R.string.purchases, R.string.recreation, R.string.real_estate};
    BigDecimal[] values = new BigDecimal[numberOfForms];
    TextView[] textViews;

    DBHelper dbHelper;

    FileOperations fileOperations;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        dbHelper = new DBHelper(this.getActivity());
        textViews = new TextView[] {root.findViewById(R.id.amountText), root.findViewById(R.id.transportText), root.findViewById(R.id.nutritionText), root.findViewById(R.id.purchasesText), root.findViewById(R.id.recreationText), root.findViewById(R.id.realEstateText)};
        fileOperations = new FileOperations(getContext());
        setScreenNumbers();
        return root;
    }

    @Override
    public void onClick(View view) {
        Dialog dialog = null;
        switch (view.getId()) {
            case R.id.amountText:
                dialog = createDialog(AMOUNT_ID);
                break;
            case R.id.transportText:
                dialog = createDialog(TRANSPORT_ID);
                break;
            case R.id.nutritionText:
                dialog = createDialog(NUTRITION_ID);
                break;
            case R.id.purchasesText:
                dialog = createDialog(PURCHASES_ID);
                break;
            case R.id.recreationText:
                dialog = createDialog(RECREATION_ID);
                break;
            case R.id.realEstateText:
                dialog = createDialog(REAL_ESTATE_ID);
                break;
        }
        dialog.show();
    }

    public Dialog createDialog(int id) {
        final int formId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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
                            Toast.makeText(getActivity(), R.string.null_edit_text_error, Toast.LENGTH_LONG).show();
                        }
                        else if(notExpectedSumFormat(new BigDecimal(amountEditText.getText().toString()))) {
                            Toast.makeText(getActivity(), R.string.wrong_money_sum, Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(formId == AMOUNT_ID) {
                                values[AMOUNT_ID] = values[AMOUNT_ID].add(new BigDecimal(amountEditText.getText().toString()));
                                textViews[AMOUNT_ID].setText(String.valueOf(decimalFormat.format(values[AMOUNT_ID])));
                                fileOperations.saveData(AMOUNT_ID, getContext());
                                contentValues.put(DBHelper.KEY_OPERATION_AMOUNT, amountEditText.getText().toString());
                                contentValues.put(DBHelper.KEY_OPERATION_DESCRIPTION, descriptionEditText.getText().toString());
                                contentValues.put(DBHelper.KEY_DATE, System.currentTimeMillis() / 1000L);
                                contentValues.put(DBHelper.KEY_BALANCE, values[AMOUNT_ID].toString());
                                transactionsDb.insert(DBHelper.TABLE_NAME, null, contentValues);
                            }
                            else {
                                if(values[AMOUNT_ID].compareTo(new BigDecimal(amountEditText.getText().toString())) == -1) {
                                    Toast.makeText(getActivity(), R.string.lack_of_money_error, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    values[formId] = values[formId].add(new BigDecimal(amountEditText.getText().toString()));
                                    values[AMOUNT_ID] = values[AMOUNT_ID].subtract(new BigDecimal(amountEditText.getText().toString()));
                                    textViews[formId].setText(getString(formNames[formId], decimalFormat.format(values[formId])));
                                    textViews[AMOUNT_ID].setText(String.valueOf(decimalFormat.format(values[AMOUNT_ID])));
                                    fileOperations.saveData(formId, getContext());
                                    fileOperations.saveData(AMOUNT_ID, getContext());
                                    contentValues.put(DBHelper.KEY_OPERATION_AMOUNT, "-" + amountEditText.getText().toString());
                                    contentValues.put(DBHelper.KEY_OPERATION_DESCRIPTION, descriptionEditText.getText().toString());
                                    contentValues.put(DBHelper.KEY_DATE, System.currentTimeMillis() / 1000L);
                                    contentValues.put(DBHelper.KEY_BALANCE, values[AMOUNT_ID].toString());
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

    public void setScreenNumbers() {
        values = fileOperations.getValues();
        for(int i = 0; i < numberOfForms; i++) {
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
}