package com.saqibdb.YahrtzeitsOfGedolim.network.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.GetEvent;
import java.util.Collections;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;

public class ApiManager {
    private static final String TAG = ApiManager.class.getSimpleName();
    private EventsService eventsService;

    public ApiManager() {
        // Create OkHttpClient with increased timeout
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // Add logging interceptor for debugging (optional)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        // Pass the custom OkHttpClient to RetrofitClient

        eventsService = RetrofitClient.getRetrofit(httpClientBuilder.build()).create(EventsService.class);
    }

    public String makeRequest(JSONObject body) throws Exception {
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
            } else {
                throw new Exception("Server error");
            }
        } catch (Exception exception) {
            // handle exception
            String message = exception.getMessage();
            Log.e(TAG, "[ERROR]: " + message);
            return "";
        }
    }
}
