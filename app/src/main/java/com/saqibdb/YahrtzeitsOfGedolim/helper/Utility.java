package com.saqibdb.YahrtzeitsOfGedolim.helper;

import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;

import java.util.ArrayList;
import java.util.Calendar;

public class Utility {

    public static final String ATTRIBUTE_APPNAME = "CalendarEvent";
    public static final String ATTRIBUTE_NOTIFICATION_ENABLED = "isNotificationEnabled";
    public static final String ATTRIBUTE_LAST_DATE_OF_COMPLETELY_API_CALLED = "LastDate";
    public static final String[] arrHebrewMonth = new String[]{"Nisan", "Iyyar", "Sivan", "Tamuz", "Av", "Elul", "Tishrei", "Cheshvan", "Kislev", "Tevet", "Shvat", "Adar I", "Adar II"};
    public static ArrayList<String> SYNCED_LIST = new ArrayList<>();
    public static final String TODAY_CONVERSION_MODEL = "todayConversionModel";
    public static int H_YEAR = 0;
    public static int G_YEAR = 0;
    public static boolean RELOAD= true;


    public static int getMonthInt(String hm) {
        for (int i = 0; i < arrHebrewMonth.length; i++) {
            if (arrHebrewMonth[i].equalsIgnoreCase(hm)) {
                return (i + 1);
            }
        }
        return 13;
    }

    public static boolean checkForDateIsOld(HebrewDateModel hebrewDateModel) {
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(calendarToday.get(Calendar.YEAR), calendarToday.get(Calendar.MONTH), calendarToday.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        long todayMS = calendarToday.getTimeInMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(hebrewDateModel.getGy(), hebrewDateModel.getGm() - 1, hebrewDateModel.getGd(), 0, 0, 0);
        long eventMS = calendar.getTimeInMillis();
        if (eventMS < todayMS) {
            return true;
        }
        return false;
    }
}
