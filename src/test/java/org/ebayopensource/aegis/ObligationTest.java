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
import java.util.Set;

public class ObligationTest {
	
    static private String type = "typex";
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
        Obligation obligation = new Obligation(type); 
        assertEquals(type, obligation.getType());
    }

    @Test
    public void testEmptyAttrs() {
        Obligation obligation = new Obligation(type); 
        assertEquals(obligation.getAttribute(attr1), null);
    }
	
    @Test
    public void testSetAttributeNew() {
        Obligation obligation = new Obligation(type); 
        obligation.setAttribute(attr1, val1);
        assertEquals(obligation.getAttribute(attr1), val1);
        obligation.setAttribute(attr2, val2);
        assertEquals(obligation.getAttribute(attr2), val2);
    }
    @Test
    public void testGetAttributeIds() {
        Obligation obligation = new Obligation(type); 
        obligation.setAttribute(attr1, val1);
        obligation.setAttribute(attr2, val2);
        Set<String> ks = obligation.getAttributeIds();
        assertEquals(ks.size(), 2);
        boolean attr1Seen = false;
        boolean attr2Seen = false;
        for (String s : ks) {
            String v = (String) obligation.getAttribute(s);
            assert(v != null);
            if (attr1.equals(s)) {
                assertEquals(v, val1);
                attr1Seen = true;
            } else if (attr2.equals(s)) {
                assertEquals(v, val2);
                attr2Seen = true;
            }
        }
        assert(attr1Seen && attr2Seen);
    }
    public void testGetAttributeIdsNull() {
        Obligation obligation = new Obligation(type); 
        Set<String> ks = obligation.getAttributeIds();
        assertEquals(ks, null);
    }
    @Test
    public void testToString() {
        Obligation obligation = new Obligation(type); 
        obligation.setAttribute(attr1, val1);
        boolean c =  (obligation.toString() != null);
        assertEquals(c, true);
    }
}
