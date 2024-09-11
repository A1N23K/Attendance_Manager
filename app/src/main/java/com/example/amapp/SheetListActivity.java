package com.example.amapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SheetListActivity extends AppCompatActivity {
    private ListView sheetList;
    private ArrayAdapter adapter;
    private ArrayList<String> listItems = new ArrayList();
    private long cid;
    private String className;
    private String subjectName;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sheet_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cid = getIntent().getLongExtra("cid", -1);
        className = getIntent().getStringExtra("className");
        subjectName = getIntent().getStringExtra("subjectName");
        Log.i("1234567890", "onCreate: " + cid);
        loadList();
        sheetList = findViewById(R.id.sheet_list);
        adapter = new ArrayAdapter(this, R.layout.sheet_list_item, R.id.list_date_text, listItems);
        sheetList.setAdapter(adapter);
        sheetList.setOnItemClickListener((parent, view, position, id) -> openSheetActivity(position));
        setToolbar();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.title_toolbar);
        TextView subtitle = findViewById(R.id.sub_title_toolbar);
        ImageButton back = findViewById(R.id.back_button);
        ImageButton save = findViewById(R.id.save_button);
        title.setText("Sheet List");
        subtitle.setText(className+" | "+subjectName);
        back.setOnClickListener(v -> finish());
        save.setVisibility(View.INVISIBLE);
    }

    private void openSheetActivity(int position) {
        long [] sidArray = getIntent().getLongArrayExtra("sidArray");
        int [] rollArray = getIntent().getIntArrayExtra("rollArray");
        String [] nameArray = getIntent().getStringArrayExtra("nameArray");
        Intent intent = new Intent(this, SheetActivity.class);
        intent.putExtra("className", className);
        intent.putExtra("subjectName", subjectName);
        intent.putExtra("sidArray", sidArray);
        intent.putExtra("rollArray", rollArray);
        intent.putExtra("nameArray", nameArray);
        intent.putExtra("month", listItems.get(position));

        startActivity(intent);
    }

    private void loadList() {
        Cursor cursor = new DBHelper(this).getDistinctMonth(cid);

        Log.i("1234567890", "loadList: "+cursor.getCount());
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DBHelper.ATTENDANCE_DATE));
            listItems.add(date.substring(3));
        }
    }
}