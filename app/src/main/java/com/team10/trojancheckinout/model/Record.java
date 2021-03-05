package com.team10.trojancheckinout.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Record {
    private String studentId;
    private String buildingId;
    private LocalDateTime time;
    private boolean checkIn;

    private static final ZoneId pst = ZoneId.of("America/Los_Angeles");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    public Record() { }
    public Record(String buildingId, boolean checkIn) {
        this.buildingId = buildingId;
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
}
