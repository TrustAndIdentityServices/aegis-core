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

public class ExprTest {
	
    static PolicyDecisionPoint pdp = null;
    @BeforeClass
    public static void oneTimeSetUp() {
        // Setup PDP 
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDPMultipleExprPolicy.properties");
            props.load(url.openStream());
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
      * These tests use 5 policies for same target, Obligations are used to determine which poolicy fired:
      * policy1 : PERMIT, rule: stringattr = "abcd" AND intattr = 1010, Obligations : P1
      * policy2 : PERMIT, rule: stringattr < "abcd" AND intattr < 1010, Obligatios : P2
      * policy3 : PERMIT, rule: stringattr > "abcd" AND intattr > 1010, Obligatios : P3
      * policy4 : PERMIT, rule: stringattr <= "abcd" AND intattr <= 1010, Obligatios : P4
      * policy5 : PERMIT, rule: stringattr >= "abcd" AND intattr >= 1010, Obligatios : P5
      * DENY overrides PERMIT
      * Default : DENY
      * Test params and results:
      *   Test : stringattr, intattr  : expected policies
      * ===========================================
      *   Test1 : "abcd", 1010 :  P1,P4,P5
      *   Test2 : "abcd", 10000 : P5
      *   Test3 : "abcd", 100 : P4
      *   Test4 : "xyz", 1010 : P5
      *   Test5 : "xyz", 10000 : P3, P5
      *   Test6 : "xyz", 100 : NONE
      *   Test7 : "1234", 1010 : P4
      *   Test8 : "1234", 10000 : P5
      *   Test9 : "1234", 100 : P2
      */
    @Test
    public void testTest1() 
    {
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("stringattr", "abcd");
        env1.setAttribute("intattr", new Integer(1010));
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_PERMIT );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        //for (Obligation o : ob) {
            //System.out.println("OBL="+o.getType());
        //}
        assertEquals(3, ob.size());
        assertEquals("P1", ob.get(0).getType());
        assertEquals("P4", ob.get(1).getType());
        assertEquals("P5", ob.get(2).getType());
    }
    @Test
    public void testTest2() 
    {
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("stringattr", "abcd");
        env1.setAttribute("intattr", new Integer(10000));
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_PERMIT );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        assertEquals(1, ob.size());
        assertEquals("P5", ob.get(0).getType());
    }
    @Test
    public void testTest3() 
    {
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("stringattr", "abcd");
        env1.setAttribute("intattr", new Integer(100));
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_PERMIT );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        assertEquals(1, ob.size());
        assertEquals("P4", ob.get(0).getType());
    }
    @Test
    public void testTest4() 
    {
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("stringattr", "xyz");
        env1.setAttribute("intattr", new Integer(1010));
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_PERMIT );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        assertEquals(1, ob.size());
        assertEquals("P5", ob.get(0).getType());
    }
    @Test
    public void testTest5() 
    {
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("stringattr", "xyz");
        env1.setAttribute("intattr", new Integer(10000));
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_PERMIT );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob != null);
        assertEquals(2, ob.size());
        assertEquals("P3", ob.get(0).getType());
        assertEquals("P5", ob.get(1).getType());
    }
    @Test
    public void testTest6() 
    {
        Target resource = new Target("webcmd", "cmdX");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("stringattr", "xyz");
        env1.setAttribute("intattr", new Integer(100));
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);

        // Decision should be PERMIT 
        assertEquals(decision.getType(), Decision.EFFECT_DENY );
        List<Obligation> ob = decision.getObligations();
        assertTrue(ob == null);
    }
}
