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
import java.util.ArrayList;

/**
  * Encapsulates boolean expressions between same types. Eg :
  *  <code> 
  *   ANY_OF { Subject1, Subject2, Subject3 }
  *   ALL_OF { Subject1, Subject2, Subject3 }
  * </code>
  */
public class Expression<T>
{
    final public static int ALL_OF =0;
    final public static int ANY_OF =1;

    final public static String ANY_OF_STR = "ANY_OF";
    final public static String ALL_OF_STR = "ALL_OF";

    private int m_type = ALL_OF;
    private ArrayList<Object> m_members;
    private static String m_strs[] = {ALL_OF_STR, ANY_OF_STR };

    public void setType(int type) 
    {
        m_type = type;
    }
    public int getType() 
    {
        return m_type;
    }
    public void add(T data)
    {
        if (m_members == null)
            m_members = new ArrayList<Object>();
        m_members.add(data);
    }
    public void add(Expression<T> data)
    {
        if (m_members == null)
            m_members = new ArrayList<Object>();
        m_members.add(data);
    }
    public ArrayList<Object> getMembers()
    {
        return  m_members;
    }

    public String toString(boolean shortform)
    {
        StringBuilder sb = new StringBuilder();
        if (!shortform) {
            sb.append("\"").append(m_strs[getType()]).append("\" : ");
        }
        sb.append("[ ");
        boolean first = true;
        if ( m_members != null) {
            for (Object ob : m_members) {
                if (first)
                    first = false;
                else
                    sb.append(", ");
                sb.append(ob.toString()).append(" "); 
            }
        } 
        sb.append("] ");
        return sb.toString();
    }
    public String toString()
    {
        return toString(false);
    }

}
