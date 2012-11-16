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

import org.ebayopensource.aegis.Context;
import org.ebayopensource.aegis.Obligation;
import org.ebayopensource.aegis.plugin.ObligationEvaluator;

/**
  * This class evaluates the default ObligationEvaluator.
  */
public class DefaultObligationEvaluator implements ObligationEvaluator
{
    public void initialize(Context ctx) 
    {
    }
    public Obligation getObligation(String id, Context context)
    {
        Obligation ob = new Obligation(id);
        return ob;
    }
}
