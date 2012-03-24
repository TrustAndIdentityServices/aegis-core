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

import java.util.HashMap;
import java.util.List;

import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.Target;
import org.ebayopensource.aegis.plugin.TargetEvaluator;

/**
  * This class evaluates a Target based on the data in the context.
  * Restrictions:
  * All data it needs must be available in the input <code>context</code>
  * Only primitive types that respond correctly to <code>java.lang.Comparable</code> are acceptable
  * TODO : add collection support ( if (x instanceof Collection<?>){})
  */
public class GenericTargetEvaluator implements TargetEvaluator
{
    final static String WILD_CHAR = "*";
    public void initialize(HashMap props) 
    {
    }
    public boolean evaluate(Target reqresource, Target polresource, List<Environment>  context) throws Exception
    {
        if (polresource.getName().equals(WILD_CHAR))
            return reqresource.getType().equals(polresource.getType());
        else
            return reqresource.getType().equals(polresource.getType()) && reqresource.getName().equals(polresource.getName());
    }
}
