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
public class ResourceTest {
	
    static private String type = "typex";
    static private String name = "http://www.ebay.com/resource1";
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
        Resource resource = new Resource(type, name); 
        assertEquals(type, resource.getType());
    }

    @Test
    public void testName() {
        Resource resource = new Resource(type, name); 
        assertEquals(name, resource.getName());
    }
    @Test
    public void testEmptyAttrs() {
        Resource resource = new Resource(type, name); 
        assertEquals(null, resource.getAttribute(attr1));
    }
	
    @Test
    public void testSetAttributeNew() {
        Resource resource = new Resource(type, name); 
        resource.setAttribute(attr1, val1);
        assertEquals(val1, resource.getAttribute(attr1));
        resource.setAttribute(attr2, val2);
        assertEquals(val2, resource.getAttribute(attr2));
 
    }
    @Test
    public void testToString() {
        Resource resource = new Resource(type, name); 
        resource.setAttribute(attr1, val1);
        boolean c =  (resource.toString() != null);
        assertEquals(true, c);
    }
}
