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

public class Debug
{
    final static String DEFAULT_DEBUG_FILE = "debug.txt";
    private static PrintStream ps = null;
    static {
        try {
            ps = new PrintStream(new FileOutputStream(DEFAULT_DEBUG_FILE, true));
        } catch (Exception ex) {
            System.out.println("ERROR: could not open debug file : "+DEFAULT_DEBUG_FILE);
        }
    }
    /**
     * Log a Error message
     * @param type
     * @param message
     */
    public static void error(String type, String msg)
    {
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
       ps.println("ERROR:"+type+":"+msg+ex);
       ex.printStackTrace(ps);
    }

    /**
     * Log a Warning message
     * @param type
     * @param message
     */
    public static void warning(String type, String msg)
    {
       ps.println("WARNING:"+type+":"+msg);
    }
    /**
     * Log a Info message
     * @param type
     * @param message
     */
    public static void message(String type, String msg)
    {
       ps.println("INFO:"+type+":"+msg);
    }

}
