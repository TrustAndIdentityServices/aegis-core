/* 
 *  Copyright (c) 2006-2012 eBay Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
*******************************************************************************/

package org.ebayopensource.aegis;

import java.util.*;
import org.ebayopensource.aegis.md.MetaData;
import org.ebayopensource.aegis.plugin.AuditLogger;
import org.ebayopensource.aegis.debug.Debug;

public class Context
{
    public final static String ETAG_PROPERTY="PDP_ETAG";
    public final static String
            METADATA_REPOSITORY_CLASS_PARAM="METADATA_REPOSITORY_CLASS";
    public final static String
            AUDIT_LOG_CLASS_PARAM="AUDIT_LOG_CLASS";
    public static String DEFAULT_AUDIT_LOG_CLASS =
             "org.ebayopensource.aegis.impl.FileAuditLogger";
//org.ebayopensource.aegis.impl.DefaultMetaDataRepository
    static private String   lock    = "lock";
    static private Properties   s_props    = null;
    static private MetaData  s_metadata   = null;
    static private AuditLogger  s_logger   = null;
    static  private int         s_etag   = -1;
    private int                 m_id   = 0;
    private HashMap<String,Object> m_sharedState = null;
    private List<Environment> m_environment = null;

    public Context(Properties pdpprops, List<Environment> env) throws Exception
    {
        m_environment = env;
        boolean load = false;
        // Check etag on the properties
        String curretag = pdpprops.getProperty(ETAG_PROPERTY);
        int intetag = 0;
        if (curretag != null)
            intetag = Integer.parseInt(curretag);
        m_id = (new Random()).nextInt(Integer.MAX_VALUE);
        if (intetag == s_etag) {
            return;
        }
        synchronized(lock) 
        {
            if (intetag == s_etag) {
                return;
            }
            s_props = new Properties(pdpprops);
            // Initialize Logger
            try {
                String cl = s_props.getProperty(AUDIT_LOG_CLASS_PARAM);
                if (cl == null)
                    cl = DEFAULT_AUDIT_LOG_CLASS;
                Debug.message("Context", "initialize Logger : class="+cl);
                s_logger = (AuditLogger) Class.forName(cl).newInstance();
                s_logger.initialize(s_props);
            } catch (Exception ex) {
                Debug.error("Context", "Failed to load AuditLogger :", ex);
            }
            // Initialize Metadata
            s_metadata = new MetaData();
            s_metadata.loadProperties(s_props);
            s_etag = intetag;
        }
    }

    public int getId()
    {
        return m_id;
    }
    public AuditLogger getAuditLogger()
    {
        return s_logger;
    }

    public String getPDPProperty(String name)
    {
        if (s_props != null)
            return s_props.getProperty(name);
        return null;
    }
    public Object getSharedState(String key)
    {
         if (m_sharedState == null)
             return null;
         return m_sharedState.get(key);
    }
    public void setSharedState(String key, Object value)
    {
         if (m_sharedState == null)
             m_sharedState = new HashMap<String,Object>();
         m_sharedState.put(key, value);
    }
    
    public Object getEnvValue(String id)
    {
        Object cval = null;
        if (m_environment == null)
            return cval;
        for (Environment env : m_environment) {
            cval = env.getAttribute(id);
            if (cval != null)
                break;
        }
        return cval;
    }
    public MetaData getMetaData()
    {
        return s_metadata;
    }
    public int getEtag()
    {
        return s_etag;
    }
    public void logPolicyEval(String logtype, String logsubtype, Target target, Policy policy, Decision decision, String extra)
    {
        String policystr = (policy == null) ? "" : "PolicyName="+policy.getName()+"&PolicyID="+policy.getId()+"&silent="+policy.isSilent();
        String decisionstr = "&Decision="+decision;
        String extraStr = (extra == null) ? "" : extra;
        s_logger.log(
                     getId(),
                     logtype,
                     logsubtype,
                     target.getType()+":"+target.getName(),
                     policystr+
                     decisionstr+extraStr);
    }
}
