
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
package org.ebayopensource.aegis.plugin;

import java.util.HashMap;
import java.util.List;

import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.Context;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Rule;

public interface RuleEvaluator
{
    public void initialize(Context ctx);
    public Decision evaluate(Rule condition, Context context) throws Exception;
}
