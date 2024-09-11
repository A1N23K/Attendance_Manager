package com.example.amapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

//import java.text.DateFormat;
import java.util.Calendar;
import android.text.format.DateFormat;

public class MyCalendar extends DialogFragment {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    public interface OnCalendarOkClickListener{
        void onClick(int year, int month, int day);
    }
    private OnCalendarOkClickListener onCalendarOkClickListener;
    public void setOnCalendarOkClickListener(OnCalendarOkClickListener onCalendarOkClickListener){
        this.onCalendarOkClickListener = onCalendarOkClickListener;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(requireActivity(), (view, year, month, dayOfMonth) -> {
            if (onCalendarOkClickListener != null){
                onCalendarOkClickListener.onClick(year,month,dayOfMonth);
            }
        },year,month,day);
    }

    void setCalendarDate(int year, int month, int day){
        calendar.set(year,month,day);
    }

    String getCalendarDate(){
        return DateFormat.format("dd-MM-yyyy", calendar).toString();
    }
}
