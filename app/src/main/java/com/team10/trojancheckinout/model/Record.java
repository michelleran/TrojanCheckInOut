package com.team10.trojancheckinout.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Record {
    private String studentId;
    private String buildingId;
    private String buildingName;
    private LocalDateTime time;
    private boolean checkIn;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String major;

    private static final ZoneId pst = ZoneId.of("America/Los_Angeles");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    public Record() { }

    public Record(String studentId, String buildingId, String buildingName, String major, boolean checkIn) {
        this.studentId = studentId;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.time = LocalDateTime.now(pst);
        this.year = time.getYear();
        this.month = time.getMonthValue();
        this.day = time.getDayOfMonth();
        this.hour = time.getHour();
        this.minute = time.getMinute();
        this.checkIn = checkIn;
        this.major = major;
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
