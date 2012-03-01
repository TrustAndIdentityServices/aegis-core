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

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssertionTest {
	
    static private String category = "categoryx";
    static private String name = "namex";
    static private String attr1 = "attr1";
    static private String val1 = "val1";
    static private String attr2 = "attr2";
    static private String val2 = "val2";
    static private int INVALID_OP = 4;

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
        Assertion assertion = new Assertion(category, name); 
        assertEquals(category, assertion.getCategory());
    }

    @Test
    public void testName() {
        Assertion assertion = new Assertion(category, name); 
        assertEquals(name, assertion.getName());
    }
    @Test
    public void testExprEQOperation() {
        testExpr(Assertion.OP_EQ);
    }
    @Test
    public void testExprNEOperation() {
        testExpr(Assertion.OP_NE);
    }
	
    @Test
    public void testExprLTOperation() {
        testExpr(Assertion.OP_LT);
    }

    @Test
    public void testExprGTOperation() {
        testExpr(Assertion.OP_GT);
    }

    @Test
    public void testExprInvalidOperation() {
        try {
            Assertion assertion = new Assertion(category, name); 
            assertion.setCExpr(attr1, INVALID_OP, val1);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    @Test
    public void testExprInvalidId() {
        try {
            Assertion assertion = new Assertion(category, name); 
            assertion.setCExpr(null, Assertion.OP_EQ, val1);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    @Test
    public void testExprInvalidValue() {
        try {
            Assertion assertion = new Assertion(category, name); 
            assertion.setCExpr(attr1, Assertion.OP_EQ, null);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }

    @Test
    public void testToString() {
        Assertion assertion = new Assertion(category, name); 
        assertion.setCExpr(attr1, Assertion.OP_EQ, val1);
        assertion.setCExpr(attr2, Assertion.OP_GT, val2);
        CExpr c = assertion.getCExpr();
        assertEquals((c != null), true);
        assertEquals(c.id_, attr2);
        assertEquals(c.val_, val2);
        assertEquals(c.op_, Assertion.OP_GT);
        boolean t =  (assertion.toString() != null);
    }

    private void testExpr(int op) {
        Assertion assertion = new Assertion(category, name); 
        assertion.setCExpr(attr1, op, val1);
        CExpr e = assertion.getCExpr();
        boolean b = (e != null);
        assertEquals(true, b);
        assertEquals(attr1, e.id_);
        assertEquals(op, e.op_);
        assertEquals(val1, e.val_);
    }
}
