/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.structures;

/**
 *
 * @author Steve
 */
public class Result
{
    private boolean success;
    private String message;
    
    public Result()
    {
        success = true;
        message = "Success";
    }
    
    public Result(boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String toString() {
        return success + ":" + message;
    }
    
}
