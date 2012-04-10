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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ebayopensource.aegis.Advice;
import org.ebayopensource.aegis.CExpr;
import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Context;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.PolicyException;
import org.ebayopensource.aegis.plugin.AssertionEvaluator;
import org.ebayopensource.aegis.debug.Debug;

/**
  * This class evaluates a Assertion based on the data in the context.
  * Restrictions:
  * All data it needs must be available in the input <code>context</code>
  * Only primitive types that respond correctly to <code>java.lang.Comparable</code> are acceptable
  * TODO : add collection support ( if (x instanceof Collection<?>){})
  */
public class GenericAssertionEvaluator extends BaseAssertionEvaluator
{
    public void initialize(Context ctx) 
    {
    }
    public Object getValue(String id, Context ctx)
    {
        return ctx.getEnvValue(id);
    }
    public boolean isMember(String parentcategory, Object parentname, Object member, Context ctx)
    {
        return false;
    }
}
