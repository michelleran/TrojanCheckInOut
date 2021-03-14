package com.team10.trojancheckinout.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Record {
    private String studentId;
    private String buildingId;
    private LocalDateTime time;
    private boolean checkIn;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String major;

    private static final ZoneId pst = ZoneId.of("America/Los_Angeles");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    public Record() { }
    public Record(String buildingId, String major, boolean checkIn) {
        this.studentId = Server.getCurrentUser().getIdString(); // assumes current user is a student
        this.buildingId = buildingId;
        this.year = LocalDateTime.now(pst).getYear();
        this.month = LocalDateTime.now(pst).getMonthValue();
        this.day = LocalDateTime.now(pst).getDayOfYear();
        this.hour = LocalDateTime.now(pst).getHour();
        this.minute = LocalDateTime.now(pst).getMinute();
        this.time = LocalDateTime.now(pst);
        this.checkIn = checkIn;
    }

    public String getStudentId() { return studentId; }
    public String getBuildingId() { return buildingId; }
    public LocalDateTime getTime() { return time; }
    public boolean getCheckIn() { return checkIn; }
    public String getTimeString() {
        return time.format(formatter);
    }


    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public String getMajor(){ return major;}
}
