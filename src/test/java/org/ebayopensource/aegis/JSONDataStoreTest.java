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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.util.*;
import org.ebayopensource.aegis.impl.*;

public class JSONDataStoreTest {
	
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
    public void testCreate() {
        File policyfile = null;
        try {
            policyfile = getTmpFile();
            PolicyStore pstore = setupPolicyStore(policyfile);
            List<Policy> policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 0);
            Policy pol1 = newPolicy("pol1", "desc1");
            pstore.createPolicy(pol1);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 1);
            Policy pol2 = newPolicy("pol2", "desc2");
            pstore.createPolicy(pol2);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 2);
        } catch (Exception ex)  {
            ex.printStackTrace();
            fail("Exception:"+ex);
        } finally {
            if (policyfile != null) {
                policyfile.delete();
            }
        }
    }

    @Test
    public void testModify() {
        File policyfile = null;
        try {
            policyfile = getTmpFile();
            PolicyStore pstore = setupPolicyStore(policyfile);
            List<Policy> policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 0);
            Policy pol1 = newPolicy("pol1", "desc1");
            pstore.createPolicy(pol1);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 1);
            Policy pol2 = newPolicy("pol2", "desc2");
            pstore.createPolicy(pol2);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 2);
            // Update
            pol1 = policyList.get(0);
            pol1.setDescription("desc3");
            pol2 = policyList.get(1);
            pol2.setDescription("desc4");
            pstore.updatePolicy(pol1);
            pstore.updatePolicy(pol2);
            
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 2);
            pol1 = policyList.get(0);
            assertEquals(pol1.getDescription(), "desc3");
            pol2 = policyList.get(1);
            assertEquals(pol2.getDescription(), "desc4");
        } catch (Exception ex)  {
            ex.printStackTrace();
            fail("Exception:"+ex);
            
        } finally {
            if (policyfile != null)
                policyfile.delete();
        }
    }
    @Test
    public void testDelete() {
        File policyfile = null;
        try {
            policyfile = getTmpFile();
            PolicyStore pstore = setupPolicyStore(policyfile);
            List<Policy> policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 0);
            Policy pol1 = newPolicy("pol1", "desc1");
            pstore.createPolicy(pol1);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 1);
            Policy pol2 = newPolicy("pol2", "desc2");
            pstore.createPolicy(pol2);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 2);
            // Delete
            pol1 = policyList.get(0);
            pol2 = policyList.get(1);
            pstore.deletePolicy(pol1);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 1);

            pstore.deletePolicy(pol2);
            policyList = pstore.getAllPolicies();
            assertEquals(policyList.size(), 0);
        } catch (Exception ex)  {
            ex.printStackTrace();
            fail("Exception:"+ex);
            
        } finally {
            if (policyfile != null)
                policyfile.delete();
        }
    }
    private Policy newPolicy(String nm, String desc)
    {
        Policy pol = new Policy(nm, desc, null,
                  null, new Effect(Effect.PERMIT));
        return pol;
    }
    private File getTmpFile() throws Exception
    {
        File policyfile = File.createTempFile("JSONDS", ".json");
        return policyfile;
    }
    private PolicyStore setupPolicyStore(File policyfile) throws Exception
    {
        String location = "file:///"+policyfile.getCanonicalPath();
        Properties props = new Properties();
        props.setProperty(PolicyStore.PolicyStoreLocation, location);
        JSONPolicyStore pstore = new JSONPolicyStore();
        pstore.initialize(props);
        return pstore;
    }
}
