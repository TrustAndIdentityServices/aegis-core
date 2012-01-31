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

import org.ebayopensource.aegis.Action;
import org.ebayopensource.aegis.Condition;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyStore;
import org.ebayopensource.aegis.Resource;
import org.ebayopensource.aegis.Subject;
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
            String str = policy.getString("name");
            str = policy.getString("effect");
            Effect effect = new Effect(Integer.parseInt(str));
            JSONObject j_subjects = policy.getJSONObject("Subjects");
            Expression<Subject> subjects = new Expression<Subject>();

            String oper = null;
            int operint = 0;
            if (j_subjects.has("ANY_OF")) {
                oper = "ANY_OF";
                operint = subjects.ANY_OF;
            } else if (j_subjects.has("ALL_OF")) {
                oper = "ALL_OF";
                operint = subjects.ALL_OF;
            } else {
                Debug.error("JSONPolicy", "parsePolicy:invalid subj expr:"+pol1);
                return null;
            }
            subjects.setType(operint);
           
            JSONArray arrayob = j_subjects.getJSONArray(oper);
            
            int cnt = arrayob.length();
            for (int i = 0; i < cnt; i++) {
                ob = arrayob.getJSONObject(0);
                JSONObject subob = ob.getJSONObject("Subject");
       
                Subject sub1 = new Subject(subob.getString("type"), subob.getString("name"));
                subjects.add(sub1);
    
            }
            JSONArray j_resources = policy.getJSONArray("Resources");
            ob = j_resources.getJSONObject(0);
            JSONObject resob = ob.getJSONObject("Resource");
            List<Resource> resources = new ArrayList<Resource>();
            Resource res1 = new Resource(resob.getString("type"),resob.getString("name") );
            resources.add(res1);

            JSONArray j_actions = policy.getJSONArray("Actions");
            ob = j_actions.getJSONObject(0);
            JSONObject actob = ob.getJSONObject("Action");
            List<Action> actions = new ArrayList<Action>();
            Action ac1 = new Action(actob.getString("type"),actob.getString("name") );
            actions.add(ac1);

            JSONObject j_conditions = policy.getJSONObject("Conditions");
            oper = null;
            if (j_conditions.has("ANY_OF")) {
                oper = "ANY_OF";
                operint = Expression.ANY_OF;
            } else if (j_conditions.has("ALL_OF")) {
                oper = "ALL_OF";
                operint = Expression.ALL_OF;
            } else {
                Debug.error("JSONPolicy", "parsePolicy:invalid cond expr:"+pol1);
                return null;
            }
            arrayob = j_conditions.getJSONArray(oper);
            cnt = arrayob.length();
            Expression<Condition> conditions = new Expression<Condition>();
            conditions.setType(operint);
            for (int j = 0; j < cnt; j++) {
                ob = arrayob.getJSONObject(j);
                JSONObject condob = ob.getJSONObject("Condition");
                String ctype = condob.getString("type");
                String cname = condob.getString("name");
                Condition cond1 = new Condition(ctype, cname);
                JSONArray exprs = condob.getJSONArray("expr");

                int totexprs = exprs.length();
                Debug.message("JSONPolicy", "Parse: tot="+totexprs+":"+exprs);
                for (int i = 0; i < totexprs; i++) {
                    JSONObject expr = exprs.getJSONObject(i);
                    String op = expr.getString("op");
                    int opi = 0;
                    for (String opstr : Condition.ops ) {
                        if (opstr.equals(op)) 
                             break;
                        opi++;
                    }
                    cond1.addExpr(expr.getString("name"), opi, expr.get("value"));

                }
                conditions.add(cond1);
            }
            pol1 = new Policy(subjects, resources, actions, effect, conditions);


        } catch (Exception ex) {
            Debug.error("JSONDataStore", "readPolicies: Exception:",ex);
        }
        return pol1;
    }
}
