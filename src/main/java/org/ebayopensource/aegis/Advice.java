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
    private String m_type = null;
    private ArrayList<CExpr> m_attrs = null;
    static private String[] s_strs = { "=", "!=", "<", ">", "in", "notin", "<=", ">=" };

    /**
     * Constructs a Advice 
     * @param type advice type 
     */
    public Advice(String type)
    {
        m_type = type;
    }

    /**
     * Retrieve advice type
     * @return type
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Sets a advice attribute
     * @param id attribute name
     * @param value value
     */
    public void addExpr(String id, int op,  Object value)
    {
        if (op > Assertion.OP_MAX || id == null || value == null) {
            throw new InvalidParameterException();
        }
        if (m_attrs == null)
            m_attrs = new ArrayList<CExpr>();
        m_attrs.add(new CExpr(id, op, value));
    }

    /**
     * Retrieve advice attribute
     * @return all items
     */
    public ArrayList<CExpr>  getAllExpr()
    {
        return m_attrs;
    }

    /**
     * convert advice to string
     * @return string
     */
    public String toString()
    {
        if (m_attrs == null)
            return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        
        for (CExpr e : m_attrs) {
            if (i++ > 0)
               sb.append(" AND ");
            sb.append("( ").append(e.id_).append(" ").append(s_strs[e.op_]).append(" ").append(e.val_).append(" )");
        }
        return sb.toString();
    }
}
