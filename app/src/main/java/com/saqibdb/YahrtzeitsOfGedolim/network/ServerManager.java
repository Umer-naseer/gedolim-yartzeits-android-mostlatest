package com.saqibdb.YahrtzeitsOfGedolim.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerManager {

    // Internet is connected or not
    public static boolean isInternetConnected(Context appContext) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        //App.getContext()
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
        //return true;
    }

    public static void getServerAllEventList(int errorMessageId, Context context, int pageNo, String date, JsonHttpResponseHandler handler) {
        if (isInternetConnected(context)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("modified_date", date);
                jsonObject.put("page", pageNo);
                Log.d("NewTAG", "Get page " + pageNo + " Event List : " + jsonObject.toString());
                AppRestClient.post(context, AppRestClient.API_GET_ALL_EVENT_LIST_END_POINT, jsonObject, "application/json", handler);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show();
        }
    }

    public static void getEventListByDate(int errorMessageId, Context context, int hMonth, int hDay, JsonHttpResponseHandler handler) {
        if (isInternetConnected(context)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("month", hMonth + "");
                jsonObject.put("day", hDay + "");

                AppRestClient.post(context, AppRestClient.API_GET_EVENT_LIST_BY_DATE_END_POINT, jsonObject, "application/json", handler);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show();
        }
    }

    public static void doDateConversion(int errorMessageId, Context context, String url, JsonHttpResponseHandler handler) {
        if (isInternetConnected(context)) {
            try {
                AppRestClient.getForDateConversion(context, url , handler);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show();
        }
    }

    public static void doDateConversionTodayDate(int errorMessageId, Context context, String url, JsonHttpResponseHandler handler) {
        if (isInternetConnected(context)) {
            try {
                AppRestClient.getForDateConversionTodayDate(context, url , handler);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show();
        }
    }

    public static void doDateConversionHtoE(int errorMessageId, Context context, String url, JsonHttpResponseHandler handler) {
        if (isInternetConnected(context)) {
            try {
                AppRestClient.getDateConversionHtoE(context, url , handler);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show();
        }
    }

    public static void doDateConversionEtoH(int errorMessageId, Context context, String url, JsonHttpResponseHandler handler) {
        if (isInternetConnected(context)) {
            try {
                AppRestClient.getDateConversionEtoH(context, url , handler);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show();
        }
    }

    public static void doAllEventApiDateConversion(int errorMessageId, Context context, String url, JsonHttpResponseHandler handler) {
        if (isInternetConnected(context)) {
            try {
                AppRestClient.doAllEventApiDateConversion(context, url , handler);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show();
        }
    }
}
