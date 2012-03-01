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

import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyStore;
import org.ebayopensource.aegis.Rule;
import org.ebayopensource.aegis.Target;
import org.ebayopensource.aegis.debug.Debug;

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
        ArrayList<Target> targets = new ArrayList<Target>();
        Target target1 = new Target("web", "http://www.ebay.com/xxx");
        Target target2 = new Target("webcmd", "addItem");
        targets.add(target1);
        targets.add(target2);

        Expression<Rule> rules = new Expression<Rule>();
        rules.setType(Expression.ALL_OF);


        Expression<Assertion> exp1 = new Expression<Assertion>();
        exp1.setType(Expression.ALL_OF);
        Assertion a1 = new Assertion("authn", "assertion1");
        a1.setCExpr( "authn.level", Assertion.OP_GT,  new Integer(2));

        exp1.add(a1);

        Rule rule1 = new Rule("category1", "rule1", exp1);
        rules.add(rule1);

        rules.add(rule1);

        Effect effect = new Effect(Effect.PERMIT);
        Policy pol = new Policy("testPolicy", "TEST Policy", targets, rules, effect);

        List<Policy> policies = new ArrayList<Policy>();
        policies.add(pol);
        Debug.message("MockOnePolicyStore", "policy="+pol);

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
