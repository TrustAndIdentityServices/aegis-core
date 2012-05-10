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
    public final static String ETAG_PROPERTY            = "PDP_ETAG";
    public final static String AUDIT_LOG_LEVEL_PARAM    = "AUDIT_LOG_LEVEL";
    public final static String PDP_LOG_OBLIGATION_PARAM = "PDP_LOG_OBLIGATION";
    public final static String PDP_LOG_OBLIGATION_FORMAT_PARAM = "PDP_LOG_OBLIGATION_FORMAT";
    public final static String
            METADATA_REPOSITORY_CLASS_PARAM="METADATA_REPOSITORY_CLASS";
    public final static String
            AUDIT_LOG_CLASS_PARAM="AUDIT_LOG_CLASS";
    public static String DEFAULT_AUDIT_LOG_CLASS =
             "org.ebayopensource.aegis.impl.FileAuditLogger";
     

    static private String      lock       = "lock";
    static private Properties  s_props    = null;
    static private MetaData    s_metadata = null;
    static private AuditLogger s_logger   = null;
    static  private int        s_etag     = -1;
    private int                m_id       = 0;
    private HashMap<String,Object> m_sharedState = null;
    private List<Environment> m_environment      = null;
    private StringBuilder m_logstring            = null;

    // Flag to pass logging obligation to caller
    private static boolean s_logObligation = false;
    // Obligation format : CSV || XML
    private static String s_logobformat = "CSV";

    // Log level
    private static int s_loglevel = 10;

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
            s_logObligation = ("true".equals(s_props.getProperty(PDP_LOG_OBLIGATION_PARAM)));
            String loglevel = s_props.getProperty(AUDIT_LOG_LEVEL_PARAM);
            if (loglevel != null)
                s_loglevel = Integer.parseInt(loglevel);
            String logobformat  = s_props.getProperty(PDP_LOG_OBLIGATION_FORMAT_PARAM);
            if (logobformat != null)
                s_logobformat = logobformat;
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
    
    /**
      * Maps the attribute name defined in a policy to application attribute and
      * searches for the attribute in the <code>Environment</code>
      */
    public Object getEnvValue(String id)
    {
        Object cval = null;
        if (m_environment == null)
            return cval;
        String envid = s_metadata.getMappingEnvAttribute(id);
        Debug.message("Context", "getEnvValue: mapping : "+id+"="+envid);
        for (Environment env : m_environment) {
            cval = env.getAttribute(envid);
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
    public void logPolicyEval(String logtype, String logsubtype, Target target, Policy policy, Decision decision, Decision decisionifnotsilent, String extra, int level)
    {
        if (level > s_loglevel)
            return;
        String policystr = (policy == null) ? "" : "PolicyName="+policy.getName()+"&PolicyVersion="+policy.getVersion()+"&PolicyID="+policy.getId()+"&silent="+policy.isSilent();
        String decisionstr = "&Decision="+decision;
        String extraStr = (extra == null) ? "" : extra;
        s_logger.log(
                     getId(),
                     logtype,
                     logsubtype,
                     target.getType()+":"+target.getName(),
                     policystr+
                     decisionstr+extraStr);
        addLogRecord(
                     getId(),
                     logtype,
                     logsubtype,
                     decisionifnotsilent == null? "" : decisionifnotsilent.getTypeStr(),
                     target.getType()+":"+target.getName(),
                     policy == null? "" : policy.getId(), policy == null? "" : policy.getVersion(),
                     policy == null? "" : ""+policy.isSilent(),
                     policystr+
                     decisionstr+extraStr);
    }

    public boolean getLogObligationFlag()
    {
        return s_logObligation ;
    }

    public String getCompleteLogRecord()
    {
        return m_logstring.toString();
    }

    private void addLogRecord( int id, String type, String subtype, String decisionifnotsilent, String target, String policyid, String policyversion, String isSilent,  String data) 
    {
        if ("CSV".equals(s_logobformat )) {
            addLogRecordCSV( id, type, subtype, decisionifnotsilent, target, policyid, policyversion, isSilent, data) ;
        } else if ("XML".equals(s_logobformat )) {
            addLogRecordXML( id, type, subtype, decisionifnotsilent, target, policyid, policyversion, isSilent, data);
        }
    }

    // Log in XML format
    private void addLogRecordXML( int id, String type, String subtype, String decisionifnotsilent, String target,String policyid, String policyversion, String isSilent,  String data) 
    {
        if (m_logstring == null) {
            m_logstring = new StringBuilder();
        }
        m_logstring.append("<log>");
          m_logstring.append("<id>");
            m_logstring.append(id);
          m_logstring.append("</id>");
          m_logstring.append("<type>");
            m_logstring.append(type);
          m_logstring.append("</type>");
          m_logstring.append("<subtype>");
            m_logstring.append(subtype);
          m_logstring.append("</subtype>");
          m_logstring.append("<target>");
            m_logstring.append(target);
          m_logstring.append("</target>");
          m_logstring.append("<policyid>");
            m_logstring.append(policyid);
          m_logstring.append("</policyid>");
          m_logstring.append("<policyversion>");
            m_logstring.append(policyversion);
          m_logstring.append("</policyversion>");
          m_logstring.append("<data><![CDATA[");
            m_logstring.append(data);
          m_logstring.append("]]></data>");
        m_logstring.append("</log>");
    } 
    
    // Log in CSV format
    private void addLogRecordCSV(int id, String type, String subtype, String decisionifnotsilent, String target, String policyid, String policyversion, String isSilent, String data) 
    {
        if (m_logstring == null) {
            m_logstring = new StringBuilder();
        } else {
            m_logstring.append(",||,");
        }
        encode(""+id).append(",");
        encode(type).append(",");
        encode(subtype).append(",");
        encode(decisionifnotsilent).append(",");
        encode(target).append(",");
        encode(policyid).append(",");
        encode(policyversion).append(",");
        encode(isSilent).append(",");
        encode(data);
    }
    // Escape comma characters only for now
    private StringBuilder encode(String s)
    {   
        if (m_logstring == null)
            m_logstring = new StringBuilder();
        if (s != null) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == ',') {
                    m_logstring.append("%2C");
                } else { 
                    m_logstring.append(c);
                }
            }
        }
        return m_logstring;
    }
}
