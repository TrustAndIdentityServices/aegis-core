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
package org.ebayopensource.aegis;

public class Effect
{
    final public static int DENY = 0;
    final public static int PERMIT = 1;
    final public static int UNKNOWN = 2;
    static final public int MAX = 2;

    private static String s_str[] = {"DENY", "PERMIT", "UNKNOWN" };
    private int m_effect;

    public Effect(int e)
    {
        if (e > MAX )
            throw new InvalidParameterException();
        m_effect = e;
    }
    public Effect(String s)
    {
        int e =  convertFromString(s);
        if (e > MAX || e < 0 )
            throw new InvalidParameterException();
        m_effect = e;
    }
    public int get()
    {
        return m_effect;
    }
    public void set( int e)
    {
        if (e > MAX )
            throw new InvalidParameterException();
        m_effect = e;
    }
    public String toString()
    {
         StringBuilder bld = new StringBuilder();
         bld.append("\"effect\" : ").append("\"").append(s_str[get()]).append("\"");
         return bld.toString();
    }
    private int convertFromString(String str)
    {
         for (int i = 0; i < s_str.length; i++) {
             if (s_str[i].equalsIgnoreCase(str)) 
                 return i;
         }
         return -1;
    }
}
