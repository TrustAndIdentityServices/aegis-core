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

import org.ebayopensource.aegis.plugin.AuditLogger;
import org.ebayopensource.aegis.debug.Debug;

import java.io.*;
import java.util.*;

import java.io.*;

public class FileAuditLogger implements AuditLogger
{
    private String      m_filename = null;
    private PrintStream m_ps = System.out;
    private boolean     m_psIsSystem = true;
    
    public void initialize(Properties props)
    {
        m_filename = props.getProperty(AuditLogger.AUDIT_LOG_FILE_PARAM);
        setFile(m_filename);
    }
    public void setFile(String f)
    {
        if (!m_psIsSystem && m_ps != null) {
            try {
                m_ps.close();
           } catch (Exception ex) {}
        }
        if (f == null) {
            Debug.message("FlatAuditLogger", "Default File Audit to System.out");
            m_ps = System.out;
            m_psIsSystem = true;
        } else {
           try {
               Debug.message("FlatAuditLogger", "Opening configured Audit file : "+f);
               m_ps = new PrintStream(new FileOutputStream(f, true));
               m_psIsSystem = false;
           } catch (Exception ex) {
               Debug.error("FlatAuditLogger", "ERROR: could not open Audit file : "+f);
           }
        }
    }
    /**
     * Create a Audit log
     * @param type
     * @param msgid
     * @param message
     */
    public void log(String type, String msgid, String message)
    {
        m_ps.println(new Date()+"||"+type+"||"+msgid+"||"+message);
    }
}
