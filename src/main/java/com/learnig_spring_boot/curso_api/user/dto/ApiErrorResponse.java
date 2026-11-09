package com.learnig_spring_boot.curso_api.user.dto;

import java.time.Instant;

public class ApiErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path
    ){
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp(){
        return this.timestamp;
    }

    public int getStatus(){
        return this.status;
    }

    public String getError(){
        return  this.error;
    }

    public String getMessage(){
        return this.message;
    }

    public String getPath(){
        return this.path;
    }
}
