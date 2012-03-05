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


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.Advice;
import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.PolicyException;
import org.ebayopensource.aegis.Rule;
import org.ebayopensource.aegis.Target;
import org.ebayopensource.aegis.md.MetaData;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyDecisionPoint;
import org.ebayopensource.aegis.PolicyStore;
import org.ebayopensource.aegis.debug.Debug;
import org.ebayopensource.aegis.plugin.AssertionEvaluator;
import org.ebayopensource.aegis.plugin.TargetEvaluator;
import org.ebayopensource.aegis.plugin.RuleEvaluator;

/**
  * Embedded PDP implementation
  */
public class EmbeddedPolicyDecisionPoint implements PolicyDecisionPoint
{
    
    static private volatile List<Policy> s_policyCache = null;
    //static private volatile MedaData md = null;
    private static final String POLICYSTORE_CLASS_PROP = "PolicyStoreClass";
    Properties m_props = null;
    PolicyStore m_ps = null;

    /**
      * gets a policy <code>Decision</code> for a given input params :
      *  @param subjects <code>List</code> of identities attempting acccess
      *  @param resource being accessed
      *  @param action being performed
      *  @param env context of the request
      *  @return Decision
      */
    public Decision getPolicyDecision(Target target, List<Environment> env) 
    {
        // Get relevant policies
        // TODO :m_ps query policies...
        // For now use in memory cache 
        Debug.message("PolicyEval", "Start getPolicyDecision");
   
        Decision decision = new Decision(Effect.UNKNOWN);
        // For each policy, execute the conditions - accumulate results from each.
        for (Policy policy : s_policyCache) {
            Debug.message("PolicyEval", "getPolicyDecision:check for policy : "+policy);
            
            try {
                if (!targetMatches(target, policy.getTargets(), env))
                    continue;
                // Check Rules and collect applicable Obligations and Advices
                int effect = policy.getEffect().get();
                Decision conditionDecision = ruleMatches(policy.getRules(), env, (effect == Effect.PERMIT));
                
                // Perform conflict resolution Phase 1
                MetaData.getConflictResolver().resolve(policy, decision, conditionDecision);
                
                Debug.message("PolicyEval", "Found matching policy:"+policy+" effect="+effect);
            } catch(Exception ex) {
                // TODO simply log error and continue for now..
                Debug.error("PolicyEval", "getPolicyDecision:Exception=",ex);
                continue;
            }
        }
        // Perform conflict 2 resolution here
        MetaData.getConflictResolver().resolveFinal(decision);
    
        // Construct final Decision
        return decision;
    }

    private boolean targetMatches(Target reqtarget, List<Target> ptargets, List<Environment> env)
    {
        boolean match = false;
        Debug.message("PolicyEval", "targetMatches: start: "+reqtarget);
        if (ptargets == null || ptargets.size() == 0)
            match =  true; // Empty is same as ANY target
        if (!match) {
            // Get Eval class..
            TargetEvaluator reval = MetaData.getTargetEvaluator(reqtarget.getType());
            for (Target pres : ptargets) {
                Debug.message("PolicyEval", "targetMatches: pres=: "+pres);
                try {
                    if (reval.evaluate(reqtarget, pres, env)) {
                       match = true;
                       break;
                    }
                } catch(Exception ex) {
                    // TODO simply ignore, log error and continue for now..
                    Debug.error("PolicyEval", "getPolicyDecision:targetMatches=",ex);
                    continue;
                }
            }
        } 
        Debug.message("PolicyEval", "targetMatches: returning: "+match);
        return match;
    }
    // TODO : process collect advices - advice collection for PERMIT policies only
    private Decision ruleMatches(Expression<Rule> prules, List<Environment> env, boolean collectadvices)
    {
     
        Debug.message("PolicyEval", "ruleMatches: start: "+prules);
        boolean finalmatch = false;
        Decision finaldecision = new Decision(Decision.RULE_INIT);
        boolean match = false;
        if (prules == null || prules.getMembers() == null || 
            prules.getMembers().size() == 0) {
            finalmatch = match =  true; // Empty is same as no rules
            finaldecision.setType(Decision.RULE_MATCH);
        }

        int combiner = Expression.ALL_OF;
        if (! match) {
            ArrayList<Object>  policymembers = prules.getMembers();
            combiner = prules.getType(); //ANY_OF, ALL_OF
        
            for  (Object obj : policymembers) {
                Rule rule = (Rule) obj;
                Debug.message("PolicyEval", "ruleMatches: pol member: "+rule);
                Decision d = null;
                // Get Eval class..
                RuleEvaluator seval = MetaData.getRuleEvaluator(rule.getCategory());
                if (seval != null) {
                    try {
                        d = seval.evaluate(rule, env);
                        match = (d.getType() == Decision.RULE_MATCH);
                            Debug.message("PolicyEval", "ruleMatches: ruleeval: "+rule+ " match="+match);
                    } catch(Exception ex) {
                        // TODO simply ignore, log error and continue for now..
                        Debug.error("PolicyEval", "getPolicyDecision:ruleMatches=",ex);
                        continue;
                    }
                } else {
                    // Iterate thru Assertions
                    Expression<Assertion> assertions = rule.getExpression();
                    int acombiner = assertions.getType(); //ANY_OF, ALL_OF
                    ArrayList<Object>  assmembers = assertions.getMembers();
                    boolean afinalmatch = false;
                    Decision afinaldecision = new Decision(Decision.RULE_INIT);
                    boolean amatch = false;
                    Decision adecision = new Decision(Decision.RULE_INIT);
                    for  (Object aobj : assmembers) {
                        Assertion assertion = (Assertion) aobj;
                        Decision da = null;
                        AssertionEvaluator aeval = MetaData.getAssertionEvaluator(assertion.getCategory());
                        if (aeval != null) {
                            try {
                                da = aeval.evaluate(assertion, env);
                                amatch = (da.getType() == Decision.RULE_MATCH);
                                Debug.message("PolicyEval", "ruleMatches: asserteval: "+rule+ " amatch="+amatch);
                                // Check combiner rules..
                                afinalmatch = processDecision( da, amatch, acombiner, afinaldecision, afinalmatch); 
                            } catch(Exception ex) {
                                // TODO simply ignore, log error and continue for now..
                                Debug.error("PolicyEval", "getPolicyDecision:ruleMatches=",ex);
                                continue;
                            }
                        }
                    }
                    d = afinaldecision;
                    match = afinalmatch;
                }
                // Check combiner rules..
                finalmatch = processDecision( d, match, combiner, finaldecision, finalmatch) ;

            }
        }
        return finaldecision;
    }

    private boolean processDecision(Decision d, boolean match, int combiner,
                                    Decision finaldecision, boolean finalmatch) 
    {
        Debug.message("EmbeddedPolicyDecision", "process:START:"+match);
        // Check combiner rules..
        if (combiner == Expression.ALL_OF && match == false)
            finalmatch= false; // We are done here
        if (combiner == Expression.ANY_OF && match == true) {
            finalmatch = true; // We are done here
            finaldecision.setType(Decision.RULE_MATCH);
            finaldecision.resetAdvices();
        }
        if (combiner == Expression.ALL_OF && match == true && 
            finaldecision.getType() == Decision.RULE_INIT) {
            finalmatch = true;
            finaldecision.setType(Decision.RULE_MATCH);
        }
        // aggregate advices and obligations if we are currently heading towards a non-match so far
        if (match == false && finalmatch == false) {
            finaldecision.setType(Decision.RULE_NOMATCH);
            List<Advice> curradvices = d.getAdvices();
            if (curradvices != null){
                for (Advice cadvice : curradvices) {
                    finaldecision.addAdvice(cadvice);
                }
            }
        }
        Debug.message("EmbeddedPolicyDecision", "process:"+finalmatch);
        return finalmatch;
    }

    public void initialize(Properties props) throws Exception
    {
        m_props = props;
        // setup PolicyStore
        String pcl = m_props.getProperty(POLICYSTORE_CLASS_PROP);
        if (pcl == null)
            throw new PolicyException("POLICY_STORE_NOTFOUND",
                                  "PolicyStore not found");
        m_ps  = (PolicyStore) Class.forName(pcl).newInstance();
        m_ps.initialize(props);
        // Cache all policies in memory
        s_policyCache = m_ps.getAllPolicies();
    }
}
