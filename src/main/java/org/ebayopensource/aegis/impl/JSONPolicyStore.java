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
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyException;
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
    private Properties m_props = null;

    public void initialize(Properties props)
    {
        m_props = props;
    }

    public Policy getPolicy(String id) 
    {
        List<Policy> plist =  getAllPolicies();
        for (Policy p : plist) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    public List<Policy> getAllPolicies()
    {
        List<Policy> policies = new ArrayList<Policy>();
        BufferedReader rdr = null;
        try {
            rdr = getReader();
            String polstr = null;
            
            int i = 0;
            while ((polstr = rdr.readLine()) != null) {
            	if (polstr.startsWith("#"))
            		continue;
                Policy pol1 = parsePolicy(polstr);
                policies.add(pol1);
            }
        } catch (PolicyException pe) {
            Debug.error("JSONPolicyStrore", "getAllPolicies failed : ex=",pe);
            throw pe;
        } catch (Exception ex) {
            PolicyException pe = new PolicyException("POLICY_DATASTORE_EXCEPTION");
            pe.initCause(ex);
            throw pe;
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
        // Add a new GUID
        policy.setId(java.util.UUID.randomUUID().toString());
        String jsonStr = policy.toString();
        String location = m_props.getProperty(PolicyStore.PolicyStoreLocation);
        // Append the policy
        PrintStream p = getWriter(true);
        p.println(policy.toString());
        p.close();
    }
    public void updatePolicy(Policy policy)
    {
        List<Policy> plist =  getAllPolicies();
        PrintStream ps = getWriter(false);
        boolean idfound = false;
        for (Policy p : plist) {
            if (policy.getId().equals(p.getId())) {
                ps.println(policy.toString());
                idfound = true;
            } else {
                ps.println(p.toString());
            }
        }
        ps.close();
        if (!idfound) {
            throw(new PolicyException("POLICY_NOT_FOUND_ERROR", policy.getId()));
        }
    }
    public void deletePolicy(Policy policy)
    {
        deletePolicyById(policy.getId());
    }
    public void deletePolicyById(String id)
    {
        List<Policy> plist =  getAllPolicies();
        PrintStream ps = getWriter(false);
        boolean idfound = false;
        for (Policy p : plist) {
            if (id.equals(p.getId())) {
                idfound = true;
                continue;
            } else {
                ps.println(p.toString());
            }
        }
        ps.close();
        if (!idfound) {
            throw(new PolicyException("POLICY_NOT_FOUND_ERROR", id));
        }
    }
    public static Policy parsePolicy(String pol)
    {
        Policy pol1 = null;
        Debug.message("JSONPolicy", "parsePolicy:Start Parsing po="+pol);
        try {
            JSONObject ob = new JSONObject(pol);
            JSONObject policy = ob.getJSONObject("Policy");
            Debug.message("JSONPolicyStore", "parsePolicy: initial policy jsonobject="+policy); 
            String uuid = null;
            try {
                uuid = policy.getString("id");
            } catch(Exception ex) {}
            boolean silent = false;
            boolean active = true;
            String version = "0";
            try {
                silent = policy.getBoolean("silent");
            } catch(Exception ex) {}
            try {
                active = policy.getBoolean("active");
            } catch(Exception ex) {}
            try {
                version = policy.getString("version");
            } catch(Exception ex) {}

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
                    Object val = j_assertion.get(2);
                    Debug.message("JSONPolicyStore", "parsePolicy:found valtype="+val.getClass());
                    Assertion a = new Assertion(category, "");
                    a.setCExpr(id, op,  val);
                    assertions.add(a);
                }
                Rule rule = new Rule(category, "", assertions);
                rules.add(rule);
            }

            // Retrieve obligations
            List obligations = null;
            try {
                JSONArray j_obligations = policy.getJSONArray("obligations");
                for (int i = 0; i < j_obligations.length() ; i++) {
                    String obl = j_obligations.getString(i);
                    if (obligations == null)
                        obligations = new ArrayList<String>();
                    obligations.add(obl);
                }
            } catch (Exception ex) {}

            pol1 = new Policy(name, desc, targets, rules, effect);
            if (obligations != null) {
                pol1.setObligations(obligations);
            }
            // Set state 
            pol1.setActive(active);
            // Set silent mode if present
            pol1.setSilent(silent);
            if (uuid == null) {
                // Generate one if one does not exist
                uuid = java.util.UUID.randomUUID().toString();
            }
            pol1.setId(uuid);  
            Debug.message("JSONPolicyStore", "parsePolicy:final result : "+pol1.toString());
        } catch (Exception ex) {
            Debug.error("JSONDataStore", "parsePolicy: Exception:",ex);
        }
        return pol1;
    }
    private BufferedReader getReader()
    {
        String location = m_props.getProperty(PolicyStore.PolicyStoreLocation);
        BufferedReader rdr = null;
        try {
            if (location.startsWith("classpath:")) {
                String cl = location.substring(10, location.length());
                Debug.message("JSONPolicyStrore", "getReader: cl="+cl);
                 
                Debug.message("JSONPolicyStrore", "getReader: res="+
                      ClassLoader.getSystemResourceAsStream(cl));
                rdr = new BufferedReader(
                      new InputStreamReader(
                      ClassLoader.getSystemResourceAsStream(cl)));
            } else {
                URL url = new URL(location); 
                rdr = new BufferedReader(
                      new InputStreamReader(url.openStream()));
            }
        
        } catch(Exception ex) {
            Debug.error("JSONPolicyStrore", "getReader failed : ex=",ex);
            PolicyException pe = 
                   new PolicyException("POLICY_DATASTORE_URLERROR", location);
            pe.initCause(ex);
            throw pe;
        }
        return rdr;
    }
    private PrintStream getWriter(boolean append)
    {
        String location = m_props.getProperty(PolicyStore.PolicyStoreLocation);
        PrintStream p = null;
        try {
            if (location.startsWith("file://")) {
                String cl = location.substring(7, location.length());
                Debug.message("JSONPolicyStrore", "getWriter: cl="+cl);
                p = new PrintStream(
                      new PrintStream(new FileOutputStream(cl, append)));
            } else {
                PolicyException pe = 
                   new PolicyException("POLICY_DATASTORE_URLERROR", location);
                throw pe;
            } 
        
        } catch(Exception ex) {
            Debug.error("JSONPolicyStrore", "getWriter failed : ex=",ex);
            PolicyException pe = 
                   new PolicyException("POLICY_DATASTORE_URLERROR", location);
            pe.initCause(ex);
            throw pe;
        }
        return p;
    }
}
