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
    public  final static  String DEFAULT_METADATA_PROPERTIES_FILE = "MetaData.properties";
    public final static String DEFAULT_TARGET_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericTargetEvaluator";
    public final static String DEFAULT_RULE_EVALCLASS= null;
       // "org.ebayopensource.aegis.impl.GenericRuleEvaluator";
    public final static String DEFAULT_ASSERTION_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericAssertionEvaluator";
    public final static String DEFAULT_CONFLICTRESOLVER_CLASS = 
        "org.ebayopensource.aegis.impl.SimpleDenyOverridesConflictResolver";
    private static Properties _props = null;
    static {
        // TODO initialize _props based on classpath
        try {
            Properties _props = new Properties();
            _props.load(new FileInputStream(DEFAULT_METADATA_PROPERTIES_FILE));
        } catch (Exception ex) {
            Debug.error("MetaData", "Init:error loading file:"+DEFAULT_METADATA_PROPERTIES_FILE);
        }
    }
    public static TargetEvaluator getTargetEvaluator(String type)
    {
        try {
        //TODO read class name from metadata repository - for now return default
            return (TargetEvaluator) Class.forName(DEFAULT_TARGET_EVALCLASS).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }
    public static RuleEvaluator getRuleEvaluator(String type)
    {
        try {
        //TODO read class name from metadata repository - for now return default
       //return new GenericResourceEvaluator();
            return (RuleEvaluator) Class.forName(DEFAULT_RULE_EVALCLASS).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }
    public static AssertionEvaluator getAssertionEvaluator(String type)
    {
        try {
        //TODO read class name from metadata repository - for now return default
       //return new GenericResourceEvaluator();
            return (AssertionEvaluator) Class.forName(DEFAULT_ASSERTION_EVALCLASS).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }
    public static ConflictResolver getConflictResolver()
    {
        try {
            //TODO read class name from metadata repository - for now return default
            return (ConflictResolver) Class.forName(DEFAULT_CONFLICTRESOLVER_CLASS).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }
    public static String getProperty(String id)
    {
        if (_props != null)
            return _props.getProperty(id);
        return null;
    }
}

