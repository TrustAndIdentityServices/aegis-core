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

import java.net.*;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContextTest {
	
    private Context ctx1 = null;
    private Properties pdpprops= null;
    
    private String cat1 = "cat1";
    private String name1 = "name1";
    private String attr1 = "attr1";
    private String val1 = "val1";
    private String cat2 = "cat2";
    private String name2 = "name2";
    private String attr2 = "attr2";
    private String val2 = "val1";

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
        try {
            Environment env1 = new Environment(cat1, name1);
            env1.setAttribute(attr1, val1);
            Environment env2 = new Environment(cat2, name2);
            env1.setAttribute(attr2, val2);
            ArrayList<Environment> env = new ArrayList<Environment>();
            env.add(env1);
            env.add(env2);
            pdpprops = new Properties();
            URL url = ClassLoader.getSystemResource("PDP.properties");
            pdpprops.load(url.openStream());
            pdpprops.setProperty("pdpprop1", "pdpval1");
            ctx1 = new Context(pdpprops, env);
        } catch (Exception ex) {
            System.out.println("setUp: ex="+ex);
        }
        assert(ctx1 != null);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testETag() {
        assertEquals(pdpprops.getProperty("PDP_ETAG"), ""+ctx1.getEtag());
    }
    @Test
    public void testAuditLogger() {
        assert(ctx1.getAuditLogger() != null);
    }
    @Test
    public void testPDPProperty() {
        assert(ctx1.getPDPProperty("pdpprop1").equals(pdpprops.getProperty("pdpprop1")));
        assert(ctx1.getPDPProperty("nonexistsntproperty") == null);
    }
    @Test
    public void testId() {
        assert(ctx1.getId() >= 0);
    }
    @Test
    public void testSharedState() {
        assert(ctx1.getSharedState("state1") == null);
        ctx1.setSharedState("state1", "state1");
        assert(ctx1.getSharedState("state1").equals("state1" ));
        ctx1.setSharedState("state1", null);
        assert(ctx1.getSharedState("state1") == null);
    }
    @Test
    public void testEnvValue() {
        assert(ctx1.getEnvValue("nonexistent") == null);
        assert(ctx1.getEnvValue(attr1).equals(val1));
        assert(ctx1.getEnvValue(attr2).equals(val2));
    }
    @Test
    public void testMetaData() {
        assert(ctx1.getMetaData() != null);
    }
	
}
