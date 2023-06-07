package com.saqibdb.YahrtzeitsOfGedolim;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DateUtil;
import com.saqibdb.YahrtzeitsOfGedolim.helper.FileReader;
import com.saqibdb.YahrtzeitsOfGedolim.helper.FileWriter;
import com.saqibdb.YahrtzeitsOfGedolim.helper.SharedPreferencesHelper;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.GetEvent;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;
import com.saqibdb.YahrtzeitsOfGedolim.network.AppRestClient;
import com.saqibdb.YahrtzeitsOfGedolim.network.ServerManager;
import com.saqibdb.YahrtzeitsOfGedolim.network.retrofit.ApiManager;
import com.saqibdb.YahrtzeitsOfGedolim.network.retrofit.request.RequestBodyFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        dbHandler = new DatabaseHandler(mActivity);
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
        protected Void doInBackground(Void... voids) {
            HebrewDateModel hebrewDateModel = DateUtil.convertHDateToGDate(todayHebrewDateModel.getHy(), event.getMonth(), event.getDay());
            if (hebrewDateModel == null)
                return null;
            String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
            event.setDay(hebrewDateModel.getGd());
            event.setMonth(hebrewDateModel.getGm());
            event.setYear(hebrewDateModel.getGy());
            event.setDayHebrewStr("" + arrStr[0].trim());
            event.setDayHebrew("" + hebrewDateModel.getGd());
            event.setMonthHebrew("" + hebrewDateModel.getHm_());
            event.setMonthHebrewStr("" + hebrewDateModel.getHm());
            event.setYearHebrew("" + hebrewDateModel.getHy());
            dbHandler.addEvent(event, 0);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (onComplete != null)
            onComplete.onComplete();

        for (int i = 0; i < list.size(); i++) {
            EventDetails event = list.get(i);
            new ConvertAndSave(event).execute();
        }

        if (list.size() > 0) {
            new NotificationGenerate(mActivity, NotificationGenerate.ALL).execute();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        boolean isSaved = SharedPreferencesHelper.getInstance().getBoolean("isDataSaved_", false);
        if (!isSaved) {

            FileWriter fileWriter = new FileWriter();
            FileReader fileReader = new FileReader();

            if (ServerManager.isInternetConnected(mActivity)) {
                ApiManager apiManager = new ApiManager();
                RequestBodyFactory factory = new RequestBodyFactory();

                String eventDetailsRaw = null;
                try {
                    eventDetailsRaw = apiManager.makeRequest(factory.createRequestBody());
                } catch (Exception e) {
                    fileWriter.writeContentToFile(mActivity, loadJSONFromAsset());
                    String message = e.getMessage();
                    Log.e(TAG, "[INFO]: " + message);
                }
                if (checkResponseAvailability(eventDetailsRaw)) {
                    fileWriter.writeContentToFile(mActivity, eventDetailsRaw);
                } else {
                    Log.i(TAG, "[INFO]: List taken from api is empty");
                }
            } else {
                fileWriter.writeContentToFile(mActivity, loadJSONFromAsset());
                Log.i(TAG, "[INFO]: Internet is not connected");
            }

            Gson gson = new Gson();
            GetEvent getEvent = gson.fromJson(fileReader.readContentFromFile(mActivity), GetEvent.class);
            if (getEvent != null && getEvent.getEventDetails() != null && getEvent.getEventDetails().size() > 0) {
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

    private boolean checkResponseAvailability(String eventDetailsRaw) {
        return eventDetailsRaw != null && !eventDetailsRaw.isEmpty();
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
