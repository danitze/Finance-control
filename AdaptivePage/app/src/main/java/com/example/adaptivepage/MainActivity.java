package com.example.adaptivepage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
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
    final int AMOUNT_ID = 0;
    final int TRANSPORT_ID = 1;
    final int NUTRITION_ID = 2;
    final int PURCHASES_ID = 3;
    final int RECREATION_ID = 4;
    final int REAL_ESTATE_ID = 5;

    final String[] fileNames = new String[] {"Amount", "Transport", "Nutrition", "Purchases", "Recreation", "Real_estate"};
    int[] values = new int[6];
    TextView[] textViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViews = new TextView[] {findViewById(R.id.amountText), findViewById(R.id.transportText), findViewById(R.id.nutritionText), findViewById(R.id.purchasesText), findViewById(R.id.recreationText), findViewById(R.id.realEstateText)};
        textViews[AMOUNT_ID] = (TextView) findViewById(R.id.amountText);
        getData(AMOUNT_ID);
        textViews[AMOUNT_ID].setText(String.valueOf(values[AMOUNT_ID]));
        textViews[AMOUNT_ID].setOnClickListener(this);
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
        }
        dialog.show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_amount);
        LayoutInflater inflater = this.getLayoutInflater();
        switch (id) {
            case AMOUNT_ID:
                builder.setView(inflater.inflate(R.layout.amount_add_dialog, null))
                        .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Dialog dialog = (Dialog) dialogInterface;
                                EditText addAmountEditText = (EditText) dialog.findViewById(R.id.addAmountEditText);
                                if ((addAmountEditText.getText() == null)) {
                                    Toast.makeText(MainActivity.this, "Enter a number", Toast.LENGTH_LONG).show();
                                } else {
                                    values[AMOUNT_ID] += Integer.parseInt(addAmountEditText.getText().toString());
                                    textViews[AMOUNT_ID].setText(String.valueOf(values[AMOUNT_ID]));
                                    saveData(AMOUNT_ID);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                break;
            case TRANSPORT_ID:

        }
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
}