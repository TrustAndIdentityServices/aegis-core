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
//import org.ebayopensource.aegis.impl.GenericActionEvaluator;
//import org.ebayopensource.aegis.impl.GenericConditionEvaluator;
//import org.ebayopensource.aegis.impl.GenericResourceEvaluator;
//import org.ebayopensource.aegis.impl.GenericSubjectEvaluator;
//import org.ebayopensource.aegis.impl.SimpleDenyOverridesConflictResolver;
import org.ebayopensource.aegis.plugin.ActionEvaluator;
import org.ebayopensource.aegis.plugin.ConditionEvaluator;
import org.ebayopensource.aegis.plugin.ConflictResolver;
import org.ebayopensource.aegis.plugin.ResourceEvaluator;
import org.ebayopensource.aegis.plugin.SubjectEvaluator;

public class MetaData
{
    public  final static  String DEFAULT_METADATA_PROPERTIES_FILE = "MetaData.properties";
    public final static String DEFAULT_CONDITION_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericConditionEvaluator";
    public final static String DEFAULT_RESOURCE_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericResourceEvaluator";
    public final static String DEFAULT_ACTION_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericActionEvaluator";
    public final static String DEFAULT_SUBJECT_EVALCLASS=
        "org.ebayopensource.aegis.impl.GenericSubjectEvaluator";
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
    public static SubjectEvaluator getSubjectEvaluator(String type)
    {
        try {
        //TODO read class name from metadata repository - for now return default
       //return new GenericSubjectEvaluator();
            return (SubjectEvaluator) Class.forName(DEFAULT_SUBJECT_EVALCLASS).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }
    public static List<String> getSubjectAttributeNames(String type)
    {
        return null;
    }
    public static ConditionEvaluator getConditionEvaluator(String type)
    {
        try {
        //TODO read class name from metadata repository - for now return default
       //return new GenericConditionEvaluator();
            return (ConditionEvaluator) Class.forName(DEFAULT_CONDITION_EVALCLASS).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }
    public static ResourceEvaluator getResourceEvaluator(String type)
    {
        try {
        //TODO read class name from metadata repository - for now return default
       //return new GenericResourceEvaluator();
            return (ResourceEvaluator) Class.forName(DEFAULT_RESOURCE_EVALCLASS).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }
    public static ActionEvaluator getActionEvaluator(String type)
    {
        //TODO read class name from metadata repository - for now return default
       //return new GenericActionEvaluator();
        try {
            return (ActionEvaluator) Class.forName(DEFAULT_ACTION_EVALCLASS).newInstance();
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

