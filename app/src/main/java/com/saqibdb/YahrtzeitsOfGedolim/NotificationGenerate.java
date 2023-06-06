package com.saqibdb.YahrtzeitsOfGedolim;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.helper.SharedPreferencesHelper;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class NotificationGenerate extends AsyncTask<Void, Void, Void> {
    Context context;
    DatabaseHandler dbHandler;
    public static int ALL = 1;
    public static int NEW = 0;
    int type;
    Calendar calendar;
    boolean onOffNotification = true;
    AlarmManager alarmManager;

    public NotificationGenerate(Context context, int type) {
        this.context = context;
        this.type = type;
        calendar = Calendar.getInstance();
    }

    public NotificationGenerate(Context context, int type, boolean onOffNotification) {
        this.context = context;
        this.type = type;
        calendar = Calendar.getInstance();
        this.onOffNotification = onOffNotification;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        setEventInGoogleCalendar(type);
        return null;
    }

    public void setEventInGoogleCalendar(int type) {
        if (dbHandler == null)
            dbHandler = new DatabaseHandler(context);
        ArrayList<EventDetails> eventList;
        if (type == NEW)
            eventList = dbHandler.getAllEventForGcalList();
        else {
            eventList = new ArrayList<>();
            Gson gson = new Gson();
            for (String s : dbHandler.getAllEventList()) {
                EventDetails records = gson.fromJson(s, EventDetails.class);
                eventList.add(records);
            }
        }
        Log.e("eventTesting",""+eventList.size());
        ArrayList<EventDetails> newEventList = new ArrayList<>();
        for (int i = 0; i < eventList.size(); i++) {
            EventDetails event = eventList.get(i);
            if (event == null || event.getMonth() == null || event.getSubjectTitle() == null || event.getDay() == null)
                continue;
            event.setDateFull(event.getYear().toString());
            if (event.getMonth().toString().length() == 1)
                event.setDateFull(event.getDateFull() + "0" + event.getMonth());
            else
                event.setDateFull(event.getDateFull() + event.getMonth());
            if (event.getDay().toString().length() == 1)
                event.setDateFull(event.getDateFull() + "0" + event.getDay());
            else
                event.setDateFull(event.getDateFull() + event.getDay());

            newEventList.add(event);
            //updateEventAfterNotification(event);
        }

        class CustomComparator implements Comparator<EventDetails> {
            @Override
            public int compare(EventDetails o1, EventDetails o2) {
                return o1.getDateFull().compareTo(o2.getDateFull());
            }
        }
        newEventList.sort(new CustomComparator());

        ArrayList<EventDetails> notificationEventList = new ArrayList<>();

        for (int i = 1; i < newEventList.size(); i++) {
            EventDetails event = newEventList.get(i);
            if (notificationEventList.size() == 0)
                notificationEventList.add(event);
            else {
                EventDetails notificationEvent = notificationEventList.get(notificationEventList.size() - 1);
                if (notificationEvent.getDateFull().equals(event.getDateFull()))
                    notificationEvent.setSubjectTitle(notificationEvent.getSubjectTitle() + " , " + event.getSubjectTitle());
                else
                    notificationEventList.add(event);
            }
        }
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < notificationEventList.size(); i++) {
            Calendar temp = Calendar.getInstance();
            temp.set(Calendar.YEAR, notificationEventList.get(i).getYear());
            temp.set(Calendar.MONTH, notificationEventList.get(i).getMonth() - 1);
            temp.set(Calendar.DAY_OF_MONTH, notificationEventList.get(i).getDay());

//            if (temp.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
//                // when it's Friday show events of Saturday
//                setLocalNotificationForSatOnFri(notificationEventList.get(i), notificationEventList.get(i+1).getSubjectTitle());
//                Log.d("tag", String.valueOf(temp.get(Calendar.DAY_OF_WEEK)));
//            } else {
//            }
            if (notificationEventList.indexOf(notificationEventList.get(i)) + 1 < notificationEventList.size())
                setLocalNotification(notificationEventList.get(i), notificationEventList.get(i+1).getSubjectTitle());

        }
    }

    private void setLocalNotification(EventDetails event, String subjectTitle) {
        if (event == null || event.getMonth() == null || event.getSubjectTitle() == null || event.getDay() == null)
            return;

        if (event.getYear() > calendar.get(Calendar.YEAR)) ;
        else if (event.getYear() == calendar.get(Calendar.YEAR) && event.getMonth() > calendar.get(Calendar.MONTH) + 1)
            ;
        else if (event.getYear() == calendar.get(Calendar.YEAR) && event.getMonth() == calendar.get(Calendar.MONTH) + 1
                && event.getDay() > calendar.get(Calendar.DAY_OF_MONTH)) ;
        else return;


        calendar.set(Calendar.YEAR, event.getYear());
        calendar.set(Calendar.MONTH, event.getMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, event.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_HOURS, 8));
        calendar.set(Calendar.MINUTE, SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_HOURS, 0));

        Intent notificationIntent = new Intent(context, BroadcastManager.class);
        notificationIntent.putExtra("msg", event.getSubjectTitle());
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            notificationIntent.putExtra("msg", event.getSubjectTitle() + "\nShabbos Yartzeits: " + subjectTitle);
        } else {
            notificationIntent.putExtra("msg", event.getSubjectTitle());
        }
        notificationIntent.putExtra("date", event.getDateFull());
        notificationIntent.putExtra("id", event.getId());

        PendingIntent broadcast = PendingIntent.getBroadcast(context, event.getId(), notificationIntent, PendingIntent.FLAG_MUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
        if (!SharedPreferencesHelper.getInstance().getBoolean(Constants.TURN_NOTIFICATION_ON_OFF, true)){
            alarmManager.cancel(broadcast);
        }
    }

    private void setLocalNotificationForSatOnFri(EventDetails event, String subjectTitle) {
        if (event == null || event.getMonth() == null || event.getSubjectTitle() == null || event.getDay() == null)
            return;

        if (event.getYear() > calendar.get(Calendar.YEAR)) ;
        else if (event.getYear() == calendar.get(Calendar.YEAR) && event.getMonth() > calendar.get(Calendar.MONTH) + 1)
            ;
        else if (event.getYear() == calendar.get(Calendar.YEAR) && event.getMonth() == calendar.get(Calendar.MONTH) + 1
                && event.getDay() > calendar.get(Calendar.DAY_OF_MONTH)) ;
        else return;

        Intent notificationIntent = new Intent(context, BroadcastManager.class);
        notificationIntent.putExtra("msg", event.getSubjectTitle() + "\nSaturday Events: " + subjectTitle);
        notificationIntent.putExtra("date", event.getDateFull());
        notificationIntent.putExtra("id", event.getId());

        PendingIntent broadcast = PendingIntent.getBroadcast(context, event.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        calendar.set(Calendar.YEAR, event.getYear());
        calendar.set(Calendar.MONTH, event.getMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, event.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_HOURS, 8));
        calendar.set(Calendar.MINUTE, SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_HOURS, 0));

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
        if (!SharedPreferencesHelper.getInstance().getBoolean(Constants.TURN_NOTIFICATION_ON_OFF, true)){
            alarmManager.cancel(broadcast);
        }
    }


/*
    private void setLocalNotification2(EventDetails event) {
        if (event == null || event.getMonth() == null || event.getSubjectTitle() == null || event.getDay() == null)
            return;

        Calendar cal = Calendar.getInstance();

        if (event.getYear() > cal.get(Calendar.YEAR)) ;
        else if (event.getYear() == cal.get(Calendar.YEAR) && event.getMonth() > cal.get(Calendar.MONTH) + 1)
            ;
        else if (event.getYear() == cal.get(Calendar.YEAR) && event.getMonth() == cal.get(Calendar.MONTH) + 1
                && event.getDay() > cal.get(Calendar.DAY_OF_MONTH)) ;
        else return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, BroadcastManager.class);
        notificationIntent.putExtra("msg", event.getSubjectTitle());
        notificationIntent.putExtra("date", event.getDateFull());

        PendingIntent broadcast = PendingIntent.getBroadcast(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        cal.set(event.getYear(), event.getMonth() - 1, event.getDay(), 8, 0, 0);
        Log.e("eventTesting: " + event.getDateFull(), event.getSubjectTitle());

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }
*/

    private void updateEventAfterNotification(EventDetails event) {
        if (dbHandler == null)
            dbHandler = new DatabaseHandler(context);
        dbHandler.updateEventAddedInGCalCompleted(event);
    }

}
