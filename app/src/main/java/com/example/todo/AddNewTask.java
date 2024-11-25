package com.example.todo;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todo.Model.ToDoModel;
import com.example.todo.utlis.DataBaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";
    private EditText mEditText;
    private Button mSaveButton;
    private DataBaseHelper myDb;
    private boolean isTextCleared = false; // Flag to ensure text is cleared only once

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_tew_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditText = view.findViewById(R.id.editText);
        mSaveButton = view.findViewById(R.id.addbutton);
        myDb = new DataBaseHelper(getActivity());

        boolean isUpdate = false;
        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            mEditText.setText(task);
            mSaveButton.setEnabled(task.length() > 0); // Enable button if there's an existing task
        } else {
            mEditText.setHint("Enter your task...");
        }

        // TextWatcher to manage button state
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Clear the text only on the first input
                if (!isTextCleared && charSequence.length() > 0) {
                    isTextCleared = true; // Prevent further clearing
                }

                // Update save button state
                boolean isEmpty = charSequence.toString().trim().isEmpty();
                mSaveButton.setEnabled(!isEmpty);
                mSaveButton.setBackgroundColor(isEmpty ? Color.GRAY : Color.BLUE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        boolean finalIsUpdate = isUpdate;
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mEditText.getText().toString().trim(); // Trim whitespace
                if (text.isEmpty()) {
                    // Show a message to user (optional)
                    mEditText.setError("Task cannot be empty");
                    return; // Do not proceed if the task is empty
                }

                if (finalIsUpdate) {
                    // Update existing task
                    myDb.updateTask(bundle.getInt("Id"), text);
                } else {
                    // Insert a new task
                    ToDoModel item = new ToDoModel();
                    item.setTask(text);
                    item.setStatus(0); // New tasks are assumed to have status 0 (not completed)
                    myDb.inserttask(item);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}
