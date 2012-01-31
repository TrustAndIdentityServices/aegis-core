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
public class SubjectTest {
	
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
        Subject subject = new Subject(type, name); 
        assertEquals(type, subject.getType());
    }

    @Test
    public void testName() {
        Subject subject = new Subject(type, name); 
        assertEquals(name, subject.getName());
    }
    @Test
    public void testEmptyAttrs() {
        Subject subject = new Subject(type, name); 
        assertEquals(subject.getAttribute(attr1), null);
    }
	
    @Test
    public void testSetAttributeNew() {
        Subject subject = new Subject(type, name); 
        subject.setAttribute(attr1, val1);
        assertEquals(subject.getAttribute(attr1), val1);
        subject.setAttribute(attr2, val2);
        assertEquals(subject.getAttribute(attr2), val2);
 
    }
    @Test
    public void testToString() {
        Subject subject = new Subject(type, name); 
        subject.setAttribute(attr1, val1);
        boolean c =  (subject.toString() != null);
        assertEquals(c, true);
    }
}
