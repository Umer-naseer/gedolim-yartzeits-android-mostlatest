package com.saqibdb.YahrtzeitsOfGedolim.network;

/**
 * Created by ESCA on 5/8/2016.
 */
public class Response {

    private int success = 0;
    private String message = "";
    private int status_code = 0;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

}
