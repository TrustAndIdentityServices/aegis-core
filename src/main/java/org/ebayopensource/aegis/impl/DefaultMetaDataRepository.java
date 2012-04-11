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

package org.ebayopensource.aegis.impl;

import java.io.*;
import java.net.*;
import java.util.*;
import org.ebayopensource.aegis.plugin.MetaDataRepository;
import org.ebayopensource.aegis.debug.Debug;

/**
  * This class provides a simple <code>Properties</code> based
  * implementation of MetaData. The Properties can either
  * be loaded from a flatfile or in a URL.
  */
public class DefaultMetaDataRepository implements MetaDataRepository
{
    public final static String METADATA_CONFIG_FILE_PARAM = 
                                 "METADATA_CONFIG_FILE";
    public final static String METADATA_CONFIG_URL_PARAM = 
                                 "METADATA_CONFIG_URL";
    public final static String METADATA_CONFIG_CLASSPATH_PARAM = 
                                 "METADATA_CONFIG_CLASSPATH";

    private Properties m_props = new Properties();

    public void initialize(Properties props) throws Exception
    {
        loadProperties(props);
    }
    public String getProperty(String key)
    {
        return m_props.getProperty(key);
    }
    public void setProperty(String key, String value)
    {
    }    
    private void loadProperties(Properties PDPProperties) throws Exception
    {
        InputStream fin = null;
        String location = null;
        try {
            // FILE takes precedence over URL
            location = PDPProperties.getProperty(METADATA_CONFIG_FILE_PARAM);
            if (location != null) {
                Debug.message("MetaData", "loadProperties : file="+location);
                m_props.load((fin = new FileInputStream(location)));
            } else {
                location = PDPProperties.getProperty(METADATA_CONFIG_URL_PARAM);
                if (location != null) {
                    Debug.message("MetaData", "loadProperties : url="+location);
                    URL url = new URL(location); 
                    m_props.load((fin = url.openStream()));
                } else {
                    location = PDPProperties.getProperty(METADATA_CONFIG_CLASSPATH_PARAM);
                    if (location != null) {
                        Debug.message("MetaData", "loadProperties : classp="+location);
                        m_props.load(fin = Thread.currentThread().getContextClassLoader().getResourceAsStream(location));
                    } else {
                        Debug.message("MetaData", "loadProperties: no MetaData specified.");
                    }
                }
           }
        } catch (Exception ex) {
            Debug.error("DefaultMetaData", "load:error loading file:"+location);
            throw(ex);
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (Exception ex) {}
        }
    }
}

