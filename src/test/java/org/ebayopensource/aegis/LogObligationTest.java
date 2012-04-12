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

public class LogObligationTest {
	
    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code        
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

    @Test
    public void testNoObligation() {
        Decision decision = executePDP(false);
        assertTrue(decision.getObligations() == null);
    }
    @Test
    public void testObligation() {
        Decision decision = executePDP(true);
        Debug.message("obligationTest", decision.getObligations().toString());
        List<Obligation> obligations = decision.getObligations();
        assertFalse(obligations == null);
        Obligation o = obligations.get(0);
        assertEquals(o.getType(), "LOG");
        String logdata =(String)  o.getAttribute("LOGRECORD");
        System.out.println("TEST: logdata="+logdata);
        assertFalse(logdata == null);
        assertTrue(logdata.startsWith("<"));
    }
    
    private Decision executePDP(boolean logobligation)
    {
        // Setup PDP - reuse PDPOnePolicyDataStore.properties
        PolicyDecisionPoint pdp = null;
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDPOnePolicyDataStore.properties");
            props.load(url.openStream());
            // Just to ensure cache is reloaded
            props.setProperty("PDP_ETAG", logobligation ? "10101" : "10102");
            if (logobligation)
                props.setProperty("PDP_LOG_OBLIGATION", "true");
            else
                props.setProperty("PDP_LOG_OBLIGATION", "false");
            pdp = PolicyEnforcementPoint.getPDP(props);
        } catch (Exception ex) {
            System.out.println("Properties File not found:"+ex);
            ex.printStackTrace();
            return null;
        }
        // Get Decision
        Target resource = new Target("web", "http://www.ebay.com/xxx");
        List<Environment> env = new ArrayList<Environment>();
        Environment env1 = new Environment("session", "env1");
        env1.setAttribute("authn.level", new Integer(0));
        env1.setAttribute("role", "manager");
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);
        return decision;
    }
}
