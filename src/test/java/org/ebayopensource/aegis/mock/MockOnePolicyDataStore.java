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
package org.ebayopensource.aegis.mock;

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

/**
  * Mock for a single policy :
  * <code>
  * Policy :
  *  {
  *     Subjects : { ANY_OF { Subject : {  type:"role", name : "manager"} } },
  *     Resources : { Resource : {  type:"web", name : "http://www.ebay.com/xxx"}},
  *     Actions : { Action : {  type:"webcmd", name : "addItem"}},
  *     Conditions : { ANY_OF { type : authn, name : authncondition1, ( authn.level > 2 ) AND ( authn.idp = EBAY )}},
  *     effect=PERMIT
  *  }
  * </code>
  */
public class MockOnePolicyDataStore implements PolicyStore
{
    public Policy getPolicy(String id) 
    {
        return null;
    }
    public List<Policy> getAllPolicies()
    {
        Expression<Subject> subjects = new Expression<Subject>();
        Subject sub1 = new Subject("role", "manager");
        subjects.add(sub1);

        List<Resource> resources = new ArrayList<Resource>();
        Resource res1 = new Resource("web","http://www.ebay.com/xxx" );
        resources.add(res1);

        List<Action> actions = new ArrayList<Action>();
        Action action1 = new Action("webcmd", "addItem");
        actions.add(action1);

        Effect effect = new Effect(Effect.PERMIT);

        Expression<Condition> conditions = new Expression<Condition>();
        Condition cond1 = new Condition("authn", "authncondition1");
        cond1.addExpr("authn.level", Condition.OP_GT,  new Integer(2));
        cond1.addExpr("authn.idp", Condition.OP_EQ,  "EBAY");

        conditions.add(cond1);

        Policy pol1 = new Policy(subjects, resources, actions, effect, conditions);

        List<Policy> policies = new ArrayList<Policy>();
        policies.add(pol1);
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
}
