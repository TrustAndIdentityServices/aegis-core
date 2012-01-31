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

public class ConditionTest {
	
    static private String type = "typex";
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
        Condition condition = new Condition(type, name); 
        assertEquals(type, condition.getType());
    }

    @Test
    public void testName() {
        Condition condition = new Condition(type, name); 
        assertEquals(name, condition.getName());
    }
    @Test
    public void testExprEQOperation() {
        testExpr(Condition.OP_EQ);
    }
    @Test
    public void testExprNEOperation() {
        testExpr(Condition.OP_NE);
    }
	
    @Test
    public void testExprLTOperation() {
        testExpr(Condition.OP_LT);
    }

    @Test
    public void testExprGTOperation() {
        testExpr(Condition.OP_GT);
    }

    @Test
    public void testExprInvalidOperation() {
        try {
            Condition condition = new Condition(type, name); 
            condition.addExpr(attr1, INVALID_OP, val1);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    @Test
    public void testExprInvalidId() {
        try {
            Condition condition = new Condition(type, name); 
            condition.addExpr(null, Condition.OP_EQ, val1);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    @Test
    public void testExprInvalidValue() {
        try {
            Condition condition = new Condition(type, name); 
            condition.addExpr(attr1, Condition.OP_EQ, null);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }

    @Test
    public void testToString() {
        Condition condition = new Condition(type, name); 
        condition.addExpr(attr1, Condition.OP_EQ, val1);
        condition.addExpr(attr2, Condition.OP_GT, val2);
        assertEquals(2, condition.getAllAttrs().size());
        boolean c =  (condition.toString() != null);
        assertEquals(c, true);
    }

    private void testExpr(int op) {
        Condition condition = new Condition(type, name); 
        condition.addExpr(attr1, op, val1);
        ArrayList<CExpr> al = condition.getAllAttrs();
        assertEquals(1, al.size());
        CExpr e = al.get(0);
        boolean b = (e != null);
        assertEquals(true, b);
        assertEquals(attr1, e.id_);
        assertEquals(op, e.op_);
        assertEquals(val1, e.val_);
    }
}
