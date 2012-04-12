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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Properties;
import java.io.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ebayopensource.aegis.debug.Debug;

public class DebugTest {
	
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
    public void testMessageLevel() {
        try {
            // Setup Properties
            File debugfile = File.createTempFile("DEBUGFILE", ".txt");
            Properties props = new Properties();
            props.setProperty(Debug.DEBUG_LEVEL_PARAM, "message");
            props.setProperty(Debug.DEBUG_FILE_PARAM, debugfile.getAbsolutePath());
            Debug.initialize(props);
            assertEquals(Debug.getLevel(), Debug.LEVEL_MESSAGE);
            assertEquals(countLines(debugfile), 0);
            Debug.message("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 1);
            Debug.setLevel("warning");
            Debug.message("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 1);
            Debug.setLevel("error");
            Debug.message("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 1);
            Debug.setLevel("message");
            Debug.message("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 2);
        } catch (Exception ex) {
            fail("Debug file could not be opened:"+ex);
        }
    }
    @Test
    public void testWarningLevel() {
        try {
            // Setup Properties
            File debugfile = File.createTempFile("DEBUGFILE", ".txt");
            Properties props = new Properties();
            props.setProperty(Debug.DEBUG_LEVEL_PARAM, "warning");
            props.setProperty(Debug.DEBUG_FILE_PARAM, debugfile.getAbsolutePath());
            Debug.initialize(props);
            assertEquals(Debug.getLevel(), Debug.LEVEL_WARNING);
            assertEquals(countLines(debugfile), 0);
            Debug.warning("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 1);
            Debug.setLevel("message");
            Debug.warning("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 2);
            Debug.setLevel("error");
            Debug.warning("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 2);
            Debug.setLevel("warning");
            Debug.warning("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 3);
        } catch (Exception ex) {
            fail("Debug file could not be opened:"+ex);
        }
    }
    @Test
    public void testErrorLevel() {
        try {
            // Setup Properties
            File debugfile = File.createTempFile("DEBUGFILE", ".txt");
            Properties props = new Properties();
            props.setProperty(Debug.DEBUG_LEVEL_PARAM, "error");
            props.setProperty(Debug.DEBUG_FILE_PARAM, debugfile.getAbsolutePath());
            Debug.initialize(props);
            assertEquals(Debug.getLevel(), Debug.LEVEL_ERROR);
            assertEquals(countLines(debugfile), 0);
            Debug.error("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 1);
            Debug.setLevel("message");
            Debug.error("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 2);
            Debug.setLevel("error");
            Debug.error("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 3);
            Debug.setLevel("warning");
            Debug.warning("DebugTest", "hello1");
            assertEquals(countLines(debugfile), 4);
        } catch (Exception ex) {
            fail("Debug file could not be opened:"+ex);
        }
    }
    private int countLines(File debugfile)
    {
        int cnt = 0;
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(new FileReader(debugfile));
            while(rdr.readLine() != null) cnt++;
        } catch (Exception ex) {
            cnt= -1;
        } finally {
            if (rdr != null) {
                try {
                   rdr.close();
                } catch(Exception e) {}
            }
        }
        return cnt;
    }
}
