package com.example.adaptivepage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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
    int[] values = new int[numberOfForms];
    TextView[] textViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViews = new TextView[] {findViewById(R.id.amountText), findViewById(R.id.transportText), findViewById(R.id.nutritionText), findViewById(R.id.purchasesText), findViewById(R.id.recreationText), findViewById(R.id.realEstateText)};
        setScreenNumbers();
    }

    @Override
    public void onClick(View view) {
        Dialog dialog = null;
        switch (view.getId()) {
            case R.id.amountText:
                dialog = onCreateDialog(AMOUNT_ID);
                break;
            case R.id.transportText:
                dialog = onCreateDialog(TRANSPORT_ID);
                break;
            case R.id.nutritionText:
                dialog = onCreateDialog(NUTRITION_ID);
                break;
            case R.id.purchasesText:
                dialog = onCreateDialog(PURCHASES_ID);
                break;
            case R.id.recreationText:
                dialog = onCreateDialog(RECREATION_ID);
                break;
            case R.id.realEstateText:
                dialog = onCreateDialog(REAL_ESTATE_ID);
                break;
        }
        dialog.show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titles[id]);
        LayoutInflater inflater = this.getLayoutInflater();
        if(id == AMOUNT_ID) {
            builder.setView(inflater.inflate(R.layout.amount_add_dialog, null))
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Dialog dialog = (Dialog) dialogInterface;
                            EditText addAmountEditText = (EditText) dialog.findViewById(R.id.spendAmountEditText);
                            if ((addAmountEditText.getText() == null)) {
                                Toast.makeText(MainActivity.this, R.string.null_edit_text_error, Toast.LENGTH_LONG).show();
                            } else {
                                values[AMOUNT_ID] += Integer.parseInt(addAmountEditText.getText().toString());
                                textViews[AMOUNT_ID].setText(String.valueOf(values[AMOUNT_ID]));
                                saveData(AMOUNT_ID);
                            }
                        }
                    });
        }
        else {
            final int formId = id;
            builder.setView(inflater.inflate(R.layout.amount_spend_dialog, null))
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Dialog dialog = (Dialog) dialogInterface;
                            EditText spendAmountEditText = dialog.findViewById(R.id.spendAmountEditText);
                            if(spendAmountEditText.getText() == null) {
                                Toast.makeText(MainActivity.this, R.string.null_edit_text_error, Toast.LENGTH_LONG).show();
                            }
                            else if(values[AMOUNT_ID] < Integer.parseInt(spendAmountEditText.getText().toString())) {
                                Toast.makeText(MainActivity.this, R.string.lack_of_money_error, Toast.LENGTH_LONG).show();
                            }
                            else {
                                values[formId] += Integer.parseInt(spendAmountEditText.getText().toString());
                                values[AMOUNT_ID] -= values[formId];
                                textViews[formId].setText(getString(formNames[formId], values[formId]));
                                textViews[AMOUNT_ID].setText(String.valueOf(values[AMOUNT_ID]));
                            }
                        }
                    });
        }
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
            bufferedWriter.write(values[id]);
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
            values[id] = bufferedReader.read();
        } catch (FileNotFoundException e) {
            new File(fileNames[id]);
            values[id] = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setScreenNumbers() {
        for(int i = 0; i < numberOfForms; i++) {
            getData(i);
            textViews[i].setText(getString(formNames[i], values[i]));
            textViews[i].setOnClickListener(this);
        }
    }
}