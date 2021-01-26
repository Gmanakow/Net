package com.manakov.util;

public class IncorrectNumberOfArgumentsException extends Exception {

    public IncorrectNumberOfArgumentsException(){
        super();
    }

    public IncorrectNumberOfArgumentsException(String message){
        super(message);
    }
}
