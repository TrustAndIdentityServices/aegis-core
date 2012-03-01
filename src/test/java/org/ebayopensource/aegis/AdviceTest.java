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
public class AdviceTest {
	
    static private String type = "typex";
    static private String attr1 = "attr1";
    static private String val1 = "val1";
    static private String attr2 = "attr2";
    static private String val2 = "val2";
    static private int INVALID_OP = Assertion.OP_MAX+1;

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
        Advice advice = new Advice(type); 
        assertEquals(type, advice.getType());
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

    private void testExpr(int op) {
        Advice advice = new Advice(type); 
        advice.addExpr(attr1, op, val1);
        ArrayList<CExpr> al = advice.getAllExpr();
        assertEquals(1, al.size());
        CExpr e = al.get(0);
        boolean b = (e != null);
        assertEquals(true, b);
        assertEquals(attr1, e.id_);
        assertEquals(op, e.op_);
        assertEquals(val1, e.val_);
    }
    @Test
    public void testExprInvalidOperation() {
        try {
            Advice advice = new Advice(type); 
            advice.addExpr(attr1, INVALID_OP, val1);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    @Test
    public void testExprInvalidId() {
        try {
            Advice advice = new Advice(type); 
            advice.addExpr(null, Assertion.OP_EQ, val1);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    @Test
    public void testExprInvalidValue() {
        try {
            Advice advice = new Advice(type); 
            advice.addExpr(attr1, Assertion.OP_EQ, null);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    @Test
    public void testEmptyAttrs() {
        Advice advice = new Advice(type); 
        assertEquals(advice.getAllExpr(), null);
    }
    @Test
    public void testExprAttrs() {
        Advice advice = new Advice(type); 
        advice.addExpr(attr1, Assertion.OP_EQ, val1);
        advice.addExpr(attr2, Assertion.OP_NE, val2);
        ArrayList<CExpr> es = advice.getAllExpr();
        boolean b = (es != null);
        assertEquals(true, b);
        assertEquals(2, es.size());
        CExpr c1 = es.get(0);
        assertEquals(attr1, c1.id_);
        assertEquals(val1, c1.val_);
        assertEquals(Assertion.OP_EQ, c1.op_);
        CExpr c2 = es.get(1);
        assertEquals(attr2, c2.id_);
        assertEquals(val2, c2.val_);
        assertEquals(Assertion.OP_NE, c2.op_);
    }
	
    @Test
    public void testToString() {
        Advice advice = new Advice(type); 
        boolean c =  (advice.toString() != null);
        assertEquals(c, true);
    }
}
