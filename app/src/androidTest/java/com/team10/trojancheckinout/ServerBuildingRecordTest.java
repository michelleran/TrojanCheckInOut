package com.team10.trojancheckinout;

import android.util.Log;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;
import com.team10.trojancheckinout.model.User;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.team10.trojancheckinout.TestUtils.sleep;
import static org.junit.Assert.*;
import static com.team10.trojancheckinout.TestUtils.*;

@RunWith(AndroidJUnit4.class)
public class ServerBuildingRecordTest {

    @After
    public void logout() {
        Server.logout();
    }

    @Test
    public void getValidBuilding() {
        Server.getBuilding("FGhcVP2KLHA6VgA1ceFr", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                Log.d("ServerTest", result.getName());
                assertEquals("FGhcVP2KLHA6VgA1ceFr", result.getId());
            }

            @Override
            public void onFailure(Exception exception) {
            }
        });
        sleep(WAIT_DATA);
    }

    @Test
    public void getInvalidBuilding(){
        Server.getBuilding("testBuilding", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
            }

            @Override
            public void onFailure(Exception exception) {
                assertEquals(exception.getMessage(),"No such document");
            }
        });
        sleep(WAIT_DATA);
    }

    @Test
    public void addBuilding(){
        // Add building
        Server.addBuilding("testBuilding", 20, new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                assertEquals("testBuilding", result.getName());
                assertEquals(20, result.getMaxCapacity());
                // Get added building
                Server.getBuilding(result.getId(), new Callback<Building>() {
                    @Override
                    public void onSuccess(Building result2) {
                        Log.d("ServerTest", result2.getName());
                        assertEquals("testBuilding", result2.getName());
                        assertEquals(20, result2.getMaxCapacity());
                        // Remove building
                        Server.removeBuilding(result2.getId(), new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Log.d("ServerTest", "Success");
                            }

                            @Override
                            public void onFailure(Exception exception) {

                            }
                        });
                        sleep(WAIT_DATA);
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });

                sleep(WAIT_DATA);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("ServerTest", exception.getMessage());
            }
        });
        sleep(WAIT_DATA*3);

    }

    @Test
    public void checkInNoCurrentBuilding(){
        // Login in
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        // Checkout of any building
        Server.checkOut(new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        // Check in to new building
        Server.checkIn("FGhcVP2KLHA6VgA1ceFr", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                assertEquals("FGhcVP2KLHA6VgA1ceFr", result.getId());
                assertEquals(1, result.getCurrentCapacity());
                Server.getCurrentUser(new Callback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        Student s = (Student) result;
                        assertEquals("FGhcVP2KLHA6VgA1ceFr", s.getCurrentBuilding());
                        Log.d("ServerTest", "Success");
                    }

                    @Override
                    public void onFailure(Exception exception) {
                    }
                });
                sleep(WAIT_DATA);

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("ServerTest", exception.getMessage());

            }
        });
        sleep(WAIT_DATA);

        // Check out of building
        Server.checkOut(new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);
    }

    @Test
    public void checkInCurrentBuildingNotNull(){
        // Login
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        // Check out of any building
        Server.checkOut(new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        // Check into a building
        Server.checkIn("FGhcVP2KLHA6VgA1ceFr", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                Server.getCurrentUser(new Callback<User>() {
                    @Override
                    public void onSuccess(User result) {
                    }

                    @Override
                    public void onFailure(Exception exception) {
                    }
                });
                sleep(WAIT_DATA);

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("ServerTest", exception.getMessage());

            }
        });
        sleep(WAIT_DATA);

        // Check into another building
        Server.checkIn("rsoJxXgGzYy9uvmsk8Yt", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                assertEquals("rsoJxXgGzYy9uvmsk8Yt", result.getId());
                assertEquals(1, result.getCurrentCapacity());
                Server.getCurrentUser(new Callback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        Student s = (Student) result;
                        assertEquals("rsoJxXgGzYy9uvmsk8Yt", s.getCurrentBuilding());
                        Server.getBuilding("FGhcVP2KLHA6VgA1ceFr", new Callback<Building>() {
                            @Override
                            public void onSuccess(Building result) {
                                Log.d("ServerTest", "Success");
                                assertEquals(0,result.getCurrentCapacity());
                            }

                            @Override
                            public void onFailure(Exception exception) {

                            }
                        });
                        sleep(WAIT_DATA);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                    }
                });
                sleep(WAIT_DATA);

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("ServerTest", exception.getMessage());

            }
        });
        sleep(WAIT_DATA);

        // Check out of building
        Server.checkOut(new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

    }

    @Test
    public void checkInBuildingFull() {
        // Login
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);
        // Add building
        Server.addBuilding("testBuilding", 0, new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                Server.checkIn(result.getId(), new Callback<Building>() {
                    @Override
                    public void onSuccess(Building result2) {
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d("ServerTest", "Success");
                        assertEquals("Building is full", exception.getMessage());
                        // Remove added building
                        Server.removeBuilding(result.getId(), new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                            }

                            @Override
                            public void onFailure(Exception exception) {
                            }
                        });
                        sleep(WAIT_DATA);
                    }
                });
                sleep(WAIT_DATA*2);

            }

            @Override
            public void onFailure(Exception exception) {
            }
        });
        sleep(WAIT_DATA*7);

    }

    @Test
    public void checkInBuildingTwice() {
        // Login
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        // Check In to building
        Server.checkIn("FGhcVP2KLHA6VgA1ceFr", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        // Check in to same building
        Server.checkIn("FGhcVP2KLHA6VgA1ceFr", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("ServerTest", "Success");
                assertEquals("Student is already checked into this building", exception.getMessage());
                // Check out from building
                Server.checkOut(new Callback<Building>() {
                    @Override
                    public void onSuccess(Building result) {
                        Log.d("ServerTest", "Checked Out");
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });
                sleep(WAIT_DATA);

            }
        });
        sleep(WAIT_DATA*5);


    }

    @Test
    public void checkOutValid(){
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        // Check In
        Server.checkIn("FGhcVP2KLHA6VgA1ceFr", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                Log.d("ServerTest", "Checked In");
                // Check Out
                Server.checkOut(new Callback<Building>() {
                    @Override
                    public void onSuccess(Building result) {
                        Log.d("ServerTest", "Checked Out");
                        assertEquals(0, result.getCurrentCapacity());
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });
                sleep(WAIT_DATA);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA*2);


        // Get user and check their current building
        Server.getCurrentUser(new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                Student s = (Student) result;
                Log.d("ServerTest", s.getGivenName());
                assertEquals(null, s.getCurrentBuilding());
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA*2);

    }

    @Test
    public void checkOutNotValid(){

        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA);

        Server.checkOut(new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("ServerTest", "Success");
                assertEquals("Student is not checked into a building", exception.getMessage());
            }
        });
        sleep(WAIT_DATA*5);

    }

    @Test
    public void filterByStudentID(){
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                Student s = (Student) result;
                // Filter by ID
                Server.filterRecords(-1, -1, -1, -1, -1,
                        -1, -1, -1, -1, -1, "",
                        s.getId(), "", new Callback<Record>() {
                            @Override
                            public void onSuccess(Record result2) {
                                Log.d("ServerTest", "Success");
                                assertEquals(result.getUid(), result2.getStudentUid());
                            }

                            @Override
                            public void onFailure(Exception exception) {
                            }
                        });
                sleep(WAIT_DATA);

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA*5);


    }

    @Test
    public void filterByBuildingID(){
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                // Filter by building name
                Server.filterRecords(-1, -1, -1, -1, -1,
                        -1, -1, -1, -1, -1, "Doheny",
                        "", "", new Callback<Record>() {
                            @Override
                            public void onSuccess(Record result2) {
                                Log.d("ServerTest", "Success");
                                assertEquals("Doheny", result2.getBuildingName());
                            }

                            @Override
                            public void onFailure(Exception exception) {
                            }
                        });
                sleep(WAIT_DATA);

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA*5);

    }

    @Test
    public void filterByStudentIDAndBuildingID(){
        Server.login("vkher@usc.edu", "123456789", new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                Student s = (Student) result;
                Server.filterRecords(-1, -1, -1, -1, -1,
                        -1, -1, -1, -1, -1, "Leavey",
                        s.getId(), "", new Callback<Record>() {
                            @Override
                            public void onSuccess(Record result2) {
                                Log.d("ServerTest", "Success");
                                assertEquals("Leavey", result2.getBuildingName());
                                assertEquals(result.getUid(), result2.getStudentUid());
                            }

                            @Override
                            public void onFailure(Exception exception) {
                            }
                        });
                sleep(WAIT_DATA);

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
        sleep(WAIT_DATA*5);

    }
}

