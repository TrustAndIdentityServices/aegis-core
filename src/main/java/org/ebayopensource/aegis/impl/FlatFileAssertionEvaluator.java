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
package org.ebayopensource.aegis.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.ebayopensource.aegis.Advice;
import org.ebayopensource.aegis.CExpr;
import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.plugin.AssertionEvaluator;
import org.ebayopensource.aegis.debug.Debug;
import org.ebayopensource.aegis.md.MetaData;

/**
  * This class evaluates a Assertion based on the data in a flatfile
  */
public class FlatFileAssertionEvaluator extends GenericAssertionEvaluator
{
    public void initialize(HashMap props) 
    {
        super.initialize(props);
    }
    public Decision evaluate(Assertion assertion, List<Environment> context) throws Exception
    {
        CExpr expr = assertion.getCExpr();
        CExpr e = expr;
        Decision d = new Decision(Decision.RULE_MATCH);
        Advice adv = null;
        Object cval = null;

        // Get membership attribute
        String membershipAttr = MetaData.getMembershipAttribute(e.id_);

        // TODO : make sure e.op_ is one of the supported operations : currently "=" or "in" only.

        // Retrieve membership attr value from the environment
        for (Environment env : context) {
            cval = env.getAttribute(membershipAttr);
            if (cval != null)
                break;
        }

        // Check membership - is membershipAttr value a member of assertion name
        boolean match = checkMembership(e.id_, e.val_, cval);
        Debug.message("FlatFileAssertionEvaluator", "evaluate:match="+match);
        // if no match, change decision state and add to Advice, 
        if (match == false) {
            d.setType(Decision.RULE_NOMATCH);
            if (adv == null)
                adv = new Advice("x");
            adv.addExpr(e.id_, e.op_, e.val_);
        }
        if (adv != null) {
            d.addAdvice(adv);
        }
        return d;
    }
    private boolean checkMembership(String parentcategory, Object parentname, Object member)
    {
        // Get Attribute store directory - 
        String ffdirname = MetaData.getProperty(MetaData.FLATFILE_ATTRIBUTE_STORE);
        String ffgroupfilename = ffdirname+"/attrgroups.txt";
        String ffmembersdir = ffdirname+"/groupmembers/";

        // Read thru groups to find parent UUID matching parent category parent name
        // attrgroups.txt - contains parents (format : UUID|category|name|description )
        BufferedReader rdr = null;
        String ids = null;
        String grpcat = null;
        String grpname = null;
        try {
            rdr = new BufferedReader(new FileReader(ffgroupfilename));
            String l = null;
            while ((l = rdr.readLine()) != null) {
                StringTokenizer tok = new StringTokenizer(l, "|");
                ids = null;
                grpcat = null;
                grpname = null;
                if (tok.hasMoreTokens())
                    ids = tok.nextToken();
                if (tok.hasMoreTokens())
                    grpcat = tok.nextToken();
                if (tok.hasMoreTokens())
                    grpname = tok.nextToken();
                if (parentcategory.equals(grpcat) && parentname.equals(grpname)) {
                    break;
                }
            }
        } catch (Exception ex) {
            Debug.error("FlatFileAssertionEvaluator", "checkMembership failed to read groups file.", ex);
            return false;
        } finally {
            if (rdr != null) 
                try {
                    rdr.close();
                } catch(Exception ex) {}
        }   

        if (ids == null) {
            // Not found! - assertion uses non-existant group. TODO : implement bad policy support.
            Debug.error("FlatFileAssertionEvaluator", "checkMembership:Group not found."+parentcategory+":"+parentname);
            return false;
        }
        // Iterate group members and see if contain  member
        // Open the group membership file in groupmembers directory. 
        // Filename : <UUID>.txt (format : membername|description)
        String ffmembersfile = ffmembersdir+ids+".txt";
        try {
            rdr = new BufferedReader(new FileReader(ffmembersfile));
            String l = null;
            while ((l = rdr.readLine()) != null) {
                StringTokenizer tok = new StringTokenizer(l, "|");
                String membername = null;
                if (tok.hasMoreTokens()) {
                    membername = tok.nextToken();
                    if (membername.equals(member)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            Debug.error("FlatFileAssertionEvaluator", "checkMembership failed to read members file.", ex);
            return false;
        } finally {
            if (rdr != null) 
                try {
                    rdr.close();
                } catch(Exception ex) {}
        }   
        return false;
    }
}
