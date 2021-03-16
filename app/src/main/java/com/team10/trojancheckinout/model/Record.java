package com.team10.trojancheckinout.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Record implements Comparable<Record> {
    private String studentId;
    private String buildingId;
    private String buildingName;
    private boolean checkIn;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String major;

    private static final ZoneId pst = ZoneId.of("America/Los_Angeles");

    public Record() { }

    public Record(String studentId, String buildingId, String buildingName, String major, boolean checkIn) {
        this.studentId = studentId;
        this.buildingId = buildingId;
        this.buildingName = buildingName;

        LocalDateTime time = LocalDateTime.now(pst);
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
    public String getBuildingName() { return buildingName; }
    public boolean getCheckIn() { return checkIn; }
    public String getTimeString() {
        return String.format(Locale.US, "%d/%d/%d %02d:%02d", month, day, year, hour, minute);
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public String getMajor(){ return major;}

    @Override
    public int compareTo(Record record) {
        if (year == record.getYear()) {
            if (month == record.getMonth()) {
                if (day == record.getDay()) {
                    if (hour == record.getHour()) {
                        return minute - record.getMinute();
                    }
                    return hour - record.getHour();
                }
                return day - record.getDay();
            }
            return month - record.getMonth();
        }
        return year - record.getYear();
    }
}
