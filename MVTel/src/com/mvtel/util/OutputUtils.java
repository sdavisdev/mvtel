/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.util;

import java.util.logging.Logger;

import com.mvtel.structures.Result;
import org.json.simple.JSONObject;

/**
 *
 * @author Steve
 */
public class OutputUtils 
{
    public static JSONObject toJSON(Result result)
    {
        JSONObject object = new JSONObject();
        object.put("success", result.isSuccess());
        object.put("message", result.getMessage());
        
        return object;
    }
    
    public static void logError(Logger logger, Throwable t, String message)
    {
    	StringBuilder sb = new StringBuilder(message);
    	sb.append('\n');
    	sb.append(t.getMessage());
    	sb.append('\n');
    	
        for(StackTraceElement ste : t.getStackTrace())
        {
        	sb.append("   at ");
        	sb.append(ste);
        	sb.append('\n');
        }

        logger.severe(sb.toString());
    }
}
