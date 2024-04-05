package com.cloud.tictactoe.exception;

public class GameNotFoundException extends Exception{

    private String message;

    public GameNotFoundException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
