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

import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Policy;

/**
  * Interface to detect and resolve potential "conflicts" resulting from multiple policies evaluations
  * returning different decisions.
  *
  * The conflict resolver is designed as a 2 phase process:
  *   Phase 1 : called multiple times,  once at the end of every matching policy evaluation
  *   Phase 2 : called once at the end when all applicable policies have been evaluated, just
  *             before the final result is returned to the PDP caller.
  */
public interface ConflictResolver
{
     /**
       * Called at the end of each policy evaluation. The resolver is expected to detect conflicts and update the
       *    currentdecision as necessary.
       *
       *  @param currentpolicy the current matching policy
       *  @param currentdecision the accumulating decision object
       *  @param conditiondecision the results of condition evaluation returned from the current policy
       */
     public void resolve(Policy currentpolicy, Decision currentdecision, Decision conditiondecision);
 
     /**
       * Called at the end when all policies have been evaluated. 
       * The resolver is expected to detect conflicts and update the final decision as apporpriate.
       *
       *  @param decision the final policy decision.
       */
     public void resolveFinal(Decision decision);
}
