package com.example.dhanrakshak;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<TransactionModel> allTransactions;
    private List<TransactionModel> filteredTransactions;
    private EditText searchEditText;
    private TabLayout tabLayout;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        db = new DatabaseHelper(requireContext());

        recyclerView = view.findViewById(R.id.transactionsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        tabLayout = view.findViewById(R.id.tabLayout);

        allTransactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), filteredTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Load all transactions
        loadTransactions("All");

        // Tab filtering
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filter = tab.getText().toString();
                loadTransactions(filter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // FAB
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fabAddTransaction);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddTransactionActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TabLayout.Tab tab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
        if (tab != null) {
            loadTransactions(tab.getText().toString());
        }
    }

    private void loadTransactions(String filterType) {
        allTransactions.clear();
        allTransactions.addAll(db.getAllTransactions(filterType));
        filteredTransactions.clear();
        filteredTransactions.addAll(allTransactions);
        adapter.notifyDataSetChanged();
    }

    private void filterBySearch(String query) {
        filteredTransactions.clear();
        if (query.isEmpty()) {
            filteredTransactions.addAll(allTransactions);
        } else {
            for (TransactionModel t : allTransactions) {
                if (t.getCategory() != null && t.getCategory().toLowerCase().contains(query.toLowerCase())) {
                    filteredTransactions.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
