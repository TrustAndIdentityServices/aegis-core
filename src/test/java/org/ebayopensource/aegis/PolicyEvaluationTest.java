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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.debug.Debug;
import org.junit.Before;
import org.junit.Test;

public class PolicyEvaluationTest
{
    private PolicyDecisionPoint pdp = null;
    @Before
    public void setUp()
    {
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDPOnePolicyDataStore.properties");
            props.load(url.openStream());
            pdp = PolicyEnforcementPoint.getPDP(props);
            //TODO : refactor existing Generic datastore as mock
        } catch (Exception ex) {
            fail("Properties File not found");
        }
    }

    @Test
    public void testSimpleAdvice() {
        List<Subject> subjects = new ArrayList<Subject>();
        Subject sub1 = new Subject("role", "manager");
        subjects.add(sub1);

        Resource resource = new Resource("web", "http://www.ebay.com/xxx");
        Action action = new Action("cmd", "addItem");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("authn.level", new Integer(0));
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision(subjects, resource, action, env);
        Debug.message("TEST", decision.toString());
        // Decision should not be null
        assertEquals(true, decision != null);
        // Decision should be DENY
        assertEquals(Decision.EFFECT_DENY, decision.getType());
        List<Advice> advs = decision.getAdvices();
        assertEquals(true, advs != null);
        assertEquals(1, advs.size());
        
    }
}
