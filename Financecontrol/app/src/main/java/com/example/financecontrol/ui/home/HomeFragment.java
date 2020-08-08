package com.example.financecontrol.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.financecontrol.DBHelper;
import com.example.financecontrol.Data;
import com.example.financecontrol.DialogCreator;
import com.example.financecontrol.R;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class HomeFragment extends Fragment implements View.OnClickListener {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public final static int numberOfForms = 6;

    public final static int AMOUNT_ID = 0;
    public final static int TRANSPORT_ID = 1;
    public final static int NUTRITION_ID = 2;
    public final static int PURCHASES_ID = 3;
    public final static int RECREATION_ID = 4;
    public final static int REAL_ESTATE_ID = 5;
    final int[] titles = new int[] {R.string.add_amount, R.string.spend_on_transport, R.string.spend_on_nutrition, R.string.spend_on_purchases, R.string.spend_on_recreation, R.string.spend_on_real_estate};
    final int[] formNames = new int[] {R.string.amount, R.string.transport, R.string.nutrition, R.string.purchases, R.string.recreation, R.string.real_estate};
    TextView[] textViews;

    DBHelper dbHelper;
    Data data;
    DialogCreator dialogCreator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        dbHelper = new DBHelper(this.getActivity());
        textViews = new TextView[] {root.findViewById(R.id.amountText), root.findViewById(R.id.transportText), root.findViewById(R.id.nutritionText), root.findViewById(R.id.purchasesText), root.findViewById(R.id.recreationText), root.findViewById(R.id.realEstateText)};
        data = new Data(getContext());
        dialogCreator = new DialogCreator(titles, formNames, textViews, data, decimalFormat, getContext());
        setScreenNumbers();
        return root;
    }

    @Override
    public void onClick(View view) {
        Dialog dialog = null;
        switch (view.getId()) {
            case R.id.amountText:
                dialog = dialogCreator.createDialog(AMOUNT_ID);
                break;
            case R.id.transportText:
                dialog = dialogCreator.createDialog(TRANSPORT_ID);
                break;
            case R.id.nutritionText:
                dialog = dialogCreator.createDialog(NUTRITION_ID);
                break;
            case R.id.purchasesText:
                dialog = dialogCreator.createDialog(PURCHASES_ID);
                break;
            case R.id.recreationText:
                dialog = dialogCreator.createDialog(RECREATION_ID);
                break;
            case R.id.realEstateText:
                dialog = dialogCreator.createDialog(REAL_ESTATE_ID);
                break;
        }
        dialog.show();
    }

    public void setScreenNumbers() {
        for(int i = 0; i < numberOfForms; i++) {
            textViews[i].setText(getString(formNames[i], decimalFormat.format(data.getValue(i))));
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