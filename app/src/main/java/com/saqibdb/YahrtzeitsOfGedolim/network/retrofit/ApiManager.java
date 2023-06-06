package com.saqibdb.YahrtzeitsOfGedolim.network.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.GetEvent;
import java.util.Collections;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ApiManager {
    private static final String TAG = ApiManager.class.getSimpleName();
    private EventsService eventsService;

    public ApiManager() {
        eventsService = RetrofitClient.getRetrofit().create(EventsService.class);
    }

    public String makeRequest(JSONObject body) {
        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, body.toString());
            Call<GetEvent> call = eventsService.getEvents(requestBody);
            Response<GetEvent> response = call.execute();
            if (response.isSuccessful()) {
                GetEvent responseBody = response.body();
                Gson gson = new Gson();
                String rawResponse = gson.toJson(responseBody);
                return rawResponse;
            }
            return "";
        } catch (Exception exception) {
            // handle exception
            String message = exception.getMessage();
            Log.e(TAG, "[ERROR]: " + message);
            return "";
        }
    }
}
