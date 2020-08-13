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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Data {
    private static String[] FILE_NAMES = new String[] {"Amount", "Transport", "Nutrition", "Purchases", "Recreation", "Real_estate"};
    private BigDecimal[] values;
    Context context;

    public Data(Context context) {
        this.context = context;
        values = new BigDecimal[HomeFragment.numberOfForms];
        for (int i = 0; i < HomeFragment.numberOfForms; i++) {
            getData(i);
        }
    }

    public BigDecimal getValue(int id) {
        return values[id];
    }

    public void setValue(int id, BigDecimal value) {
        values[id] = value;
    }

    public void saveData(int id) {
        WritingThread writingThread = new WritingThread(id);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(writingThread);
    }

    public void getData(int id) {
        ReadingThread readingThread = new ReadingThread(id);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<BigDecimal> future = executorService.submit(readingThread);
        try {
            values[id] = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class WritingThread implements Runnable {

        private int id;

        public WritingThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            saveData();
        }

        private void saveData() {
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE_NAMES[id], Context.MODE_PRIVATE)));
                bufferedWriter.write(values[id].toString());
                bufferedWriter.close();
            } catch (FileNotFoundException e) {
                new File(FILE_NAMES[id]);
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class ReadingThread implements Callable<BigDecimal> {

        private int id;

        public ReadingThread(int id) {
            this.id = id;
        }

        @Override
        public BigDecimal call() throws Exception {
            return getData();
        }

        private BigDecimal getData() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAMES[id])));
                BigDecimal value = new BigDecimal(bufferedReader.readLine());
                bufferedReader.close();
                return value;
            } catch (FileNotFoundException e) {
                new File(FILE_NAMES[id]);
                values[id] = new BigDecimal(0);
                WritingThread writingThread = new WritingThread(id);
                new Thread(writingThread).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
