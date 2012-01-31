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

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.debug.Debug;
import org.ebayopensource.aegis.impl.GenericActionEvaluator;
import org.ebayopensource.aegis.impl.GenericConditionEvaluator;
import org.ebayopensource.aegis.impl.GenericResourceEvaluator;
import org.ebayopensource.aegis.impl.GenericSubjectEvaluator;
import org.ebayopensource.aegis.impl.SimpleDenyOverridesConflictResolver;
import org.ebayopensource.aegis.plugin.ActionEvaluator;
import org.ebayopensource.aegis.plugin.ConditionEvaluator;
import org.ebayopensource.aegis.plugin.ConflictResolver;
import org.ebayopensource.aegis.plugin.ResourceEvaluator;
import org.ebayopensource.aegis.plugin.SubjectEvaluator;

public class MetaData
{
    public  final static  String DEFAULT_METADATA_PROPERTIES_FILE = "MetaData.properties";
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
        //TODO read class name from metadata repository - for now return default
       return new GenericSubjectEvaluator();
    }
    public static List<String> getSubjectAttributeNames(String type)
    {
        return null;
    }
    public static ConditionEvaluator getConditionEvaluator(String type)
    {
        //TODO read class name from metadata repository - for now return default
       return new GenericConditionEvaluator();
    }
    public static ResourceEvaluator getResourceEvaluator(String type)
    {
        //TODO read class name from metadata repository - for now return default
       return new GenericResourceEvaluator();
    }
    public static ActionEvaluator getActionEvaluator(String type)
    {
        //TODO read class name from metadata repository - for now return default
       return new GenericActionEvaluator();
    }
    public static ConflictResolver getConflictResolver()
    {
        //TODO read class name from metadata repository - for now return default
       return new SimpleDenyOverridesConflictResolver();
    }
    public static String getProperty(String id)
    {
        if (_props != null)
            return _props.getProperty(id);
        return null;
    }
}

