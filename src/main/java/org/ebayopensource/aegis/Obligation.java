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

public class Obligation
{
    private String m_type = null;
    private HashMap<String,Object> m_attrs = null;

    /**
     * Constructs a Obligation 
     * @param type obligation type 
     */
    public Obligation(String type)
    {
        m_type = type;
    }

    /**
     * Retrieve obligation type
     * @return type
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Sets a obligation attribute
     * @param id attribute name
     * @param value value
     */
    public void setAttribute(String id, Object value)
    {
        if (m_attrs == null)
            m_attrs = new HashMap<String,Object>();
        m_attrs.put(id, value);
    }

    /**
     * Retrieve obligation attribute
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
