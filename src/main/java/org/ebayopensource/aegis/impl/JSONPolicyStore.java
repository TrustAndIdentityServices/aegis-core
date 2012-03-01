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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyStore;
import org.ebayopensource.aegis.Rule;
import org.ebayopensource.aegis.Target;
import org.ebayopensource.aegis.debug.Debug;
import org.json.JSONArray;
import org.json.JSONObject;

/**
  * JSON implementation for policy storage.
  */
public class JSONPolicyStore implements PolicyStore
{
    private static String DEFAULT_FILE = "samples/policies.json";
    public Policy getPolicy(String id) 
    {
        return null;
    }
    public List<Policy> getAllPolicies()
    {
        String policyfile = DEFAULT_FILE;
        List<Policy> policies = new ArrayList<Policy>();
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(
                      new InputStreamReader(ClassLoader.getSystemResourceAsStream("policies.json")));
            String polstr = null;
            while ((polstr = rdr.readLine()) != null) {
            	if (polstr.startsWith("#"))
            		continue;
                Policy pol1 = parsePolicy(polstr);
                policies.add(pol1);
            }
        } catch (Exception ex) {
            Debug.error("JSONPolicyStrore", "getAllPolicies failed : ex=",ex);
            return null;
        } finally {
            if (rdr != null) {
                try {
                    rdr.close();
                } catch(Exception ex) {}
            }
        }
        return policies;
    }
    public void createPolicy(Policy policy)
    {
    }
    public void updatePolicy(Policy policy)
    {
    }
    public void deletePolicy(Policy policy)
    {
    }
    private Policy parsePolicy(String pol)
    {
        Policy pol1 = null;
        Debug.message("JSONPolicy", "Parsing po="+pol);
        try {
            JSONObject ob = new JSONObject(pol);
            JSONObject policy = ob.getJSONObject("Policy");
            Debug.message("JSONPolicyStore", "parsePolicy:policy="+policy); 
            String name = policy.getString("name");
            String desc = policy.getString("description");
            String str = policy.getString("effect");
            Debug.message("JSONPolicyStore", "parsePolicy:effect="+str); 
            Effect effect = new Effect(str);
           
            JSONArray j_targets = policy.getJSONArray("target");
            List<Target> targets = null;
            for (int i = 0; i < j_targets.length(); i++) {
                JSONArray arr = j_targets.getJSONArray(i);
                String ttype = arr.getString(0);
                String tval = arr.getString(1);;
                if (targets == null) {
                    targets = new ArrayList<Target>();
                }
                Target res1 = new Target(ttype, tval );
                targets.add(res1);
            }

            JSONArray j_rules = policy.getJSONArray("rules");

            Expression<Rule> rules = new Expression<Rule>();

            for (int i = 0; i < j_rules.length() ; i++) {
                JSONObject j_rule = j_rules.getJSONObject(i);
                String category = j_rule.getString("category");
                String oper = null;
                int operint = 0; // Default 0 okay?
                if (j_rule.has("ANY_OF")) {
                    oper = "ANY_OF";
                    operint = Expression.ANY_OF;
                } else if (j_rule.has("ALL_OF")) {
                    oper = "ALL_OF";
                    operint = Expression.ALL_OF;
                }
                JSONArray arrayob = j_rule.getJSONArray(oper);
                int cnt = arrayob.length();
                Expression<Assertion> assertions = new Expression<Assertion>();
                assertions.setType(operint);
                for (int j = 0; j < cnt; j++) {
                    JSONArray j_assertion = arrayob.getJSONArray(j);
                    String id = j_assertion.getString(0);
                    String op = j_assertion.getString(1);
                    String val = j_assertion.getString(2);
                    Assertion a = new Assertion(category, "");
                    a.setCExpr(id, op,  val);
                    assertions.add(a);
                }
                Rule rule = new Rule(category, "", assertions);
                rules.add(rule);
            }
            pol1 = new Policy(name, desc, targets, rules, effect);

        } catch (Exception ex) {
            Debug.error("JSONDataStore", "readPolicies: Exception:",ex);
        }
        Debug.message("JSONPolicyStore", "parsePolicy() : "+pol1.toString());
        return pol1;
    }
}
