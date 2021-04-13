# TrojanCheckInOut
CSCI 310 Team 10
## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Features](#features)
* [Setup](#setup)

## General Info
Trojan Check In/Out is a building capacity management and contact tracing Android application built in response to COVID-19 for CSCI 310. Students can create accounts and scan QR codes to check in and out of buildings. Designated "managers" can modify building capacities and view students' visit histories.

## Technologies
Project is created with:
* **Android Framework** - implemented backend in Java and UI in XML
* **Google Firebase** - used for database storage and authentication
* [`zxing`](https://github.com/zxing/zxing) "Zebra Crossing" - barcode image processing library

## Features
### Basic Features
* Users can register as a student or a manager with their USC email address
* During the registration, a student must provide their email, major (drop-down menu), id, name, password and a profile picture. A manager must provide their email, name, password and a profile picture.
* Once the user signed in, they can sign out or delete account
* In a user profile, users can update their profile picture
* Student account can see their visiting history
* Manager account can see the list of UPC buildings with current number of students and capacity for each building
* Students can check in by scanning a QR-code generated from the manager
* QR code for each building is unique
* Students can check out by scanning a QR-code generated from the manager or clicking a “check out” button in their profile.
* After some students check in and check out, mangers should be able to see the changes in the building simultaneously
### Additional Features
* Password can be changed
* Filter records by start date/time, end date/time, building name, student id, and/or major
## Setup
To run this project:
* Clone repository into your IDE (Android Studio)
* Click "Make Project"
* Run on emulator by clicking "Run" button
## CSV file format
Managers can import a CSV file in order to update the capacities of existing buildings.
CSV files must be of this format:
* Each update record must be on a new line
* Each update record must be in the following format: `U`,`<NAME_OF_BUILDING>`,`<MAX_CAPACITY>`
* * Replace <NAME_OF_BUILDING> with the name of the building that you wish to update
* * Replace <MAX_CAPACITY> with the new maximum capacity. This **must** be an integer.