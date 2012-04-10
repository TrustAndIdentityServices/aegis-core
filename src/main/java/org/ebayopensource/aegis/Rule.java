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


/**
  * Encapsulates a set of Assertions - wrapper to facilitate reuse in misc policies.
  */
public class Rule
{
    private String m_category = null;
    private String m_name = null;
    private Expression<Assertion> m_exp = null;

    /**
     * Constructs a Rule 
     * @param category condition category (eg User, Role, Device, Application 
     * @param name name of the condition
     * @param exp Expression of Assertions
     */
    public Rule(String category, String name, Expression<Assertion> exp)
    {
        m_category = category;
        m_name = name;
        m_exp = exp;
    }

    /**
     * Retrieve rule name
     * @return name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve rule category
     * @return category
     */
    public String getCategory()
    {
        return m_category;
    }
    /**
     * Retrieve rule expression
     * @return expression
     */
    public Expression<Assertion> getExpression()
    {
        return m_exp;
    }


    /**
     * convert condition to string
     * @return string
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        sb.append(" \"category\" : \"").append(getCategory()).append("\" , ");
        Expression<Assertion> exp = getExpression();
        if (exp != null)
            sb.append(exp.toString());
        sb.append("}");
        return sb.toString();
    }
}
