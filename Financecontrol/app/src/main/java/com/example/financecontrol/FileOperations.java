package com.example.financecontrol;

import android.content.Context;

import com.example.financecontrol.ui.home.HomeFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

public class FileOperations {
    private static String[] FILE_NAMES = new String[] {"Amount", "Transport", "Nutrition", "Purchases", "Recreation", "Real_estate"};
    private BigDecimal[] values;

    public FileOperations(Context context) {
        values = new BigDecimal[HomeFragment.numberOfForms];
        for (int i = 0; i < HomeFragment.numberOfForms; i++) {
            getData(i, context);
        }
    }

    public BigDecimal[] getValues() {
        return values;
    }

    public void setValues(BigDecimal[] values) {
        this.values = values;
    }

    public void saveData(int id, Context context) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE_NAMES[id], Context.MODE_PRIVATE)));
            bufferedWriter.write(values[id].toString());
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            new File(FILE_NAMES[id]);
            saveData(id, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getData(int id, Context context) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAMES[id])));
            values[id] = new BigDecimal(bufferedReader.readLine());
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            new File(FILE_NAMES[id]);
            values[id] = new BigDecimal(0);
            saveData(id, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
