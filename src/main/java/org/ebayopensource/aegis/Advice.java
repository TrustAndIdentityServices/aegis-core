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

public class Advice
{
    /**
     * Constructs a Advice 
     * @param type advice type 
     */
    public Advice(String type)
    {
        _type = type;
    }

    /**
     * Retrieve advice type
     * @return type
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Sets a advice attribute
     * @param id attribute name
     * @param value value
     */
    public void addExpr(String id, int op,  Object value)
    {
        if (op > Condition.OP_MAX || id == null || value == null) {
            throw new InvalidParameterException();
        }
        if (_attrs == null)
            _attrs = new ArrayList<CExpr>();
        _attrs.add(new CExpr(id, op, value));
    }

    /**
     * Retrieve advice attribute
     * @return all items
     */
    public ArrayList<CExpr>  getAllExpr()
    {
        return _attrs;
    }

    /**
     * convert advice to string
     * @return string
     */
    public String toString()
    {
        if (_attrs == null)
            return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        
        for (CExpr e : _attrs) {
            if (i++ > 0)
               sb.append(" AND ");
            sb.append("( ").append(e.id_).append(" ").append(strs[e.op_]).append(" ").append(e.val_).append(" )");
        }
        return sb.toString();
    }
    private String _type = null;
    private ArrayList<CExpr> _attrs = null;
    static private String[] strs = { "=", "!=", "<", ">" };
}
