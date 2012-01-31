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
public class EffectTest {
    private int INVALID_EFFECT = Effect.MAX+1;
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
    public void testValidDenyType() {
        testValidType(Effect.DENY);
    }

    @Test
    public void testValidPermitType() {
        testValidType(Effect.PERMIT);
    }

    @Test
    public void testValidUnknownType() {
        testValidType(Effect.UNKNOWN);
    }
    @Test
    public void testInvalidType() {
        try {
            Effect effect = new Effect(INVALID_EFFECT); 
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
        Effect effect = new Effect(Effect.PERMIT); 
        try {
            effect.set(INVALID_EFFECT);
            assertEquals(false, true);
        } catch (Exception ex) {
            assertEquals(true, true);
        }
    }
    private void testValidType(int type) {
        Effect effect = new Effect(type); 
        assertEquals(type, effect.get());
        effect.set(type);
        assertEquals(type, effect.get());
    }

    @Test
    public void testToString() {
        Effect effect = new Effect(Effect.PERMIT); 
        boolean c =  (effect.toString() != null);
        assertEquals(c, true);
    }
}
