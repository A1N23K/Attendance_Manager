package com.example.amapp;

public class StudentItem {
    private long sid;
    private int rollNo;
    private String name;
    private String status;

    public StudentItem(long sid, int rollNo, String name, String status) {
        this.sid = sid;
        this.rollNo = rollNo;
        this.name = name;
        this.status = status;
    }

    public StudentItem(long sid, int rollNo, String name) {
        this.sid = sid;
        this.rollNo = rollNo;
        this.name = name;
        status = "";
    }

    public int getRollNo() {

        return rollNo;
    }

    public void setRollNo(int rollNo) {

        this.rollNo = rollNo;

    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }
}
