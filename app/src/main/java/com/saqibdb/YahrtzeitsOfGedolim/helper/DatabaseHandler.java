package com.saqibdb.YahrtzeitsOfGedolim.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "DatabaseHandler";

    // Database name
    private static final String DATABASE = "HebrewCalendar";

    // Records table name
    private static final String TABLE_CALENDAR = "tbl_calendar";
    private static final String TABLE_EVENT = "tbl_cal_event";

    // Records Table TABLE_CALENDAR Columns name
    private static final String KEY_DATE = "date";
    private static final String KEY_RESPONSE = "response";

    // Records Table TABLE_EVENT Columns name
    private static final String KEY_EVENT_ID = "eventId";
    private static final String KEY_DAY_MONTH = "dayMonth";
    private static final String KEY_EVENT_OBJ = "eventObj";
    private static final String KEY_EVENT_IS_COMPLETED = "isCompleted";
    private static final String KEY_EVENT_ADDED_ON_GCAL = "isAddedOnGCal";

    public DatabaseHandler(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CALENDER_TABLE = "CREATE TABLE " + TABLE_CALENDAR + "(" + KEY_DATE + " TEXT," + KEY_RESPONSE + " TEXT)";
        db.execSQL(CREATE_CALENDER_TABLE);

        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_EVENT_ID + " INTEGER PRIMARY KEY,"
                + KEY_DAY_MONTH + " TEXT,"
                + KEY_EVENT_IS_COMPLETED + " TEXT,"
                + KEY_EVENT_ADDED_ON_GCAL + " TEXT,"
                + KEY_EVENT_OBJ + " TEXT)";
        db.execSQL(CREATE_EVENT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);

        // Create tables again
        onCreate(db);
    }

    // date = 2018-06-12
    // response = ...

    public boolean deleteEvent(String id) {
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
            return db.delete(TABLE_EVENT, KEY_EVENT_ID + "=" + id, null) > 0;
        } catch (Exception exc) {
            return false;
        }
    }

    // Add all event by list
    public void addEvent(List<EventDetails> getEvent) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < getEvent.size(); i++) {
                EventDetails eventDetails = getEvent.get(i);

/*
                String day = String.valueOf(eventDetails.getDay()).length() == 1 ? "0" + eventDetails.getDay() : "" + eventDetails.getDay();
                String month = String.valueOf(eventDetails.getMonth()).length() == 1 ? "0" + eventDetails.getMonth() : "" + eventDetails.getMonth();
                String dayMonth = day + "-" + month;
                */
                String dayMonth = eventDetails.getDayHebrew()+ "-" + eventDetails.getMonthHebrew();
                Gson gson = new Gson();
                String strEventObj = gson.toJson(eventDetails);

                ContentValues values = new ContentValues();
                values.put(KEY_EVENT_ID, eventDetails.getId());
                values.put(KEY_DAY_MONTH, dayMonth);
                values.put(KEY_EVENT_OBJ, strEventObj);

                Cursor cursor = db.query(TABLE_EVENT, new String[]{KEY_EVENT_ID, KEY_EVENT_IS_COMPLETED}, KEY_EVENT_ID + "=?",
                        new String[]{String.valueOf(eventDetails.getId())}, null, null, null, null);

                int total = cursor.getCount();
                if (total > 0 && cursor.moveToFirst()) {
                    String isCompleted = cursor.getString(cursor.getColumnIndex(KEY_EVENT_IS_COMPLETED));
                    if (!isCompleted.equalsIgnoreCase("1")) {
                        // Updating Row
                        db.update(TABLE_EVENT, values, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventDetails.getId())});
                        Log.d(TAG, "addEvent : " + "UPDATE Event Id : " + eventDetails.getId() + " Event DayMonth : " + dayMonth);
                    } else {
                        Log.d(TAG, "addEvent : " + "Not UPDATE Event Id : " + eventDetails.getId() + " Event DayMonth : " + dayMonth);
                    }
                } else {
                    values.put(KEY_EVENT_IS_COMPLETED, "0");
                    values.put(KEY_EVENT_ADDED_ON_GCAL, "0");
                    // Inserting Row
                    db.insert(TABLE_EVENT, null, values);
                    Log.i(TAG, "addEvent : " + "INSERT Event Id : " + eventDetails.getId() + " Event DayMonth : " + dayMonth);
                }
                cursor.close();
            }
        } catch (Exception exc) {
            Log.d(TAG, "addEvent : " + "EXCEPTION On Event Data INSERT UPDATE Query. " + exc.toString());
        }
//        if (db.isOpen())
//            db.close();
    }

    public Long addEvent(EventDetails eventDetails, int addedOnGCal) {
        SQLiteDatabase db = this.getWritableDatabase();

/*
        String day = String.valueOf(eventDetails.getDay()).length() == 1 ? "0" + eventDetails.getDay() : "" + eventDetails.getDay();
        String month = String.valueOf(eventDetails.getMonth()).length() == 1 ? "0" + eventDetails.getMonth() : "" + eventDetails.getMonth();
        String dayMonth = day + "-" + month;
*/

        String dayMonth = eventDetails.getDayHebrew()+ "-" + eventDetails.getMonthHebrew();

        Gson gson = new Gson();
        String strEventObj = gson.toJson(eventDetails);

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, eventDetails.getId());
        values.put(KEY_DAY_MONTH, dayMonth);
        values.put(KEY_EVENT_OBJ, strEventObj);

        values.put(KEY_EVENT_IS_COMPLETED, "1");
        values.put(KEY_EVENT_ADDED_ON_GCAL, "" + addedOnGCal);
        // Inserting Row
        Long rows=db.insert(TABLE_EVENT, null, values);
        return rows;
    }

    public int updatePrivateEvent(EventDetails eventDetails) {
        SQLiteDatabase db = getWritableDatabase();
//        try {
        String dayMonth = eventDetails.getDayHebrew()+ "-" + eventDetails.getMonthHebrew();

        Gson gson = new Gson();
        String strEventObj = gson.toJson(eventDetails);

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, eventDetails.getId());
        values.put(KEY_DAY_MONTH, dayMonth);
        values.put(KEY_EVENT_OBJ, strEventObj);
        values.put(KEY_EVENT_IS_COMPLETED, "1");
        values.put(KEY_EVENT_ADDED_ON_GCAL, "1");

        try {
            return db.update(TABLE_EVENT, values,  KEY_EVENT_ID + "=" + eventDetails.getId(), null);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }



//        String countQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_EVENT_ID + "=" + eventDetails.getId() + "";
//        Log.d(TAG, "updateEvent : " + countQuery);
//        Cursor cursor = db.rawQuery(countQuery, null);
//        int total = cursor.getCount();
//
//        if (total > 0) {
//            // Updating Row
//            Log.d(TAG, "updateEvent : " + "UPDATE Event Id : " + eventDetails.getId() + " updated Successfully");
//            return db.update(TABLE_EVENT, values, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventDetails.getId())});
//
//        } else {
//            Log.d(TAG, "AllApiDateConversionDB : Event Not found");
//            return 0;
//        }
    }

    public void updateEventCompleted(EventDetails eventDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
//        try {
        Gson gson = new Gson();
        String strEventObj = gson.toJson(eventDetails);
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_IS_COMPLETED, "1");
        values.put(KEY_EVENT_OBJ, strEventObj);

        String countQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_EVENT_ID + "=" + eventDetails.getId() + "";
        Log.d(TAG, "updateEvent : " + countQuery);
        Cursor cursor = db.rawQuery(countQuery, null);
        int total = cursor.getCount();
        if (total > 0) {
            // Updating Row
            db.update(TABLE_EVENT, values, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventDetails.getId())});
            Log.d(TAG, "updateEvent : " + "UPDATE Event Id : " + eventDetails.getId() + " updated Successfully");
        } else {
            Log.d(TAG, "AllApiDateConversionDB : Event Not found");
        }
//        } catch (Exception exc) {
//            Log.d(TAG, "updateEvent : " + "EXCEPTION On Event Data INSERT UPDATE Query. " + exc.toString());
//        }
//        if (db.isOpen())
//            db.close();
    }

    public void updateEventAddedInGCalCompleted(EventDetails eventDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
//        try {
        Gson gson = new Gson();
        String strEventObj = gson.toJson(eventDetails);

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ADDED_ON_GCAL, "1");
        values.put(KEY_EVENT_OBJ, strEventObj);

        String countQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_EVENT_ID + "=" + eventDetails.getId() + "";
        Log.d(TAG, "updateEvent : " + countQuery);
        try {
            Cursor cursor = db.rawQuery(countQuery, null);
            int total = cursor.getCount();
            if (total > 0) {

                db.update(TABLE_EVENT, values, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventDetails.getId())});
                Log.d(TAG, "updateEvent : " + "UPDATE Event Id : " + eventDetails.getId());
            }
        } catch (Exception e) {
        }

    }

    public ArrayList<EventDetails> getNotConversionCompletedEvent() {

        ArrayList<EventDetails> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

//        try {
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;
        Log.d(TAG, "getAllEventList : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // get completed event
                String isCompleted = cursor.getString(cursor.getColumnIndex(KEY_EVENT_IS_COMPLETED));
                if (isCompleted.equalsIgnoreCase("0")) {
                    Gson gson = new Gson();
                    EventDetails records = gson.fromJson(cursor.getString(cursor.getColumnIndex(KEY_EVENT_OBJ)), EventDetails.class);
                    // Adding records to list
                    eventsList.add(records);
                }
            }
            while (cursor.moveToNext());
        }
//        } catch (Exception exc) {
//            Log.d(TAG, "EXCEPTION On getting standings data " + exc.toString());
//        }
//        if (db.isOpen())
//            db.close();
        return eventsList;
    }

    public ArrayList<EventDetails> getAllEventForGcalList() {

        ArrayList<EventDetails> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

//        try {
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;
        Log.d(TAG, "getAllEventList : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // get completed event
                String isCompleted = cursor.getString(cursor.getColumnIndex(KEY_EVENT_IS_COMPLETED));
                String isAdded = cursor.getString(cursor.getColumnIndex(KEY_EVENT_ADDED_ON_GCAL));
                if (isAdded.equalsIgnoreCase("0") && isCompleted.equalsIgnoreCase("1")) {
                    Gson gson = new Gson();
                    EventDetails records = gson.fromJson(cursor.getString(cursor.getColumnIndex(KEY_EVENT_OBJ)), EventDetails.class);
                    // Adding records to list
                    eventsList.add(records);
                }
            }
            while (cursor.moveToNext());
        }
//        } catch (Exception exc) {
//            Log.d(TAG, "EXCEPTION On getting standings data " + exc.toString());
//        }
//        if (db.isOpen())
//            db.close();
        return eventsList;
    }

    // Get Events by Day & Month
    public ArrayList<String> getAllEventList() {
        ArrayList<String> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                eventsList.add(cursor.getString(cursor.getColumnIndex(KEY_EVENT_OBJ)));
            }
            while (cursor.moveToNext());
        }
        return eventsList;
    }


    public ArrayList<EventDetails> getEventListByDayMonth(String day, String month) {
        ArrayList<EventDetails> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String dayMonth = day + "-" + month;

/*
        SimpleDateFormat input = new SimpleDateFormat("dd-MM");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM");
        try {
            Date oneWayTripDate = input.parse(dayMonth);
            dayMonth = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/

        String selectQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_DAY_MONTH + "='" + dayMonth + "'";
        Log.d(TAG, "isAvailableEventByDayMonth : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Gson gson = new Gson();
                EventDetails records = gson.fromJson(cursor.getString(cursor.getColumnIndex(KEY_EVENT_OBJ)), EventDetails.class);
                eventsList.add(records);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return eventsList;
    }



    // SELECT * FROM tbl_event WHERE dayMonth='06-06'
    // Getting Event is Available or not
    public boolean isAvailableEventByDayMonth(int day, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        String dayMonth = day + "-" + month;

        SimpleDateFormat input = new SimpleDateFormat("dd-MM");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM");
        try {
            Date oneWayTripDate = input.parse(dayMonth);
            dayMonth = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String selectQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_DAY_MONTH + "='" + dayMonth + "'";
        Log.d(TAG, "isAvailableEventByDayMonth : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean isAvail = false;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToLast();
            isAvail = true;
        }
        cursor.close();
//        if (db.isOpen())
//            db.close();
        return isAvail;
    }

    // SELECT * FROM tbl_event WHERE dayMonth='15-05'
    public void updateHebrewApiData(HebrewDateModel hebrewDateModel) {
        SQLiteDatabase db = this.getWritableDatabase();
//        try {
        String day = String.valueOf(hebrewDateModel.getHd()).length() == 1 ? "0" + hebrewDateModel.getHd() : "" + hebrewDateModel.getHd();
        int hMonth = (Arrays.asList(Utility.arrHebrewMonth)).indexOf(hebrewDateModel.getHm()) + 1;
        String month = String.valueOf(hMonth).length() == 1 ? "0" + hMonth : "" + hMonth;
        String dayMonth = day + "-" + month;
        String selectQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_DAY_MONTH + "='" + dayMonth + "'";
        Log.d(TAG, "updateHebrewApiData : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String isCompleted = cursor.getString(cursor.getColumnIndex(KEY_EVENT_IS_COMPLETED));
                if (!isCompleted.equalsIgnoreCase("1")) {
                    ContentValues values = new ContentValues();
                    values.put(KEY_DAY_MONTH, cursor.getString(cursor.getColumnIndex(KEY_DAY_MONTH)));

                    Gson gson = new Gson();
                    EventDetails records = gson.fromJson(cursor.getString(cursor.getColumnIndex(KEY_EVENT_OBJ)), EventDetails.class);

                    String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                    records.setDay(hebrewDateModel.getGd());
                    records.setMonth(hebrewDateModel.getGm());
                    records.setYear(hebrewDateModel.getGy());
                    records.setDayHebrewStr("" + arrStr[0].trim());
                    records.setDayHebrew("" + hebrewDateModel.getHd());
                    records.setMonthHebrew("" + hebrewDateModel.getHm_());
                    records.setMonthHebrewStr("" + hebrewDateModel.getHm());
                    records.setYearHebrew("" + hebrewDateModel.getHy());

                    gson = new Gson();
                    String strEventObj = gson.toJson(records);
                    values.put(KEY_EVENT_OBJ, strEventObj);
                    values.put(KEY_EVENT_IS_COMPLETED, "1");

                    db.update(TABLE_EVENT, values, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_EVENT_ID)))});
                    Log.d(TAG, "updateHebrewApiData : " + "UPDATE Event DayMonth : " + dayMonth);
                } else
                    Log.d(TAG, "updateHebrewApiData : " + "Not UPDATE Event DayMonth : " + dayMonth);
            }
            while (cursor.moveToNext());
        }
//        } catch (Exception exc) {
//            Log.d(TAG, "EXCEPTION On Event Data INSERT UPDATE Query. " + exc.toString());
//        }
//        if (db.isOpen())
//            db.close();
    }

    public String getHebrewMonthYear(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String result = "";
//        try {
        String selectQuery = "SELECT * FROM " + TABLE_CALENDAR + " WHERE " + KEY_DATE + "='" + date + "'";
        Log.d(TAG, "getHebrewMonthYear : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        String hebrewMonthYear = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Gson gson = new Gson();
            HebrewDateModel records = gson.fromJson(cursor.getString(cursor.getColumnIndex(KEY_RESPONSE)), HebrewDateModel.class);

            hebrewMonthYear = records.getHm() + " " + records.getHy();
        }
        result = hebrewMonthYear;
//        } catch (Exception exc) {
//            Log.d(TAG, "EXCEPTION On getting Hebrew Month Year " + exc.toString());
//        }
//        if (db.isOpen())
//            db.close();
        return result;
    }

    public void addUpdateHebrewDateWise(String date, String response) {

        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_DATE, date);
            values.put(KEY_RESPONSE, response);

            String countQuery = "SELECT * FROM " + TABLE_CALENDAR + " WHERE " + KEY_DATE + "='" + date + "'";
            Log.d(TAG, "addUpdateHebrewDateWise : " + countQuery);
            Cursor cursor = db.rawQuery(countQuery, null);
            int total = cursor.getCount();
            if (total > 0) {
                // Updating Row
                db.update(TABLE_CALENDAR, values, KEY_DATE + " = ?", new String[]{String.valueOf(date)});
                Log.d(TAG, "addUpdateHebrewDateWise : " + "UPDATE Date " + date + " => " + date + " - " + response);

            } else {
                // Inserting Row
                db.insert(TABLE_CALENDAR, null, values);
                Log.d(TAG, "addUpdateHebrewDateWise : " + "INSERT Date " + date + " => " + date + " - " + response);
            }

        } catch (SQLException s) {
            Log.e(TAG, "Error with DB Open : " + s.toString());
            return;
        } catch (Exception exc) {
            Log.e(TAG, "Exception in : " + exc.toString());
            return;
        }
    }

    // Select * from tbl_calendar where date = '2018-08-17'
    // date = 2018-07-21
    public String getSpecificDate(String date) {
        String result = "";
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
            String seleceQuery = "Select * from " + TABLE_CALENDAR + " where " + KEY_DATE + " = '" + date + "'";
            Log.d(TAG, "getSpecificDate : " + seleceQuery);
            Cursor cursor = null;
            if (db.isOpen()) {
                cursor = db.rawQuery(seleceQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToLast();
                    String strResponse = cursor.getString(cursor.getColumnIndex(KEY_RESPONSE));
                    cursor.close();
                    result = strResponse;
                } else {
                    if (cursor != null)
                        cursor.close();
                    return "";
                }
            } else {
                return "";
            }
        } catch (SQLException s) {
            Log.e(TAG, "Error with DB Open : " + s.toString());
            return "";
        } catch (Exception exc) {
            Log.e(TAG, "Exception in : " + exc.toString());
            return "";
        }
//        if (db.isOpen())
//            db.close();
        return result;
    }

    // value = 2018-07-11
    public boolean checkHebDateIsExistsOrNot(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String seleceQuery = "Select * from " + TABLE_CALENDAR + " where " + KEY_DATE + " = '" + value + "'";
        Log.d(TAG, "checkHebDateIsExistsOrNot: " + seleceQuery);
        Cursor cursor = db.rawQuery(seleceQuery, null);
        if (cursor != null && cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
//        if (db.isOpen())
//            db.close();
        return true;
    }

    public void updateEventDeleteFromCal() {
        SQLiteDatabase db = this.getWritableDatabase();
//        try {
        ArrayList<EventDetails> eventsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;
        Log.d(TAG, "updateEventDeleteFromCal : " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String isAdded = cursor.getString(cursor.getColumnIndex(KEY_EVENT_ADDED_ON_GCAL));
                if (isAdded.equalsIgnoreCase("1")) {
                    Gson gson = new Gson();
                    EventDetails records = gson.fromJson(cursor.getString(cursor.getColumnIndex(KEY_EVENT_OBJ)), EventDetails.class);
                    // Adding records to list
                    eventsList.add(records);
                }
            }
            while (cursor.moveToNext());
        }

        for (int i = 0; i < eventsList.size(); i++) {
            EventDetails eventDetails = eventsList.get(i);
            Gson gson = new Gson();
            String strEventObj = gson.toJson(eventDetails);

            ContentValues values = new ContentValues();
            values.put(KEY_EVENT_ADDED_ON_GCAL, "0");
            values.put(KEY_EVENT_OBJ, strEventObj);

            db.update(TABLE_EVENT, values, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventDetails.getId())});
            Log.d(TAG, "updateEventDeleteFromCal : " + "UPDATE Event Id : " + eventDetails.getId());
        }
//        } catch (Exception exc) {
//            Log.d(TAG, "updateEventDeleteFromCal : " + "EXCEPTION On Event Data INSERT UPDATE Query. " + exc.toString());
//        }
//        if (db.isOpen())
//            db.close();
    }

}