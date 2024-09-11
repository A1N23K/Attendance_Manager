package com.example.amapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    Toolbar toolbar;
    private String className;
    private String subjectName;
    private int position;
    private StudentAdapter studentAdapter;
    private ArrayList<StudentItem> studentItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private long classId;
    private MyCalendar calendar;
    private ImageButton addStudent;
    private TextView subtitle;
    private ImageButton saveStudent;
    private ImageButton deleteStudent;
    private ImageButton editStudent;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        calendar = new MyCalendar();
        dbHelper = new DBHelper(this);
        //subtitle = findViewById(R.id.sub_title_toolbar);
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position", -1);
        classId = intent.getLongExtra("classId", -1);
        setToolbar();
        loadData();
        recyclerView = findViewById(R.id.recycler_view_student);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        studentAdapter = new StudentAdapter(this, studentItems);
        recyclerView.setAdapter(studentAdapter);
        studentAdapter.setOnItemClickListener(position -> gotoStatusChangeActivity(position));
        loadAttendance();

    }

    private void loadData() {
        Cursor cursor = dbHelper.getAllStudent(classId);
        studentItems.clear();
        while (cursor.moveToNext()) {
            long sid = cursor.getLong(cursor.getColumnIndex(DBHelper.STUDENT_ID));
            int roll = cursor.getInt(cursor.getColumnIndex(DBHelper.STUDENT_ROLL));
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.STUDENT_NAME));
            studentItems.add(new StudentItem(sid, roll, name));
        }
        cursor.close();
    }

    private void gotoStatusChangeActivity(int position) {
        String status = studentItems.get(position).getStatus();
        if(status.equals("P")){
            studentItems.get(position).setStatus("A");
        }else{
            studentItems.get(position).setStatus("P");
        }
        studentAdapter.notifyItemChanged(position);

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.sub_title_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back_button);
        ImageButton save = toolbar.findViewById(R.id.save_button);
        save.setOnClickListener(v -> saveAttendance());
        title.setText(className);
        subtitle.setText(subjectName+" | "+calendar.getCalendarDate());
        back.setOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }

    private void saveAttendance() {
        for (StudentItem studentItem : studentItems) {
           String status = studentItem.getStatus();
           if(status==null || status.isEmpty()){
               status = "A";
           }
           long value = dbHelper.addStatus(studentItem.getSid(),classId, calendar.getCalendarDate(), status);
           Log.i("1234567890", "saveAttendance: "+value);
           if(value == -1){
                dbHelper.updateStatus(studentItem.getSid(), calendar.getCalendarDate(), status);
           }
        }
    }

    private void loadAttendance() {
        for (StudentItem studentItem : studentItems) {
            String status = dbHelper.getStatus(studentItem.getSid(), calendar.getCalendarDate());
            if (status != null){
                studentItem.setStatus(status);
            } else {
                studentItem.setStatus("");
            }
        }
        studentAdapter.notifyDataSetChanged();
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.add_student) {
            showAddStudentDialog();
        }
        else if(menuItem.getItemId() == R.id.show_calendar) {
            showCalendar();
        }
        else if(menuItem.getItemId() == R.id.attendance_sheet) {
            openSheetListActivity();
        }
        return true;
    }

    private void openSheetListActivity() {
        long[] studentIds = new long[studentItems.size()];
        for (int i = 0; i < studentItems.size(); i++) {
            studentIds[i] = studentItems.get(i).getSid();
        }
        int[] rollNos = new int[studentItems.size()];
        for (int i = 0; i < studentItems.size(); i++) {
            rollNos[i] = studentItems.get(i).getRollNo();
        }
        String[] studentNames = new String[studentItems.size()];
        for (int i = 0; i < studentItems.size(); i++) {
            studentNames[i] = studentItems.get(i).getName();
        }
        Intent intent = new Intent(this, SheetListActivity.class);
        intent.putExtra("cid", classId);
        intent.putExtra("className", className);
        intent.putExtra("subjectName", subjectName);
        intent.putExtra("sidArray", studentIds);
        intent.putExtra("rollArray", rollNos);
        intent.putExtra("nameArray", studentNames);
        startActivity(intent);
    }

    private void showCalendar() {
        calendar.show(getSupportFragmentManager(), "");
        calendar.setOnCalendarOkClickListener(this::onCalendarOkClick);
    }

    private void onCalendarOkClick(int year, int month, int day) {
        calendar.setCalendarDate(year, month, day);
        subtitle.setText(subjectName+" | "+calendar.getCalendarDate());
        loadAttendance();
    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.STUDENT_ADD_DIALOG);
        dialog.setListener((roll,name)->addStudent(roll, name));
    }

    private void addStudent(String roll_str, String name) {
        int roll = Integer.parseInt(roll_str);
        long sid = dbHelper.addStudent(name, roll, classId);
        StudentItem studentItem = new StudentItem(sid, roll, name);
        studentItems.add(studentItem);
        studentAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getGroupId();
        if (item.getItemId() == 0) {
            showEditStudentDialog(position);
        } else if (item.getItemId() == 1) {
            deleteStudent(position);
        }
        return super.onContextItemSelected(item);
    }

    private void showEditStudentDialog(int position) {
        MyDialog dialog = new MyDialog(studentItems.get(position).getRollNo(), studentItems.get(position).getName());
        dialog.show(getSupportFragmentManager(), MyDialog.STUDENT_EDIT_DIALOG);
        dialog.setListener((roll_str, name) -> editStudent(position, name));
    }

    private void editStudent(int position, String name) {
        long sid = studentItems.get(position).getSid();
        dbHelper.updateStudent(sid, name);
        studentItems.get(position).setName(name);
        studentAdapter.notifyItemChanged(position);

    }

    private void deleteStudent(int position) {
        long sid = studentItems.get(position).getSid();
        dbHelper.deleteStudent(sid);
        studentItems.remove(position);
        studentAdapter.notifyItemRemoved(position);
    }
}