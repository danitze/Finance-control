package com.example.financecontrol.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financecontrol.DBHelper;
import com.example.financecontrol.R;
import com.example.financecontrol.Transaction;
import com.example.financecontrol.TransactionsAdapter;

public class TransactionsFragment extends Fragment {
        private View root;

        private RecyclerView transactionsRecyclerView;
        private TransactionsAdapter transactionsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_transactions, container, false);
        initRecyclerView();
        loadTransactions();
        return root;
    }

    private void loadTransactions() {
        transactionsAdapter.setTransactionsList(Transaction.getTransactions(new DBHelper(getContext())));
        transactionsRecyclerView.getLayoutManager().scrollToPosition(transactionsAdapter.getItemCount() - 1);
    }

    private void initRecyclerView() {
        transactionsRecyclerView = (RecyclerView) root.findViewById(R.id.transactionsList);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        transactionsAdapter = new TransactionsAdapter();
        transactionsRecyclerView.setAdapter(transactionsAdapter);
    }
}