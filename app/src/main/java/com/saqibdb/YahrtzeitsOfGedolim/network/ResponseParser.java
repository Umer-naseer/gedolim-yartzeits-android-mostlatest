package com.saqibdb.YahrtzeitsOfGedolim.network;

public class ResponseParser {

    //attributes for parsing

    public static final String API_RESPONSE_MSG_CODE = "message_code";
    public static final String API_RESPONSE_SECURITY_CODE = "Security_Code";
    public static final String API_RESPONSE_MSG = "message";
    public static final String API_RESPONSE_USER_DETAILS = "user_details";


    public static final String API_REQUEST_METHOD_TYPE = "application/x-www-form-urlencoded";
    public static final String ATTRIBUTE_KEY_TOKEN = "token";
    public static final String ATTRIBUTE_GET_ACTIVITY_LIST = "get_activity_list";

    /*public static WeekRange parseUserWeekData(JSONObject jsonObject) {

        Response response = new Response();
        // String dataUser = jsonObject.getJSONObject(ATTRIBUTE_KEY_DATA).toString();
        String dataUser = jsonObject.toString();
        WeekRange weekRange = new WeekRange();
        try {
            Gson gson = new Gson();

            weekRange = gson.fromJson(dataUser, new TypeToken<WeekRange>() {
            }.getType());
        } catch (Exception e) {
            e.toString();
        }

        return weekRange;
    }

    public static WeekData parseWeekData(JSONObject jsonObject) {

        Response response = new Response();
        // String dataUser = jsonObject.getJSONObject(ATTRIBUTE_KEY_DATA).toString();
        String dataUser = jsonObject.toString();
        WeekData weekData = new WeekData();
        try {
            Gson gson = new Gson();
            weekData = gson.fromJson(dataUser, new TypeToken<WeekData>() {
            }.getType());
        } catch (Exception e) {
            e.toString();
        }

        return weekData;
    }*/

    /*public static ScoreCard parseUserScoreCard(JSONObject jsonObject) {

        Response response = new Response();
        // String dataUser = jsonObject.getJSONObject(ATTRIBUTE_KEY_DATA).toString();
        String dataUser = jsonObject.toString();
        Gson gson = new Gson();
        ScoreCard userModel = gson.fromJson(dataUser, new TypeToken<ScoreCard>() {
        }.getType());
        return userModel;
    }

    public static LoginResponseModel parseLoginData(JSONObject jsonObject) {
        LoginResponseModel userDataModels = new LoginResponseModel();
        try {
            Gson gson = new Gson();
            TypeToken<LoginResponseModel> token = new TypeToken<LoginResponseModel>() { };
            userDataModels = gson.fromJson(jsonObject.toString(), token.getType());
        } catch (Exception e) {
            e.toString();
        }
        return userDataModels;
    }*/
}
