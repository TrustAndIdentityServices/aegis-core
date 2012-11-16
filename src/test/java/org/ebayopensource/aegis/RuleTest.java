
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

public class RuleTest {
	
    static private String category = "categoryx";
    static private String name = "namex";
    static private String attrs[] = { "attr1", "attr2", "attr3", "attr4", "attr5", "attr6"};
    static private int ops[] = {Assertion.OP_EQ, Assertion.OP_NE, Assertion.OP_LT, Assertion.OP_GT, Assertion.OP_LE, Assertion.OP_GE };
    static private String vals[] = { "val1", "val2", "val3", "val4", "val5", "val6"};
    static private int INVALID_OP = 6;

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
    public void testCategory() {
        Rule rule = new Rule(category, name, null); 
        assertEquals(category, rule.getCategory());
    }

    @Test
    public void testName() {
        Rule rule = new Rule(category, name, null);
        assertEquals(name, rule.getName());
    }

    @Test
    public void testToString() {
        Rule rule = createARule(Expression.ANY_OF, 2);
        Expression<Assertion> ex1 = rule.getExpression();
        assertEquals((ex1 != null), true);
        assertEquals(ex1.getType(), Expression.ANY_OF);
        int sz = ex1.getMembers().size();
        assertEquals(sz, 2);
        boolean t =  (rule.toString() != null);
    }

    private void testExpr(int op) {
        Rule rule = createARule(Expression.ALL_OF, 6);
        Expression<Assertion> ex1 = rule.getExpression();
        assertEquals((ex1 != null), true);
        ArrayList<Object> members = ex1.getMembers();
        int sz =members.size();
        assertEquals(sz, 6);
        for (int i = 0; i < sz; i++) {
            Assertion a = (Assertion) members.get(i);
            CExpr c = a.getCExpr();
            assertEquals(attrs[i], c.id_);
            assertEquals(ops[i], c.op_);
            assertEquals(vals[i], c.val_);
        }
        assertEquals(ex1.getType(), Expression.ALL_OF);
    }
    private Rule createARule(int exptype, int numassertions)
    {
        Expression<Assertion> ex1 = new Expression<Assertion>();
        ex1.setType(exptype);
        for (int i= 0; i < numassertions; i++) {
            Assertion a1 = new Assertion(category, "a"+i);
            a1.setCExpr(attrs[i], ops[i], vals[i]);
            ex1.add(a1);
        }
        
        Rule rule = new Rule(category, name, ex1); 
        return rule;
        
    }
}
