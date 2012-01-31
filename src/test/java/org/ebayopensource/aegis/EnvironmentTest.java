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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class EnvironmentTest {
	
    static private String type = "typex";
    static private String name = "namex";
    static private String attr1 = "attr1";
    static private String val1 = "val1";
    static private String attr2 = "attr2";
    static private String val2 = "val2";

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
    public void testType() {
        Environment environment = new Environment(type, name); 
        assertEquals(type, environment.getType());
    }

    @Test
    public void testName() {
        Environment environment = new Environment(type, name); 
        assertEquals(name, environment.getName());
    }
    @Test
    public void testEmptyAttrs() {
        Environment environment = new Environment(type, name); 
        assertEquals(environment.getAttribute(attr1), null);
    }
	
    @Test
    public void testSetAttributeNew() {
        Environment environment = new Environment(type, name); 
        environment.setAttribute(attr1, val1);
        assertEquals(environment.getAttribute(attr1), val1);
        environment.setAttribute(attr2, val2);
        assertEquals(environment.getAttribute(attr2), val2);
 
    }
    @Test
    public void testToString() {
        Environment environment = new Environment(type, name); 
        environment.setAttribute(attr1, val1);
        boolean c =  (environment.toString() != null);
        assertEquals(c, true);
    }
}
