package com.cetcbigdata.varanus.exception;

public class AuditLogException extends Exception{
    public AuditLogException(String msg){
        super(msg);
    }
    public AuditLogException(Exception e){
        super(e);
    }
    public AuditLogException(String msg, Exception e){
        super(msg, e);
    }
}
