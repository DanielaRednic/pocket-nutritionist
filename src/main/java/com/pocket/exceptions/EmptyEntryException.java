package com.pocket.exceptions;

public class EmptyEntryException extends Exception{
    public EmptyEntryException()
    {
        super(String.format("All entries must be completed!"));
    }

}
