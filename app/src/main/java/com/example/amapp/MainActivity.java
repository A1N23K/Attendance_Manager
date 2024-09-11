package com.example.amapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItems> classItems= new ArrayList<>();
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.btn_logout);
        //textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(v -> showDialog());
        loadData();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));
        setToolbar();


        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            //textView.setText(user.getEmail());
        }
        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadData() {
        Cursor cursor = new DBHelper(this).getAllClass();
        classItems.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DBHelper.CLASS_ID));
            String className = cursor.getString(cursor.getColumnIndex(DBHelper.CLASS_NAME));
            String subjectName = cursor.getString(cursor.getColumnIndex(DBHelper.SUBJECT_NAME));
            classItems.add(new ClassItems(id, className, subjectName));
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.sub_title_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back_button);
        ImageButton save = toolbar.findViewById(R.id.save_button);

        title.setText("ATTENDANCE MANAGER");
        title.setTextSize(16);
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
        save.setImageResource(R.drawable.icons_user_32);
    }

    private void gotoItemActivity(int position) {
        Intent intent = new Intent(this, StudentActivity.class);
        intent.putExtra("className", classItems.get(position).getClassName());
        intent.putExtra("subjectName", classItems.get(position).getSubjectName());
        intent.putExtra("position", position);
        intent.putExtra("classId", classItems.get(position).getCid());
        startActivity(intent);
    }

    private void showDialog() {
       MyDialog dialog = new MyDialog();
       dialog.show(getSupportFragmentManager(), MyDialog.CLASS_ADD_DIALOG);
       dialog.setListener((className, subjectName) -> addClass(className, subjectName));

    }

    private void addClass(String className, String subjectName) {
        long cid = new DBHelper(this).addClass(className, subjectName);
        ClassItems classItem = new ClassItems(cid, className, subjectName);
        classItems.add(classItem);
        classAdapter.notifyDataSetChanged();

    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = item.getGroupId();
        if(item.getItemId() == 0){
            showEditDialog(position);
        }else if(item.getItemId() == 1){
            deleteClass(position);
        }
        return super.onContextItemSelected(item);
    }

    private void showEditDialog(int position) {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_EDIT_DIALOG);
        dialog.setListener((className, subjectName) -> editClass(position, className, subjectName));
    }

    private void editClass(int position, String className, String subjectName) {
        long id = classItems.get(position).getCid();
        new DBHelper(this).updateClass(id, className, subjectName);
        classItems.get(position).setClassName(className);
        classItems.get(position).setSubjectName(subjectName);
        classAdapter.notifyDataSetChanged();
    }

    private void deleteClass(int position) {
        long id = classItems.get(position).getCid();
        new DBHelper(this).deleteClass(id);
        classItems.remove(position);
        classAdapter.notifyDataSetChanged();
    }
}