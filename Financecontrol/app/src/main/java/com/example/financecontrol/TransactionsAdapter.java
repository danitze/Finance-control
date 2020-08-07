package com.example.financecontrol;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financecontrol.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>{
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private List<Transaction> transactionsList;

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        holder.bind(transactionsList.get(position));
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    class TransactionsViewHolder extends RecyclerView.ViewHolder {
        private TextView transactionAmount;
        private TextView transactionDescription;
        private TextView dateValue;
        private TextView balanceValue;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionAmount = (TextView) itemView.findViewById(R.id.transactionAmount);
            transactionDescription = (TextView) itemView.findViewById(R.id.transactionDescription);
            dateValue = (TextView) itemView.findViewById(R.id.dateValue);
            balanceValue = (TextView) itemView.findViewById(R.id.balanceValue);
        }

        public void bind(Transaction transaction) {
            String transactionAmountValue = transaction.getAmount();
            if(transaction.getAmount().toCharArray()[0] == '-')
                transactionAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorRed));
            else {
                transactionAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorGreen));
                transactionAmountValue = "+" + transactionAmountValue;
            }
            transactionAmount.setText(itemView.getResources().getString(R.string.transaction_text, transactionAmountValue));
            transactionDescription.setText(itemView.getResources().getString(R.string.description_text, transaction.getDescription()));
            dateValue.setText(itemView.getResources().getString(R.string.date_value, dateFormat(transaction.getDate())));
            balanceValue.setText(itemView.getResources().getString(R.string.balance_value, transaction.getBalance()));
        }
    }

    public void setTransactionsList(List<Transaction> transactions) {
        this.transactionsList = transactions;
        notifyDataSetChanged();
    }

    public void addTransaction(Transaction transaction) {
        this.transactionsList.add(transaction);
        notifyDataSetChanged();
    }

    public void clearTransactions() {
        this.transactionsList.clear();
        notifyDataSetChanged();
    }

    public String dateFormat(int unixTime) {
        Date date = new Date(unixTime * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }

}
