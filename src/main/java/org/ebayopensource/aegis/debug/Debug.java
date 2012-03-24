/*******************************************************************************
 * 
 *  Copyright (c) 2006-2012 eBay Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
*******************************************************************************/
package org.ebayopensource.aegis.debug;

import java.io.*;
import java.util.*;

public class Debug
{
    public final static String DEBUG_LEVEL_PARAM = "DEBUG_LEVEL";
    public final static String DEBUG_FILE_PARAM  = "DEBUG_FILE";

    final static String LEVEL_MESSAGE_STR = "message";
    final static String LEVEL_WARNING_STR = "warning";
    final static String LEVEL_ERROR_STR   = "error";

    final static int LEVEL_MESSAGE = 3;
    final static int LEVEL_WARNING = 2;
    final static int LEVEL_ERROR =   1;

    final static String DEFAULT_DEBUG_FILE = "debug.txt";

    private static PrintStream ps = System.out;
    private static boolean s_psIsSystem = true;
    private static int s_level = LEVEL_MESSAGE;
    
    public static void initialize(Properties props)
    {
        setLevel(props.getProperty(DEBUG_LEVEL_PARAM));
        setFile(props.getProperty(DEBUG_FILE_PARAM));
    }
    public static void setFile(String f)
    {
        // Close previous file if present
        if (!s_psIsSystem && ps != null) {
           try {
               ps.close();
           } catch (Exception ex) {}
        }
        if (f == null || f.length() == 0) {
            ps = System.out;
            s_psIsSystem = true;
        } else {
           try {
               ps = new PrintStream(new FileOutputStream(f, true));
               s_psIsSystem = false;
           } catch (Exception ex) {
               System.out.println("ERROR: could not open debug file : "+f);
           }
        }
    }
    
    /**
     * Set Debug level
     */
    public static void setLevel(String level)
    {
        if (LEVEL_MESSAGE_STR.equals(level))
            s_level = LEVEL_MESSAGE;
        else if (LEVEL_WARNING_STR.equals(level))
            s_level = LEVEL_WARNING;
        else
            s_level = LEVEL_ERROR;
            
    }

    /**
     * Get Debug level
     */
    public static int getLevel(String level)
    {
         return s_level;
    }

    /**
     * Log a Error message
     * @param type
     * @param message
     */
    public static void error(String type, String msg)
    {
        if (s_level >= LEVEL_ERROR)
            ps.println("ERROR:"+type+":"+msg);
    }

    /**
     * Log a Error message
     * @param type
     * @param message
     * @param ex
     */
    public static void error(String type, String msg, Exception ex)
    {
        if (s_level >= LEVEL_ERROR) {
            ps.println("ERROR:"+type+":"+msg+ex);
            ex.printStackTrace(ps);
        }
    }

    /**
     * Log a Warning message
     * @param type
     * @param message
     */
    public static void warning(String type, String msg)
    {
        if (s_level >= LEVEL_WARNING) {
           ps.println("WARNING:"+type+":"+msg);
        }
    }
    /**
     * Log a Info message
     * @param type
     * @param message
     */
    public static void message(String type, String msg)
    {
        if (s_level >= LEVEL_MESSAGE) {
            ps.println("INFO:"+type+":"+msg);
        }
    }
}
