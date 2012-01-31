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

public class Condition
{
   static final public int OP_EQ = 0;
   static final public int OP_NE = 1;
   static final public int OP_LT = 2;
   static final public int OP_GT = 3;
   static final public int OP_MAX = 3;
   
   static final public String ops[] = { "=", "!=", "<", ">"};
    /**
     * Constructs a Condition 
     * @param type condition type (eg User, Role, Device, Application 
     * @param name name of the condition
     */
    public Condition(String type, String name)
    {
        _type = type;
        _name = name;
    }

    /**
     * Sets a condition attribute
     * @param id attribute name
     * @param op operator
     * @param value value
     */
    public void addExpr(String id, int op,  Object value)
    {
        if (op > OP_MAX || op < 0 || id == null || value == null) {
            throw new InvalidParameterException();
        }
        if (_attrs == null) {
            _attrs = new ArrayList<CExpr>();
        }
        _attrs.add(new CExpr(id, op, value));
    }

    /**
     * Retrieve condition name
     * @return name
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Retrieve condition type
     * @return type
     */
    public String getType()
    {
        return _type;
    }

    /**
     * convert condition to string
     * @return string
     */
    public ArrayList<CExpr> getAllAttrs()
    {
        return _attrs;
    }

    /**
     * convert condition to string
     * @return string
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("type : ").append(getType());
        sb.append(", name : ").append(getName());
        sb.append(", ");
        int i = 0;
        for (CExpr e : _attrs) {
            if (i++ > 0)
               sb.append(" AND ");
            sb.append("( ").append(e.id_).append(" ").append(strs[e.op_]).append(" ").append(e.val_).append(" )");
        }
        return sb.toString();
    }
    private String _type = null;
    private String _name = null;
    private ArrayList<CExpr> _attrs = null;
    static private String[] strs = { "=", "!=", "<", ">" };
    static public void main(String args[])
    {
         Condition c = new Condition("type1", "id1");
         c.addExpr("attr1", 0, new Integer(10)); 
         c.addExpr("attr2", 1, new Integer(11)); 
         System.out.println("c="+c.toString());
    }
}
