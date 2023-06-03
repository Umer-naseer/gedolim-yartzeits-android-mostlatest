/*
package com.saqibdb.YahrtzeitsOfGedolim;

import android.os.AsyncTask;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import com.saqibdb.YahrtzeitsOfGedolim.activity.CalendarViewActivity;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

 class MakeNotificationTask extends AsyncTask<Void, Void, Void> {

    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;

    public MakeNotificationTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("YahrtzeitsOfGedolim").build();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //displayNotificationProgress(true);
    }

    @Override
    protected Void doInBackground(Void... params) {

        String calendarId = "primary";
        Events googleEventList = null;
        try {

            googleEventList = mService.events().list(calendarId).execute();
        } catch (IOException e) {
            mLastError = e;
            if (mLastError instanceof UserRecoverableAuthIOException) {
                // startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
            }
            //printGetAllEventApiLog("goes horribly wrong from get event list " + e.getMessage());
            e.printStackTrace();
        }

        if (eventOperation.equalsIgnoreCase(deleteEvent)) {

            try {
                mService.calendars().clear(calendarId).execute();

                if (dbHandler == null)
                    dbHandler = new DatabaseHandler(CalendarViewActivity.this);
                dbHandler.updateEventDeleteFromCal();
                printGetAllEventApiLog("isCompleted : " + true + " isAddedGCal : " + false);

            } catch (IOException e) {
                mLastError = e;
                if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
                }
                printGetAllEventApiLog("goes horribly wrong from delete event " + e.getMessage());
                e.printStackTrace();
            }

        } else if (eventOperation.equalsIgnoreCase(insertEvent)) {

            if (dbHandler == null)
                dbHandler = new DatabaseHandler(CalendarViewActivity.this);
            ArrayList<EventDetails> eventList = dbHandler.getAllEventForGcalList();
            printGetAllEventApiLog("###########################################");
            printGetAllEventApiLog("List size : " + eventList.size());
            printGetAllEventApiLog("###########################################");
            for (int i = 0; i < eventList.size(); i++) {
                EventDetails event = eventList.get(i);
                printGetAllEventApiLog((i + 1) + "======================================================");
                java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.set(event.getYear(), event.getMonth() - 1, event.getDay(), 0, 0, 0);
                String date = "" + formatter.format(calendar.getTimeInMillis());
                String enddate = "" + formatter.format(calendar.getTimeInMillis() + 7200000);

                try {

                    DateTime startDateTime = new DateTime(date + "+05:30");
                    EventDateTime start = new EventDateTime().setDateTime(startDateTime);

                    DateTime endDateTime = new DateTime(enddate + "+05:30");
                    EventDateTime end = new EventDateTime().setDateTime(endDateTime);

                    printGetAllEventApiLog("Event : " + event.getSubjectTitle() + " Start : " + date + "+05:30" + " End : " + enddate + "+05:30");
                    String summary = event.getSubjectTitle();
                    String discription = event.getSubjectDescription();
                    Event googleEvent = new Event()
                            .setSummary(summary).setDescription(discription)
                            .setStart(start).setEnd(end);

                    boolean isExist = false;
                    if (googleEventList != null && googleEventList.getItems().size() > 0) {
                        for (int p = 0; p < googleEventList.getItems().size(); p++) {
                            if (googleEventList.getItems().get(p).getSummary().equalsIgnoreCase(summary)
                                    && googleEventList.getItems().get(p).getDescription().equalsIgnoreCase(discription)) {
                                isExist = true;
                            }
                        }
                    }
                    if (isExist) {
                        printGetAllEventApiLog("is Already existed ");

                    } else {
                        mService.events().insert(calendarId, googleEvent).execute();
                        printGetAllEventApiLog("is Inserted successfully");

                    }
                    printGetAllEventApiLog("Success in making Google Calendar event");

                } catch (Exception e) {
                    mLastError = e;
                    if (mLastError instanceof UserRecoverableAuthIOException) {
                        startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
                    }
                    printGetAllEventApiLog("goes horribly wrong from insert event " + e.getMessage());
                    e.printStackTrace();
                }

                if (dbHandler == null)
                    dbHandler = new DatabaseHandler(CalendarViewActivity.this);
                dbHandler.updateEventAddedInGCalCompleted(event);
            }
        }
        eventOperation = "";
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        displayNotificationProgress(false);
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
            } else {
                printGetAllEventApiLog("error when cancelled" + mLastError.getMessage());
            }
        } else {
            printGetAllEventApiLog("cancelled request cancelled");
        }
        displayNotificationProgress(false);
    }
}

*/
