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
public class Subject
{
    /**
     * Constructs a Subject 
     * @param type subject type (eg User, Role, Device, Application 
     * @param name name of the subject
     */
    public Subject(String type, String name)
    {
        _type = type;
        _name = name;
    }

    /**
     * Sets a subject attribute
     * @param id attribute name
     * @param value value
     */
    public void setAttribute(String id, Object value)
    {
        if (_attrs == null) {
            _attrs = new HashMap<String,Object>();
        }
        _attrs.put(id, value);
    }

    /**
     * Retrieve subject name
     * @return name
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Retrieve subject type
     * @return type
     */
    public String getType()
    {
        return _type;
    }


    /**
     * Retrieve subject attribute
     * @param id attr name
     * @return attr value
     */
    public Object getAttribute(String id)
    {
        Object val = null;
        if (_attrs != null)
            val = _attrs.get(id);
        return val;
    }
    public String toString()
    {
         StringBuilder sbld = new StringBuilder();
         sbld.append("Subject : {  type:\"").append(getType()).append("\", ");
         sbld.append("name : \"").append(getName()).append("\"");
         sbld.append("}");
         return sbld.toString();
    }
    private String _type = null;
    private String _name = null;
    private HashMap<String,Object> _attrs = null;
}
