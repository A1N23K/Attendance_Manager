package com.example.amapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    //CLASS TABLE
    public static final String CLASS_TABLE_NAME = "CLASS_TABLE";
    public static final String CLASS_ID = "C_ID";
    public static final String CLASS_NAME = "C_NAME";
    public static final String SUBJECT_NAME = "S_NAME";
    public static final String CREATE_CLASS_TABLE =
            "CREATE TABLE " + CLASS_TABLE_NAME + " (" +
                    CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CLASS_NAME + " TEXT NOT NULL, " +
                    SUBJECT_NAME + " TEXT NOT NULL,"+
                    " UNIQUE (" + CLASS_NAME + ", " + SUBJECT_NAME + ")"+
                    ");";

    private static final String DROP_CLASS_TABLE = "DROP TABLE IF EXISTS " + CLASS_TABLE_NAME;
    private static final String SELECT_ALL_CLASS = "SELECT * FROM " + CLASS_TABLE_NAME;

    //STUDENT TABLE
    public static final String STUDENT_TABLE_NAME = "STUDENT_TABLE";
    public static final String STUDENT_ID = "S_ID";
    public static final String STUDENT_NAME = "S_NAME";
    public static final String STUDENT_ROLL = "S_ROLL";
    public static final String CREATE_STUDENT_TABLE =
            "CREATE TABLE " + STUDENT_TABLE_NAME + " (" +
                    STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STUDENT_NAME + " TEXT NOT NULL, " +
                    STUDENT_ROLL + " INTEGER NOT NULL, " +
                    CLASS_ID + " INTEGER NOT NULL, " +
                    " FOREIGN KEY (" + CLASS_ID + ") REFERENCES " + CLASS_TABLE_NAME + "(" + CLASS_ID + ")" +
                    ");";
    private static final String DROP_STUDENT_TABLE = "DROP TABLE IF EXISTS " + STUDENT_TABLE_NAME;
    private static final String SELECT_ALL_STUDENT = "SELECT * FROM " + STUDENT_TABLE_NAME;

    //ATTENDANCE TABLE
    private static final String ATTENDANCE_TABLE_NAME = "ATTENDANCE_TABLE";
    public static final String ATTENDANCE_ID = "A_ID";
    public static final String ATTENDANCE_STATUS = "A_STATUS";
    public static final String ATTENDANCE_DATE = "A_DATE";
    private static final String CREATE_ATTENDANCE_TABLE =
            "CREATE TABLE " + ATTENDANCE_TABLE_NAME +
                    "(" +
                    ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    STUDENT_ID + " INTEGER NOT NULL, " +
                    ATTENDANCE_DATE + " DATE NOT NULL, " +
                    ATTENDANCE_STATUS + " TEXT NOT NULL, " +
                    CLASS_ID + " INTEGER NOT NULL, " +
                    " UNIQUE (" + STUDENT_ID + ", " + ATTENDANCE_DATE + "), " +
                    " FOREIGN KEY (" + STUDENT_ID + ") REFERENCES " + STUDENT_TABLE_NAME + "( " + STUDENT_ID + ")," +
                    " FOREIGN KEY (" + CLASS_ID + ") REFERENCES " + CLASS_TABLE_NAME + "( " + CLASS_ID+ ")" +
                    ");";
    private static final String DROP_ATTENDANCE_TABLE = "DROP TABLE IF EXISTS " + ATTENDANCE_TABLE_NAME;
    private static final String SELECT_ALL_ATTENDANCE = "SELECT * FROM " + ATTENDANCE_TABLE_NAME;


    public DBHelper(@Nullable Context context) {
        super(context, "Attendance.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_CLASS_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_ATTENDANCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(DROP_CLASS_TABLE);
            db.execSQL(DROP_STUDENT_TABLE);
            db.execSQL(DROP_ATTENDANCE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    long addClass(String className, String subjectName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CLASS_NAME, className);
        cv.put(SUBJECT_NAME, subjectName);
        long result = db.insert(CLASS_TABLE_NAME, null, cv);
        return result;
    }
    Cursor getAllClass(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(SELECT_ALL_CLASS, null);
    }
    long addStudent(String studentName, int rollNo, long classId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STUDENT_NAME, studentName);
        cv.put(STUDENT_ROLL, rollNo);
        cv.put(CLASS_ID, classId);
        long result = db.insert(STUDENT_TABLE_NAME, null, cv);
        return result;
    }

    public void deleteClass(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CLASS_TABLE_NAME, CLASS_ID + " = " + id, null);

    }

    public void updateClass(long id, String className, String subjectName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CLASS_NAME, className);
        cv.put(SUBJECT_NAME, subjectName);
        db.update(CLASS_TABLE_NAME, cv, CLASS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllStudent(long classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + STUDENT_TABLE_NAME + " WHERE " + CLASS_ID + " = " + classId, null);
    }

    public Cursor getAllAttendance(long studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + ATTENDANCE_TABLE_NAME + " WHERE " + STUDENT_ID + " = " + studentId, null);
    }

    public void deleteStudent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STUDENT_TABLE_NAME, STUDENT_ID + " = " + id, null);
    }

    public void updateStudent(long sid, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STUDENT_NAME, name);
        db.update(STUDENT_TABLE_NAME, cv, STUDENT_ID + " = " + sid, null);
    }

    long addStatus(long sid, long cid, String date, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STUDENT_ID, sid);
        cv.put(CLASS_ID, cid);
        cv.put(ATTENDANCE_DATE, date);
        cv.put(ATTENDANCE_STATUS, status);
        return db.insert(ATTENDANCE_TABLE_NAME,
                null,
                cv);
    }

    long updateStatus(long sid, String date, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ATTENDANCE_STATUS, status);
        String where = ATTENDANCE_DATE + " = ? AND " + STUDENT_ID + " = ?";
        return db.update(ATTENDANCE_TABLE_NAME, cv, where, new String[]{date, String.valueOf(sid)});
    }

    String getStatus(long sid, String date){
        String status = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String where = ATTENDANCE_DATE + " = ? AND " + STUDENT_ID + " = ?";
        Cursor cursor = db.query(ATTENDANCE_TABLE_NAME, new String[]{ATTENDANCE_STATUS}, where, new String[]{date, String.valueOf(sid)}, null, null, null);
        if(cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndex(ATTENDANCE_STATUS));
        }
        cursor.close();
        return status;
    }

    public void deleteStatus(long sid, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = STUDENT_ID + " = " + sid + " AND " + ATTENDANCE_DATE + " = '" + date+"'";
        db.delete(ATTENDANCE_TABLE_NAME, where, null);
    }

    public long addAttendance(long sid,long cid, String calendarDate, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STUDENT_ID, sid);
        cv.put(CLASS_ID, cid);
        cv.put(ATTENDANCE_DATE, calendarDate);
        cv.put(ATTENDANCE_STATUS, status);
        return db.insert(ATTENDANCE_TABLE_NAME, null, cv);
    }

    public void updateAttendance(long sid, String calendarDate, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ATTENDANCE_STATUS, status);
        String where = STUDENT_ID + " = ? AND " + ATTENDANCE_DATE + " = ?";
        db.update(ATTENDANCE_TABLE_NAME, cv, where, new String[]{String.valueOf(sid), calendarDate});
    }

    Cursor getDistinctMonth(long cid){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(ATTENDANCE_TABLE_NAME, new String[]{ATTENDANCE_DATE}, CLASS_ID + " = " + cid, null, "substr("+ATTENDANCE_DATE+",4,7)", null, null);
    }
}
