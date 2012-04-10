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

import java.util.*;

public class PolicyTest {
	
   
    static private String name = "testpolicy";
    static private String desc = "test description";

    private Policy pol1 = null;
    private Target target1 = new Target("type1", "name1");
    private Target target2 = new Target("type2", "name2");
//public Policy(String name, String desc, List<Target> targets,
                  //Expression<Rule> rules, Effect effect)
    private Rule rule1 = new Rule("category1", "name1", null);
    private Rule rule2 = new Rule("category2", "name2", null);
    private Effect effect =  new Effect(Effect.PERMIT);

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
        // Create a policy with 2 targets
        ArrayList<Target> targets = new ArrayList<Target>();
	targets.add(target1);
        targets.add(target2);
        Expression<Rule> exp = new Expression<Rule>();
        exp.add(rule1);
        exp.add(rule2);
        pol1 = new Policy(name, desc, targets, exp, effect);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testId() {
        
        String id = pol1.getId(); 
        assertEquals(id, null);
        pol1.setId("IDXXX");
        id = pol1.getId(); 
        assertEquals(id, "IDXXX");
    }

    @Test
    public void testName() {
        String nm = pol1.getName();
        assertEquals(nm, name);
    }
    @Test
    public void testDesc() {
        String d = pol1.getDescription();
        assertEquals(d, desc);
        pol1.setDescription("DESCXXX");
        d = pol1.getDescription();
        assertEquals(d, "DESCXXX");
    }
    @Test
    public void testSilent() {
        boolean s = pol1.isSilent();
        assertEquals(s, false);
        pol1.setSilent(true);
        s = pol1.isSilent();
        assertEquals(s, true);
    }
    @Test
    public void testTargets() {
        List<Target> t = pol1.getTargets();
        assert(t != null);
        assertEquals(t.size(), 2);
    }
    @Test
    public void testEffect() {
        Effect e = pol1.getEffect();
        assert(e != null);
        assertEquals(e.get(), Effect.PERMIT);
    }
    @Test
    public void testRules() {
        Expression<Rule> r = pol1.getRules();
        assert(r != null);
        assertEquals(r.getMembers().size(), 2);
    }
    @Test
    public void testActive() {
        boolean a = pol1.isActive();
        assertEquals(a, true);
        pol1.setActive(false);
        a = pol1.isActive();
        assertEquals(a, false);
    }

    @Test
    public void testToString() {
        String s = pol1.toString();
        assert(s != null);
    }
}
