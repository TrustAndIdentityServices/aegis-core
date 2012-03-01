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
public class Environment
{
    private String m_type = null;
    private String m_name = null;
    private HashMap<String,Object> m_attrs = null;

    /**
     * Constructs a Environment 
     * @param type environment type 
     * @param name name of the environment
     */
    public Environment(String type, String name)
    {
        m_type = type;
        m_name = name;
    }

    /**
     * Sets a environment attribute
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
     * Retrieve environment name
     * @return name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve environment type
     * @return type
     */
    public String getType()
    {
        return m_type;
    }


    /**
     * Retrieve environment attribute
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
}
