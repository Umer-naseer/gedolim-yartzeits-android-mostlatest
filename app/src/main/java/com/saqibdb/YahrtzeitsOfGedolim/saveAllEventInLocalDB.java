package com.saqibdb.YahrtzeitsOfGedolim;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DateUtil;
import com.saqibdb.YahrtzeitsOfGedolim.helper.SharedPreferencesHelper;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.GetEvent;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;
import com.saqibdb.YahrtzeitsOfGedolim.network.retrofit.ApiManager;
import com.saqibdb.YahrtzeitsOfGedolim.network.retrofit.request.RequestBodyFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 15-08-2018, 05:09:44 PM.
 */

public class saveAllEventInLocalDB extends AsyncTask<Void, String, Void> {

    Activity mActivity;
    HebrewDateModel todayHebrewDateModel;
    DatabaseHandler dbHandler;
    OnComplete onComplete;

    ArrayList<EventDetails> list;

    private static final String TAG = saveAllEventInLocalDB.class.getSimpleName();

    public saveAllEventInLocalDB(Activity mActivity, HebrewDateModel todayHebrewDateModel, OnComplete onComplete, ArrayList<EventDetails> list) {
        this.mActivity = mActivity;
        this.todayHebrewDateModel = todayHebrewDateModel;
        this.onComplete = onComplete;
        this.list = list;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onComplete != null)
            onComplete.onComplete();

        for (int i = 0; i < list.size(); i++) {

            EventDetails event = list.get(i);
            new ConvertAndSave(event).execute();
/*
                    HebrewDateModel hebrewDateModel = DateUtil.convertHDateToGDate(todayHebrewDateModel.getHy(), event.getMonth(), event.getDay());
                    if (hebrewDateModel == null)
                        continue;
                    String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                    event.setDay(hebrewDateModel.getGd());
                    event.setMonth(hebrewDateModel.getGm());
                    event.setYear(hebrewDateModel.getGy());
                    event.setDayHebrewStr("" + arrStr[0].trim());
                    event.setDayHebrew("" + hebrewDateModel.getHd());
                    event.setMonthHebrew("" + hebrewDateModel.getHm_());
                    event.setMonthHebrewStr("" + hebrewDateModel.getHm());
                    event.setYearHebrew("" + hebrewDateModel.getHy());
                    dbHandler.addEvent(event, 0);
*/
        }

        if (list.size() > 0){
            new NotificationGenerate(mActivity, NotificationGenerate.ALL).execute();
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (Constants.DEBUG) {
            ApiManager apiManager = new ApiManager();
            RequestBodyFactory factory = new RequestBodyFactory();

            List<EventDetails> eventDetails = apiManager.makeRequest(factory.createRequestBody());
//            if (!eventDetails.isEmpty()) {
//                list.addAll(eventDetails); // commented for now
//            } else {
//                Log.d(TAG, "[INFO]: List taken from api is empty");
//            }
        }

        boolean isSaved = SharedPreferencesHelper.getInstance().getBoolean("isDataSaved_", false);
        if (!isSaved) {
            Gson gson = new Gson();
            GetEvent getEvent = gson.fromJson(loadJSONFromAsset(), GetEvent.class);
            if (getEvent != null && getEvent.getEventDetails() != null && getEvent.getEventDetails().size() > 0) {
                if (dbHandler == null)
                    dbHandler = new DatabaseHandler(mActivity);
                list.addAll(getEvent.getEventDetails());

                //dbHandler.addEvent(getEvent.getEventDetails());
            }
            if (onComplete != null)
                onComplete.onComplete();
            SharedPreferencesHelper.getInstance().setBoolean("isDataSaved_", true);
        }
        if (todayHebrewDateModel == null)
            return null;
        //doLocalEventNotConversionCompleted();
        return null;
    }

    int lastPos = 0;


    private void doLocalEventNotConversionCompleted() {

        if (dbHandler == null)
            dbHandler = new DatabaseHandler(mActivity);
        ArrayList<EventDetails> eventList = dbHandler.getNotConversionCompletedEvent();
        if (eventList != null && eventList.size() > 0) {
            lastPos = eventList.size();
            for (int i = 0; i < eventList.size(); i++) {
                EventDetails event = eventList.get(i);
                new GetSelectedHebrewDateToEnglish(i, event).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }
    }

    private class ConvertAndSave extends AsyncTask<Void, Void, Void> {

        private EventDetails event;
        private int position;

        public ConvertAndSave(EventDetails event) {
            this.event = event;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HebrewDateModel hebrewDateModel = DateUtil.convertHDateToGDate(todayHebrewDateModel.getHy(), event.getMonth(), event.getDay());
            if (hebrewDateModel == null)
                return null;
            String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
            event.setDay(hebrewDateModel.getGd());
            event.setMonth(hebrewDateModel.getGm());
            event.setYear(hebrewDateModel.getGy());
            event.setDayHebrewStr("" + arrStr[0].trim());
            event.setDayHebrew("" + hebrewDateModel.getHd());
            event.setMonthHebrew("" + hebrewDateModel.getHm_());
            event.setMonthHebrewStr("" + hebrewDateModel.getHm());
            event.setYearHebrew("" + hebrewDateModel.getHy());
            dbHandler.addEvent(event, 0);
            return null;
        }
    }

    private class GetSelectedHebrewDateToEnglish extends AsyncTask<Void, HebrewDateModel, HebrewDateModel> {

        private EventDetails event;
        private int position;

        public GetSelectedHebrewDateToEnglish(int position, EventDetails event) {
            this.position = position;
            this.event = event;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HebrewDateModel doInBackground(Void... params) {
            HebrewDateModel hebrewDateModel = DateUtil.convertHDateToGDate(todayHebrewDateModel.getHy(), event.getMonth(), event.getDay());
            if (hebrewDateModel != null) {
                String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");

                event.setDay(hebrewDateModel.getGd());
                event.setMonth(hebrewDateModel.getGm());
                event.setYear(hebrewDateModel.getGy());
                event.setDayHebrewStr("" + arrStr[0].trim());
                event.setDayHebrew("" + hebrewDateModel.getHd());
                event.setMonthHebrew("" + hebrewDateModel.getHm_());
                event.setMonthHebrewStr("" + hebrewDateModel.getHm());
                event.setYearHebrew("" + hebrewDateModel.getHy());
                if (event.getMonthHebrew().equals("0") || event.getMonthHebrew().equals("13"))
                    Log.e("", "");
                if (dbHandler == null)
                    dbHandler = new DatabaseHandler(mActivity);
                dbHandler.updateEventCompleted(event);
            } else {
            }

            if ((lastPos - 1) == position) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(HebrewDateModel hebrewDateModel) {
            super.onPostExecute(hebrewDateModel);
/*
            if (hebrewDateModel != null) {
                String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");

                event.setDay(hebrewDateModel.getGd());
                event.setMonth(hebrewDateModel.getGm());
                event.setYear(hebrewDateModel.getGy());
                event.setDayHebrewStr("" + arrStr[0].trim());
                event.setDayHebrew("" + hebrewDateModel.getHd());
                event.setMonthHebrew("" + Utility.getMonthInt(hebrewDateModel.getHm()));
                event.setMonthHebrewStr("" + hebrewDateModel.getHm());
                event.setYearHebrew("" + hebrewDateModel.getHy());

                if (dbHandler == null)
                    dbHandler = new DatabaseHandler(mActivity);
                dbHandler.updateEventCompleted(event);
            } else {
            }

            if ((lastPos - 1) == position) {
            }
*/
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mActivity.getAssets().open("Dates.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public interface OnComplete {
        void onComplete();
    }
}
