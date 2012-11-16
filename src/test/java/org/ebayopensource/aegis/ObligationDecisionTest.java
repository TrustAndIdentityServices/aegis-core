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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.ebayopensource.aegis.debug.Debug;

public class ObligationDecisionTest {
	
    static PolicyDecisionPoint pdp = null;
    @BeforeClass
    public static void oneTimeSetUp() {
        // Setup PDP 
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDPSimpleObligations.properties");
            props.load(url.openStream());
            //if (logobligation)
                //props.setProperty("PDP_LOG_OBLIGATION", "true");
                //else
                //props.setProperty("PDP_LOG_OBLIGATION", "false");
            //props.setProperty("PDP_LOG_OBLIGATION_FORMAT", format);
            pdp = PolicyEnforcementPoint.getPDP(props);
        } catch (Exception ex) {
            System.out.println("ObligationDecisionTest : setup failed :"+ex);
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
      * These tests use 4 policies for same target:
      * policy1 : PERMIT, silent, rule: attr1 = val1 AND attr2 = val2, Obligatios : SO11 and SO12 
      * policy2 : PERMIT, non-silent, rule: attr1 = val1 AND attr2 = val2, Obligatios : O21 and O22 
      * policy3 : PERMIT, non-silent, rule: attr1 = val1 AND attr2 = val2 AND attr3 = val3, Obligatios : O31 and O32 
      * policy4 : DENY, non-silent, rule: attr1 = val1 AND attr2 = val2 AND attr3 = val3 AND attr4 = val4, Obligatios : O41 and O42 
      * DENY overrides PERMIT
      * Default : DENY
      */
    @Test
    public void testSilentPolicyMatch() 
    {
        // TEST :  policy1 (silent) and policy2 match
        // Expected Decision : PERMIT,  obligations : O11, O21 only
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("attr1", "val1");
        env1.setAttribute("attr2", "val2");
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_PERMIT );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        assertEquals(2, ob.size());
        assertEquals("O21", ob.get(0).getType());
        assertEquals("O22", ob.get(1).getType());
    }
    @Test
    public void testMultiplePolicyMatch() 
    {
        // TEST :  policy1 (silent), policy2 & policy3  match
        // Expected Decision : PERMIT,  obligations : O11, O21, O31, O32 only
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("attr1", "val1");
        env1.setAttribute("attr2", "val2");
        env1.setAttribute("attr3", "val3");
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_PERMIT );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        assertEquals(4, ob.size());
        assertEquals("O21", ob.get(0).getType());
        assertEquals("O22", ob.get(1).getType());
        assertEquals("O31", ob.get(2).getType());
        assertEquals("O32", ob.get(3).getType());
    }
    @Test
    public void testDenyPolicyMatch() 
    {
        // TEST :  policy1 (silent), policy2, policy3 & policy4  match
        // Expected Decision : DENY,  obligations : O41, O42 only
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("attr1", "val1");
        env1.setAttribute("attr2", "val2");
        env1.setAttribute("attr3", "val3");
        env1.setAttribute("attr4", "val4");
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be DENY 
        assertEquals(decision.getType(), Decision.EFFECT_DENY );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        assertEquals(2, ob.size());
        assertEquals("O41", ob.get(0).getType());
        assertEquals("O42", ob.get(1).getType());
    }
    @Test
    public void testDefaultPolicyMatch() 
    {
        // TEST :  none match
        // Expected Decision : DENY,  obligations : empty
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("attr1", "XXXXXXXX");
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        List<Obligation> ob = decision.getObligations();
        assertTrue(ob == null);
    }
}
