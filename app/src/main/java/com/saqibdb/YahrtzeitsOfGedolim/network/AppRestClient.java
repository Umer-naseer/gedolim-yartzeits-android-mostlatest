package com.saqibdb.YahrtzeitsOfGedolim.network;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;


public class AppRestClient {

    //private static final String BASE_URL = "https://yahrtzeits-of-gedolim.herokuapp.com/";
    public static final String BASE_URL = "http://24.199.91.56/";
    public static final String API_GET_ALL_EVENT_LIST_END_POINT = "get_information_on_all_date/";
    public static final String API_GET_EVENT_LIST_BY_DATE_END_POINT = "get_information_on_date/";

    private static final int DEFAULT_TIMEOUT = 120 * 1000;

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static AsyncHttpClient dateclient = new AsyncHttpClient();
    private static AsyncHttpClient dateclientTodayDate = new AsyncHttpClient();
    private static AsyncHttpClient dateclientHtoE = new AsyncHttpClient();
    private static AsyncHttpClient dateclientEtoH = new AsyncHttpClient();
    private static AsyncHttpClient allEventApiDateClient = new AsyncHttpClient();

    public static void setTimeOutTime() {
        client.setTimeout(DEFAULT_TIMEOUT);
        dateclient.setTimeout(DEFAULT_TIMEOUT);
        dateclientTodayDate.setTimeout(DEFAULT_TIMEOUT);
        dateclientHtoE.setTimeout(DEFAULT_TIMEOUT);
        dateclientEtoH.setTimeout(DEFAULT_TIMEOUT);
        allEventApiDateClient.setTimeout(DEFAULT_TIMEOUT);
    }

    public static void cancelAllRequests() {
        client.cancelAllRequests(true);
    }

    public static void cancelAllDateRequests() {
        dateclient.cancelAllRequests(true);
    }

    public static AsyncHttpClient addHeader(AsyncHttpClient client, Context context) {
        client.addHeader("Content-Type", ResponseParser.API_REQUEST_METHOD_TYPE);
        return client;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void post(Context con, String url, JSONObject jdata, String contentType, AsyncHttpResponseHandler responseHandler) {
        setTimeOutTime();
        StringEntity entity;
        try {
            entity = new StringEntity(jdata.toString(), HTTP.UTF_8);
            addHeader(client, con);
            client.post(con, getAbsoluteUrl(url), entity, contentType, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getForDateConversion(Context con, String url, AsyncHttpResponseHandler responseHandler) {

        setTimeOutTime();
        try {
            addHeader(dateclient, con);
            dateclient.get(con, url, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getForDateConversionTodayDate(Context con, String url, AsyncHttpResponseHandler responseHandler) {

        setTimeOutTime();
        try {
            addHeader(dateclientTodayDate, con);
            dateclientTodayDate.get(con, url, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDateConversionHtoE(Context con, String url, AsyncHttpResponseHandler responseHandler) {

        setTimeOutTime();
        try {
            addHeader(dateclientHtoE, con);
            dateclientHtoE.get(con, url, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDateConversionEtoH(Context con, String url, AsyncHttpResponseHandler responseHandler) {

        setTimeOutTime();
        try {
            addHeader(dateclientEtoH, con);
            dateclientEtoH.get(con, url, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doAllEventApiDateConversion(Context con, String url, AsyncHttpResponseHandler responseHandler) {

        setTimeOutTime();
        try {
            addHeader(allEventApiDateClient, con);
            allEventApiDateClient.get(con, url, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
