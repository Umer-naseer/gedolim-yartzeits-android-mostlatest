package com.saqibdb.YahrtzeitsOfGedolim.network.retrofit;

import com.saqibdb.YahrtzeitsOfGedolim.model.GetEvent;
import com.saqibdb.YahrtzeitsOfGedolim.network.AppRestClient;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventsService {
    @POST(AppRestClient.API_GET_ALL_EVENT_LIST_END_POINT)
    Call<GetEvent> getEvents(@Body RequestBody body);
}
