package com.saqibdb.YahrtzeitsOfGedolim.network.retrofit.request;

import android.util.Log;

import org.json.JSONObject;

public class RequestBodyFactory {
    private static final String TAG = RequestBodyFactory.class.getSimpleName();

    public JSONObject createRequestBody() {
        JSONObject body = new JSONObject();

        try {
            body.put("modified_date", "2017-12-14T20:10:06.000000Z");
            body.put("page", -1);
        } catch (Exception exception) {
            String message = exception.getMessage();
            Log.d(TAG, "[ERROR]: " + message);
        }

        return body;
    }
}
