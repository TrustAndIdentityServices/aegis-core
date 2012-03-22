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
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.debug.Debug;
//import org.ebayopensource.aegis.impl.SimpleDenyOverridesConflictResolver;
import org.ebayopensource.aegis.plugin.ConflictResolver;
import org.ebayopensource.aegis.plugin.TargetEvaluator;
import org.ebayopensource.aegis.plugin.AssertionEvaluator;
import org.ebayopensource.aegis.plugin.RuleEvaluator;

public class MetaData
{
    public final static String METADATA_CONFIG_FILE_PARAM = "METADATA_CONFIG_FILE";
    public final static String DEFAULT_METADATA_PROPERTIES_FILE = "MetaData.properties";
    public final static String FLATFILE_ATTRIBUTE_STORE = "FLATFILE_ATTRIBUTE_STORE";
    public final static String CONFLICTRESOLVER_CLASS_PARAM = "conflictresolver.evalclass";

    public final static String DEFAULT_TARGET_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericTargetEvaluator";
    public final static String DEFAULT_RULE_EVALCLASS= null;
    public final static String DEFAULT_ASSERTION_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericAssertionEvaluator";
    public final static String DEFAULT_CONFLICTRESOLVER_CLASS = 
        "org.ebayopensource.aegis.impl.SimpleDenyOverridesConflictResolver";
    private static Properties s_props = null;

    public static TargetEvaluator getTargetEvaluator(String type)
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
    public static RuleEvaluator getRuleEvaluator(String type)
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
    public static AssertionEvaluator getAssertionEvaluator(String type)
    {
        try {
            String cl = getProperty( "attribute."+type+".evalclass");
            if (cl == null)
                cl = DEFAULT_ASSERTION_EVALCLASS;
            Debug.message("MetaData", "getAssertionEvaluator:type="+type+" class="+cl);
            return (AssertionEvaluator) Class.forName(cl).newInstance();
        } catch (Exception ex) {
            Debug.error("MetaData", "getAssertionEvaluator:error loading class:", ex);
            return null;
        }
    }
    public static ConflictResolver getConflictResolver()
    {
        try {
            String cl = getProperty(CONFLICTRESOLVER_CLASS_PARAM);
            if (cl == null)
                cl = DEFAULT_CONFLICTRESOLVER_CLASS;
            Debug.message("MetaData", "getConflictResolver:class="+cl);
            return (ConflictResolver) Class.forName(DEFAULT_CONFLICTRESOLVER_CLASS).newInstance();
        } catch (Exception ex) {
            Debug.error("MetaData", "getConflictResolver:error loading class:", ex);
            return null;
        }
    }
    public static String getProperty(String id)
    {
        if (s_props != null)
            return s_props.getProperty(id);
        return null;
    }
    public static void loadProperties(Properties PDPProperties)
    {
        FileInputStream fin = null;
        String filename = null;
        try {
            filename = PDPProperties.getProperty(METADATA_CONFIG_FILE_PARAM);
            Debug.message("MetaData", "loadProperties : file="+filename);
            s_props = new Properties();
            s_props.load((fin = new FileInputStream(filename)));
            s_props.setProperty(FLATFILE_ATTRIBUTE_STORE, PDPProperties.getProperty(FLATFILE_ATTRIBUTE_STORE));
        } catch (Exception ex) {
            Debug.error("MetaData", "Init:error loading file:"+filename);
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (Exception ex) {}
        }
    }
    public static String getMembershipAttribute(String cat)
    {
        return s_props.getProperty("group."+cat+".membername");
    }
}
