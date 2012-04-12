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

import java.util.*;
import java.io.*;
import java.net.*;

import org.ebayopensource.aegis.debug.Debug;
/**
  * This test is driven by PDPFlatFileAttr.properties, FlatFileAttr.properties and FlatFileAttrPolicues.json.
  * And a custom AssertionEvaluator : <code>FlatFileAssertionEvaluator</code>
  *
  */

public class FlatFileAttrPoliciesTest
{
    static private PolicyDecisionPoint pdp = null;
    static private File aegisbasedir = null;
    static private String[] groups = {
        "c5d52d9f-e7b0-4647-b356-af22bfe91da5|PartnerAppRole|SELLER||",
        "01d08e5b-9501-4e5a-8349-34d3565e3ea7|PartnerAppRole|WhiteListedSELLERS||",
        "8d39efed-2023-4f05-a85e-ba94714adfea|eBayAPIGroup|BUYER_APIS||",
        "bd7e0740-6ae4-4195-bef3-4cd0c7d0b9a7|eBayAPIGroup|SELLER_APIS||"
    };
    static private String[] memberfiles = {
        "01d08e5b-9501-4e5a-8349-34d3565e3ea7.txt",
        "bd7e0740-6ae4-4195-bef3-4cd0c7d0b9a7.txt",
        "8d39efed-2023-4f05-a85e-ba94714adfea.txt",
        "c5d52d9f-e7b0-4647-b356-af22bfe91da5.txt"
    };
    static private String[] members = {
      "app1|",
      "addItem|",
      "buyItem|",
      "app1|" 
    };

    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code        
        try {
            File tmpdir = new File(System.getProperty("java.io.tmpdir"));
            String randname = ""+(new java.util.Random()).nextInt(Integer.MAX_VALUE);
            aegisbasedir = new File(tmpdir, randname);
            if (!aegisbasedir.mkdirs())
                fail("Unable to open aegisbasedir:"+aegisbasedir);
            File groupdir = new File(aegisbasedir, "groupmembers"); 
            if (!groupdir.mkdirs())
                fail("Unable to open aegis groupdir:"+groupdir);
            File attrgroups = new File(aegisbasedir, "attrgroups.txt"); 
            PrintStream ps = new PrintStream(attrgroups);
            for (String s : groups) {
               ps.println(s);
            }
            ps.close();
            for (int i =  0; i <  memberfiles.length; i++) {
                File mf = new File(groupdir, memberfiles[i]);
                ps = new PrintStream(mf);
                ps.println(members[i]);
                ps.close();
            }
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDPFlatFileAttr.properties");
            props.load(url.openStream());
            // update the
            props.setProperty("FLATFILE_ATTRIBUTE_STORE", aegisbasedir.getAbsolutePath());
            Debug.message("FlatFileAttrTest", "oneTimeSetup:props="+props);
            pdp = PolicyEnforcementPoint.getPDP(props);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to get PDP:"+ex);
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // Remove files
        deleteFile(aegisbasedir);
    }
    @Before
    public void setUp()
    {
    }
    @After
    public void tearDown() {
    }

    @Test
    public void testPERMIT() {
        // Setup Target to evaluate
        String targettype = "apicmd";
        String targetname = "addItem";
        Target resource = new Target(targettype, targetname);
        System.out.println("Target="+resource);

        // Setup Environment
        Environment env = new Environment("env", "env");
        env.setAttribute("partnerappid", "app1");
        env.setAttribute("AppRiskScore", 10000);
        System.out.println("Env="+env);
 
        // Get Decision
        Decision decision = pdp.getPolicyDecision( resource, env);

        System.out.println("evaluatePolicy Decision:"+decision.toString());
        List<Advice> advs = decision.getAdvices();
        assertEquals(Decision.EFFECT_PERMIT, decision.getType());
    }
    static private void copyFile(InputStream in, OutputStream out)
    {
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        } catch(Exception ex){
            System.out.println(ex.getMessage() + " in the specified directory.");
        }
    }
    static private void deleteFile(File f) {
        try {
            if (f.isDirectory()) {
                for (File c : f.listFiles())
                deleteFile(c);
            }
            f.delete();
        } catch (Exception ex) {
            System.out.println("FlatFileAttrTest cleanup failed:"+ex);
        }
    }
}
