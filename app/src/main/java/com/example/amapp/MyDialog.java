package com.example.amapp;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDialog extends DialogFragment {
    public static final String CLASS_ADD_DIALOG = "addClass";
    public static final String STUDENT_ADD_DIALOG = "addStudent";
    public static final String CLASS_EDIT_DIALOG = "editClass";
    public static final String STUDENT_EDIT_DIALOG = "editStudent";
    private int rollNo;
    private String name;
    private OnClickListener listener;


    public MyDialog() {

    }

    public MyDialog(int rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
    }

    public interface OnClickListener{
        void onClick(String text1, String text2);
    }
    public void setListener(OnClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = null;
        if (getTag().equals(CLASS_ADD_DIALOG)) {
            dialog = getAddClassDialog();
        }
        if (getTag().equals(STUDENT_ADD_DIALOG)) {
            dialog = getAddStudentDialog();
        }
        if (getTag().equals(CLASS_EDIT_DIALOG)) {
            dialog = getEditClassDialog();
        }
        if (getTag().equals(STUDENT_EDIT_DIALOG)) {
            dialog = getEditStudentDialog();
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    private Dialog getEditStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView title = view.findViewById(R.id.title_add_class);
        title.setText("Edit Student");
        EditText rollNo = view.findViewById(R.id.edit01);
        EditText name_EDT = view.findViewById(R.id.edit02);
        rollNo.setHint("Enter Roll Number");
        name_EDT.setHint("Enter Name");
        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);
        add.setText("Edit");
        rollNo.setText(String.valueOf(this.rollNo));
        rollNo.setEnabled(false);
        name_EDT.setText(name);
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String rollNoText = rollNo.getText().toString();
            String nameText = name_EDT.getText().toString();
            listener.onClick(rollNoText, nameText);
            dismiss();
        });
        return dialog;
    }

    private Dialog getEditClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView title = view.findViewById(R.id.title_add_class);
        title.setText("Edit Class");
        EditText className = view.findViewById(R.id.edit01);
        EditText subjectName = view.findViewById(R.id.edit02);
        className.setHint("Enter Class Name");
        subjectName.setHint("Enter Subject Name");
        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);
        add.setText("Edit");
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String classNameText = className.getText().toString();
            String subjectNameText = subjectName.getText().toString();
            listener.onClick(classNameText, subjectNameText);
            dismiss();
        });
        return dialog;
    }

    private Dialog getAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView title = view.findViewById(R.id.title_add_class);
        title.setText("Add New Student");
        EditText rollNo = view.findViewById(R.id.edit01);
        EditText name = view.findViewById(R.id.edit02);
        rollNo.setHint("Enter Roll Number");
        name.setHint("Enter Name");
        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String rollNoText = rollNo.getText().toString();
            String nameText = name.getText().toString();
            rollNo.setText(String.valueOf(Integer.parseInt(rollNoText) + 1));
            name.setText("");
            listener.onClick(rollNoText, nameText);
        });
        return dialog;
    }

    private Dialog getAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView title = view.findViewById(R.id.title_add_class);
        title.setText("Add New Class");
        EditText className = view.findViewById(R.id.edit01);
        EditText subjectName = view.findViewById(R.id.edit02);
        className.setHint("Enter Class Name");
        subjectName.setHint("Enter Subject Name");
        Button cancel = view.findViewById(R.id.cancel_btn);
        Button add = view.findViewById(R.id.add_btn);
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String classNameText = className.getText().toString();
            String subjectNameText = subjectName.getText().toString();
            listener.onClick(classNameText, subjectNameText);
            dismiss();
        });
        return dialog;
    }
}
