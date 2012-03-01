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

public class Assertion
{
    static final public int OP_EQ = 0;
    static final public int OP_NE = 1;
    static final public int OP_LT = 2;
    static final public int OP_GT = 3;
    static final public int OP_MAX = 3;
   

    static private String[] s_strs = { "=", "!=", "<", ">" };

    private String m_category = null;
    private String m_name = null;
    private CExpr  m_cexpr = null;

    /**
     * Constructs a Assertion 
     * @param category condition category (eg User, Role, Device, Application 
     * @param name name of the condition
     */
    public Assertion(String category, String name)
    {
        m_category = category;
        m_name = name;
    }

    /**
     * Sets the assertion
     * @param id attribute name
     * @param op operator
     * @param value value
     */
    public void setCExpr(String id, int op,  Object value)
    {
        if (op > OP_MAX || op < 0 || id == null || value == null) {
            throw new InvalidParameterException();
        }
        m_cexpr = new CExpr(id, op, value);
    }

    /**
     * Sets the assertion
     * @param id attribute name
     * @param op operator
     * @param value value
     */
    public void setCExpr(String id, String op,  Object value)
    {
        for (int i = 0; i < s_strs.length; i++) {
            if (s_strs[i].equals(op)) {
                setCExpr(id, i, value);
                return;
            }
        }
        throw new InvalidParameterException();
    }

    /**
     * Retrieve condition name
     * @return name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve condition category
     * @return category
     */
    public String getCategory()
    {
        return m_category;
    }

    /**
     * convert condition to string
     * @return string
     */
    public CExpr getCExpr()
    {
        return m_cexpr;
    }

    /**
     * convert condition to string
     * @return string
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        CExpr cexpr = getCExpr();
        sb.append(" [ ");
        sb.append("\"").append(cexpr.id_).append("\"");
        sb.append(", \"").append(s_strs[cexpr.op_]).append("\"");
        sb.append(", \"").append(cexpr.val_).append("\"");
        sb.append(" ]");
        return sb.toString();
    }
}
