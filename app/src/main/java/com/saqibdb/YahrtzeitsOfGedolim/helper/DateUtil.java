package com.saqibdb.YahrtzeitsOfGedolim.helper;

import android.icu.util.HebrewCalendar;
import android.util.Log;

import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;

import java.util.Calendar;

public class DateUtil {

    static String[] HEBREW_DAY = {"א׳", "ב׳", "ג׳", "ד׳", "ה׳", "ו׳", "ז׳", "ח׳", "ט׳", "י׳", "י״א", "י״ב", "י״ג", "י״ד", "ט״ו", "ט״ז",
            "י״ז", "י״ח", "י״ט", "׳כ", "כ״א", "כ״ב", "כ״ג", "כ״ד", "כ״ה", "כ״ו", "כ״ז", "כ״ח", "כ״ט", "ל׳"};

    public static HebrewDateModel convertGDateToHDate(int year, int month, int day) {
        HebrewDate hebrewDate = new HebrewDate();
        try {
            hebrewDate.setDate(month, day, year);
        } catch (Exception e) {
            return null;
        }
        HebrewDateModel dateModel = new HebrewDateModel();
        dateModel.setGd(hebrewDate.getGregorianDayOfMonth());
        dateModel.setGm(hebrewDate.getGregorianMonth());
        dateModel.setGy(hebrewDate.getGregorianYear());
        dateModel.setHd(hebrewDate.getHebrewDate());
        dateModel.setHy(hebrewDate.getHebrewYear());
        dateModel.setHm(hebrewDate.getHebrewMonthAsString());
        dateModel.setHm_(hebrewDate.getHebrewMonth());
        dateModel.setHebrew(HEBREW_DAY[dateModel.getHd() - 1]);
/*
        HebrewCalendar hebrewCalendar = new HebrewCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        long mils = calendar.getTimeInMillis();
        hebrewCalendar.setTimeInMillis(mils);
        HebrewDateModel dateModel = new HebrewDateModel();
        dateModel.setGd(day);
        dateModel.setGm(month);
        dateModel.setGy(year);
        dateModel.setHd(hebrewCalendar.get(HebrewCalendar.DAY_OF_MONTH));
        dateModel.setHy(hebrewCalendar.get(HebrewCalendar.YEAR));
        int i = hebrewCalendar.get(HebrewCalendar.MONTH) + 6;
        if (i > 12)
            i = i - 12;
        dateModel.setHm(Utility.arrHebrewMonth[i-1]);
        dateModel.setHm_(i);
        dateModel.setHebrew(HEBREW_DAY[dateModel.getHd() - 1]);
*/

        return dateModel;
    }

    public static HebrewDateModel convertHDateToGDate(int year, int month, int day) {
        HebrewDate hebrewDate = new HebrewDate();
        try {
            hebrewDate.setHebrewDate(month, day, year);
        } catch (Exception e) {
            return null;
        }
        HebrewDateModel dateModel = new HebrewDateModel();
        dateModel.setGd(hebrewDate.getGregorianDayOfMonth());
        dateModel.setGm(hebrewDate.getGregorianMonth());
        dateModel.setGy(hebrewDate.getGregorianYear());
        dateModel.setHd(day);
        dateModel.setHy(year);
        dateModel.setHm(hebrewDate.getHebrewMonthAsString());
        dateModel.setHm_(month);
        dateModel.setHebrew(HEBREW_DAY[dateModel.getHd() - 1]);
        return dateModel;
    }
}
