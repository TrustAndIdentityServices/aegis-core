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
package org.ebayopensource.aegis.md;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.net.URL;

import org.ebayopensource.aegis.debug.Debug;
//import org.ebayopensource.aegis.impl.SimpleDenyOverridesConflictResolver;
import org.ebayopensource.aegis.plugin.ConflictResolver;
import org.ebayopensource.aegis.plugin.TargetEvaluator;
import org.ebayopensource.aegis.plugin.AssertionEvaluator;
import org.ebayopensource.aegis.plugin.RuleEvaluator;
import org.ebayopensource.aegis.plugin.MetaDataRepository;

public class BaseMetaData
{
    public static final String METADATA_REPOSITORY_CLASS_PARAM =
                               "METADATA_REPOSITORY_CLASS";
    public final static String FLATFILE_ATTRIBUTE_STORE = 
                                 "FLATFILE_ATTRIBUTE_STORE";
    public final static String CONFLICTRESOLVER_CLASS_PARAM = 
                                 "conflictresolver.evalclass";

    public final static String DEFAULT_TARGET_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericTargetEvaluator";
    public final static String DEFAULT_RULE_EVALCLASS= null;
    public final static String DEFAULT_ASSERTION_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericAssertionEvaluator";
    public final static String DEFAULT_CONFLICTRESOLVER_CLASS = 
        "org.ebayopensource.aegis.impl.SimpleDenyOverridesConflictResolver";
    private Properties m_props = null;

    private MetaDataRepository metadataRepository = null;

    public TargetEvaluator getTargetEvaluator(String type)
    {
        try {
            String cl = getProperty( "target."+type+".evalclass");
            if (cl == null)
                cl = DEFAULT_TARGET_EVALCLASS;
            Debug.message("MetaData", "MetaData.getTargetEvaluator:type="+type+" class="+cl);
            return (TargetEvaluator) Class.forName(cl).newInstance();
        } catch (Exception ex) {
            Debug.error("MetaData", "getTargetEvaluator:error loading class:", ex);
            return null;
        }
    }
    public RuleEvaluator getRuleEvaluator(String type)
    {
        try {
            String cl = getProperty( "rule."+type+".evalclass");
            if (cl == null)
                return null;
            Debug.message("MetaData", "getRuleEvaluator:type="+type+" class="+cl);
            return (RuleEvaluator) Class.forName(cl).newInstance();
        } catch (Exception ex) {
            Debug.error("MetaData", "getRuleEvaluator:error loading class:", ex);
            return null;
        }
    }
    public AssertionEvaluator getAssertionEvaluator(String type)
    {
        try {
            String cl = getProperty( "assertion."+type+".evalclass");
            if (cl == null)
                cl = DEFAULT_ASSERTION_EVALCLASS;
            Debug.message("MetaData", "getAssertionEvaluator:type="+type+" class="+cl);
            return (AssertionEvaluator) Class.forName(cl).newInstance();
        } catch (Exception ex) {
            Debug.error("MetaData", "getAssertionEvaluator:error loading class:", ex);
            return null;
        }
    }
    public ConflictResolver getConflictResolver()
    {
        try {
            String cl = getProperty(CONFLICTRESOLVER_CLASS_PARAM);
            if (cl == null)
                cl = DEFAULT_CONFLICTRESOLVER_CLASS;
            Debug.message("MetaData", "getConflictResolver:class="+cl);
            return (ConflictResolver) Class.forName(cl).newInstance();
        } catch (Exception ex) {
            Debug.error("MetaData", "getConflictResolver:error loading class:", ex);
            return null;
        }
    }
    public String getProperty(String id)
    {
        if (m_props != null) {
            String ret = m_props.getProperty(id);
            if (ret != null)
                return ret;
        }
        return metadataRepository.getProperty(id);
    }
    public void loadProperties(Properties PDPProperties) throws Exception
    {
        try {
            // Retrieve Repository class
            String cl =PDPProperties.getProperty(
                           METADATA_REPOSITORY_CLASS_PARAM);
            Debug.message("MetaData", "MetaDataRepository:class="+cl);
            metadataRepository = (MetaDataRepository) Class.forName(cl).newInstance();
            metadataRepository.initialize(PDPProperties);
            m_props = new Properties();
            String ffrepository = PDPProperties.getProperty(FLATFILE_ATTRIBUTE_STORE);
            if (ffrepository != null)
                m_props.setProperty(FLATFILE_ATTRIBUTE_STORE, 
                  PDPProperties.getProperty(FLATFILE_ATTRIBUTE_STORE));
        } catch (Exception ex) {
            Debug.error("MetaData", "loadPropertes:error:",ex);
            throw(ex);
        } 
    }
    public String getMembershipAttribute(String cat)
    {
        return m_props.getProperty("group."+cat+".membername");
    }
}
