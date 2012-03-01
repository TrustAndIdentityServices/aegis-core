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

import java.util.HashMap;
public class Target
{
    private String m_type = null;
    private String m_name = null;
    private HashMap<String,Object> m_attrs = null;
    /**
     * Constructs a Target 
     * @param type target type 
     * @param name name of the target
     */
    public Target(String type, String name)
    {
        m_type = type;
        m_name = name;
    }

    /**
     * Sets a target attribute
     * @param id attribute name
     * @param value value
     */
    public void setAttribute(String id, Object value)
    {
        if (m_attrs == null) {
            m_attrs = new HashMap<String,Object>();
        }
        m_attrs.put(id, value);
    }

    /**
     * Retrieve target name
     * @return name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve target type
     * @return type
     */
    public String getType()
    {
        return m_type;
    }


    /**
     * Retrieve target attribute
     * @param id attr name
     * @return attr value
     */
    public Object getAttribute(String id)
    {
        Object val = null;
        if (m_attrs != null)
            val = m_attrs.get(id);
        return val;
    }
    public String toString()
    {
         StringBuilder sbld = new StringBuilder();
         sbld.append(" [ ");
         sbld.append(" \"").append(getType()).append("\"");
         sbld.append(", \"").append(getName()).append("\" ");
         sbld.append(" ]");
         return sbld.toString();
    }
}
