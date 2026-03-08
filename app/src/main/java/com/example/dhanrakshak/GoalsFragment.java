package com.example.dhanrakshak;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private List<GoalModel> goalList;
    private DatabaseHelper db;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        db = new DatabaseHelper(requireContext());
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs",
                requireContext().MODE_PRIVATE);
        userEmail = prefs.getString("email", "unknown");

        recyclerView = view.findViewById(R.id.goalsRecyclerView);
        goalList = new ArrayList<>();
        adapter = new GoalAdapter(requireContext(), goalList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        loadGoals();

        // FAB
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fabAddGoal);
        fab.setOnClickListener(v -> showAddGoalDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGoals();
    }

    private void loadGoals() {
        goalList.clear();
        goalList.addAll(db.getAllGoals(userEmail));
        adapter.notifyDataSetChanged();
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Goal");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText nameInput = new EditText(requireContext());
        nameInput.setHint("Goal Name (e.g., Buy Laptop)");
        layout.addView(nameInput);

        EditText targetInput = new EditText(requireContext());
        targetInput.setHint("Target Amount (₹)");
        targetInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(targetInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String targetStr = targetInput.getText().toString().trim();

            if (name.isEmpty() || targetStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double target = Double.parseDouble(targetStr);
            boolean success = db.insertGoal(userEmail, name, target);
            if (success) {
                Toast.makeText(requireContext(), "Goal added!", Toast.LENGTH_SHORT).show();
                loadGoals();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
