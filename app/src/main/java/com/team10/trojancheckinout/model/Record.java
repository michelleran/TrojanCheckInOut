package com.team10.trojancheckinout.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Record {
    private String studentUid;
    private String studentId;
    private String givenName;
    private String surname;
    private String major;

    private String buildingId;
    private String buildingName;
    private boolean checkIn;

    private String time;
    private long epochTime;

    public static final ZoneId pst = ZoneId.of("America/Los_Angeles");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z");

    public Record() { }

    public Record(Student student, String buildingId, String buildingName, boolean checkIn) {
        this.studentUid = student.getUid();
        this.studentId = student.getId();
        this.givenName = student.getGivenName();
        this.surname = student.getSurname();
        this.major = student.getMajor();

        this.buildingId = buildingId;
        this.buildingName = buildingName;

        ZonedDateTime now = ZonedDateTime.now(pst);
        this.time = now.format(formatter);
        this.epochTime = now.toEpochSecond();

        this.checkIn = checkIn;
    }

    public String getStudentUid() { return studentUid; }
    public String getStudentId() { return studentId; }
    public String getGivenName() { return givenName; }
    public String getSurname() { return surname; }
    public String getMajor(){ return major;}

    public String getBuildingId() { return buildingId; }
    public String getBuildingName() { return buildingName; }
    public boolean getCheckIn() { return checkIn; }

    public String getTime() { return time; }
    public long getEpochTime() { return epochTime; }
}
