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

import java.util.Properties;

import org.ebayopensource.aegis.debug.Debug;
public class PolicyEnforcementPoint
{
    public static final String PDP_CLASS = "pdp.class";
    public static final String DEFAULT_PDP_CLASS = "org.ebayopensource.aegis.impl.EmbeddedPolicyDecisionPoint";

    /**
      * Returns a PDP instance
      * @param props config parameters
      * @return instance of the appropriate PDP
      * @throws Exception in case of errors
      */
    public static PolicyDecisionPoint getPDP(Properties props) throws Exception
    {
        // Instantiate impl (Remove vs Embedded) - TODO onlt embedded for now
        String implClass = props.getProperty(PDP_CLASS);
        if (implClass == null)
            implClass= DEFAULT_PDP_CLASS;
        Debug.message("PEP", "\n\nSTART =============== getPDP():"+implClass);
        PolicyDecisionPoint impl = (PolicyDecisionPoint) Class.forName(implClass).newInstance();
        impl.initialize(props);
        return impl;
    }

}
  
