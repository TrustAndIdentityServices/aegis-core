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

import java.util.List;
import java.util.Properties;

public interface PolicyDecisionPoint
{
    /**
      * Returns a Decision
      * @return Decision
      */
    public Decision getPolicyDecision(List<Subject> subjects, Resource resource, Action action, List<Environment> env);

    /**
      * Initializes the PDP
      */
    public void initialize(Properties props) throws Exception;
}
  
