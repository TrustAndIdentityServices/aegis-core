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
  *   NOT { Subject1, Subject2, Subject3 }
  * </code>
  */
public class Expression<T>
{
    final public static int ANY_OF =0;
    final public static int ALL_OF =1;
    final public static int NOT =2; // TODO : rename to "NONE_OF"? or restrict to single member?

    final public static String ANY_OF_STR = "ANY_OF";
    final public static String ALL_OF_STR = "ALL_OF";
    final public static String NOT_STR = "NOT";

    public void setType(int type) 
    {
        _type = type;
    }
    public int getType() 
    {
        return _type;
    }
    public void add(T data)
    {
        if (_members == null)
            _members = new ArrayList<Object>();
        _members.add(data);
        // TODO : add check if we want a single mmeber for NOT
    }
    public void add(Expression<T> data)
    {
        if (_members == null)
            _members = new ArrayList<Object>();
        _members.add(data);
        // TODO : add check if we want a single mmeber for NOT
    }
    public ArrayList<Object> getMembers()
    {
        return  _members;
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(_strs[getType()]).append(" { ");
        if ( _members != null) {
            for (Object ob : _members) {
                sb.append(ob.toString()).append(" "); 
            }
        } 
        sb.append("} ");
        return sb.toString();
    }
    private int _type = ANY_OF;
    private ArrayList<Object> _members;
    private static String _strs[] = {ANY_OF_STR, ALL_OF_STR, NOT_STR };

    public static void main(String[] args)
    {
        Expression<String> e1 = new Expression<String>();
        e1.setType(0);
        e1.add("Role1");
        e1.add("Role2");
        e1.add("Role3");
        System.out.println(" e1= "+e1.toString());

        Expression<String> e2 = new Expression<String>();
        e2.setType(1);
        e2.add("G1");
        e2.add("G2");
        e2.add("G3");

        System.out.println(" e2= "+e2.toString());
        Expression<String> e = new Expression<String>();
        e.setType(2);
        e.add(e1);
        e.add(e2);

        System.out.println(" e= "+e.toString());

    }
}
