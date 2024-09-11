package com.example.amapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class SheetActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String className;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sheet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        className = getIntent().getStringExtra("className");
        subjectName = getIntent().getStringExtra("subjectName");

        setToolbar();

        showTable();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.title_toolbar);
        TextView subtitle = findViewById(R.id.sub_title_toolbar);
        ImageButton back = findViewById(R.id.back_button);
        ImageButton save = findViewById(R.id.save_button);
        title.setText("Attendance Sheet");
        subtitle.setText(className + " | " + subjectName + " | " + getIntent().getStringExtra("month"));
        back.setOnClickListener(v -> finish());
        save.setVisibility(ImageButton.GONE);
    }

    private void showTable() {
        DBHelper dbHelper = new DBHelper(this);
        TableLayout tableLayout = findViewById(R.id.table_layout);
        long [] sidArray = getIntent().getLongArrayExtra("sidArray");
        int [] rollArray = getIntent().getIntArrayExtra("rollArray");
        String [] nameArray = getIntent().getStringArrayExtra("nameArray");
        String month = getIntent().getStringExtra("month");

        int DAY_IN_MONTH = getDayInMonth(month);

        int rowSize = sidArray.length + 1;
        TableRow[] tableRows = new TableRow[rowSize];
        TextView[] rollTextViews = new TextView[rowSize];
        TextView[] nameTextViews = new TextView[rowSize];
        TextView[][] statusTextViews = new TextView[rowSize][DAY_IN_MONTH + 1];
        for (int i = 0; i < rowSize; i++) {
            rollTextViews[i] = new TextView(this);
            nameTextViews[i] = new TextView(this);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                statusTextViews[i][j] = new TextView(this);
            }
        }

        rollTextViews[0].setText("Roll");
        rollTextViews[0].setTypeface(rollTextViews[0].getTypeface(), Typeface.BOLD);
        nameTextViews[0].setText("Name");
        nameTextViews[0].setTypeface(nameTextViews[0].getTypeface(), Typeface.BOLD);
        for (int i = 1; i <= DAY_IN_MONTH; i++) {
            statusTextViews[0][i].setText(String.valueOf(i));
            statusTextViews[0][i].setTypeface(statusTextViews[0][i].getTypeface(), Typeface.BOLD);
        }

        for(int i = 1; i < rowSize; i++) {
            rollTextViews[i].setText(String.valueOf(rollArray[i-1]));
            nameTextViews[i].setText(nameArray[i-1]);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if (day.length() == 1) {
                    day = "0" + day;
                }
                String date = day + "-" + month;
                String status = dbHelper.getStatus(sidArray[i-1], date);
                statusTextViews[i][j].setText(status);
            }
        }

        for (int i = 0; i < rowSize; i++) {
            tableRows[i] = new TableRow(this);

            if (i % 2 == 0) {
                tableRows[i].setBackgroundColor(0xFFE0E0E0);
            }else{
                tableRows[i].setBackgroundColor(0xFFFFFFFF);
            }
            rollTextViews[i].setPadding(16,16,16,16);
            nameTextViews[i].setPadding(16,16,16,16);

            tableRows[i].addView(rollTextViews[i]);
            tableRows[i].addView(nameTextViews[i]);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                statusTextViews[i][j].setPadding(16,16,16,16);
                tableRows[i].addView(statusTextViews[i][j]);
            }
            tableLayout.addView(tableRows[i]);
        }
        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);

    }

    private int getDayInMonth(String month) {
        int monthIndex = Integer.parseInt(month.substring(0,2))-1;
        int year = Integer.parseInt(month.substring(3));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,monthIndex);
        calendar.set(Calendar.YEAR,year);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}