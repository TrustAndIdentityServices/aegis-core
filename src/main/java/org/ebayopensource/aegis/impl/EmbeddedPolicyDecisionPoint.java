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
import org.ebayopensource.aegis.Context;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.Obligation;
import org.ebayopensource.aegis.PolicyException;
import org.ebayopensource.aegis.Rule;
import org.ebayopensource.aegis.Target;
import org.ebayopensource.aegis.md.MetaData;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyDecisionPoint;
import org.ebayopensource.aegis.PolicyStore;
import org.ebayopensource.aegis.debug.Debug;
import org.ebayopensource.aegis.plugin.AssertionEvaluator;
import org.ebayopensource.aegis.plugin.AuditLogger;
import org.ebayopensource.aegis.plugin.TargetEvaluator;
import org.ebayopensource.aegis.plugin.RuleEvaluator;

/**
  * Embedded PDP implementation
  */
public class EmbeddedPolicyDecisionPoint implements PolicyDecisionPoint
{
    
    public static String AUDIT_LOG_CLASS_PARAM          = "AUDIT_LOG_CLASS";
    public static String DEFAULT_AUDIT_LOG_CLASS        = "org.ebayopensource.aegis.impl.FileAuditLogger";
    public static String AUDIT_POLICY                   = "POLICYEVAL";
    public static String AUDIT_POLICY_FINAL                   = "POLICYEVAL_FINAL";
    public static String AUDIT_POLICY_NOMATCH           = "POLICY_NOMATCH";
    public static String AUDIT_POLICY_PERMIT            = "POLICY_PERMIT";
    public static String AUDIT_POLICY_DENY              = "POLICY_DENY";
    public static String AUDIT_POLICYEVAL_SILENT_PERMIT = "POLICY_NOMATCH";
    public static String AUDIT_FINALDECISION_PERMIT     = "POLICY_PERMIT";
    public static String AUDIT_FINALDECISION_DENY       = "POLICY_DENY";

    public static String PERMIT_IF_NO_TARGETS_MATCH_PARAM = "PERMIT_IF_NO_TARGETS_MATCH";

    private volatile List<Policy> m_policyCache = null;
    private static final String POLICYSTORE_CLASS_PROP = "PolicyStoreClass";
    private Properties m_props = null;
    private PolicyStore m_ps = null;
    /**
      * gets a policy <code>Decision</code> for a given input params :
      *  @param subjects <code>List</code> of identities attempting acccess
      *  @param resource being accessed
      *  @param action being performed
      *  @param env context of the request
      *  @return Decision
      */
    public Decision getPolicyDecision(Target target, Environment env) 
    {
        ArrayList<Environment> envlist = new ArrayList<Environment>();
        envlist.add(env);
        return getPolicyDecision(target, envlist);
    }

    /**
      * gets a policy <code>Decision</code> for a given input params :
      *  @param subjects <code>List</code> of identities attempting acccess
      *  @param resource being accessed
      *  @param action being performed
      *  @param env list of contexts of the request
      *  @return Decision
      */
    public Decision getPolicyDecision(Target target, List<Environment> env) 
    {
        Context context = null;
        try {
            // Construct the Context object
            context = new Context(m_props, env);
        } catch (Exception ex) {
            Debug.error("PolicyEval", "Context init error:",ex);
            throw new PolicyException("POLICY_CONTEXT_ERROR",
                                  "Context could not be established");
        }
        // Get relevant policies
        // TODO :m_ps query policies...
        // For now use in memory cache 
        Debug.message("PolicyEval", "Start getPolicyDecision");
   
        Decision decision = new Decision(Effect.UNKNOWN);
        Decision decisionIgnoringSilentFlag = null;
        boolean sawSilent = false;
        int targetMatches = 0;
        // For each policy, execute the conditions - accumulate results from each.
        for (Policy policy : m_policyCache) {
            Debug.message("PolicyEval", "getPolicyDecision:check for policy : "+policy);
            
            if (!policy.isActive())
                continue;
            try {
                if (!targetMatches(target, policy.getTargets(), context))
                    continue;
                targetMatches++;
                // Check Rules and collect applicable Obligations and Advices
                int effect = policy.getEffect().get();
                Decision conditionDecision = ruleMatches(policy.getRules(), context, (effect == Effect.PERMIT));
                // Initial "silent" processing :
                // if this policy is in silent mode - "influence" a PERMIT by appropriately tuning the
                // decision state based on whether the policy is a PERMIT or DENY.
                // Create a Audit log for the results of this policy
                String logtype = null;
                if (conditionDecision.getType() == Decision.RULE_NOMATCH) 
                    logtype = AUDIT_POLICY_NOMATCH;
                else if (effect == Effect.PERMIT) 
                    logtype = AUDIT_POLICY_PERMIT;
                else
                    logtype = AUDIT_POLICY_DENY;

                if (policy.isSilent()) {
                    if (effect == Effect.PERMIT && conditionDecision.getType() == Decision.RULE_NOMATCH) {
                        logtype = AUDIT_POLICYEVAL_SILENT_PERMIT;
                        sawSilent = true;
                    }
                    if (effect == Effect.DENY && conditionDecision.getType() == Decision.RULE_MATCH) {
                        logtype = AUDIT_POLICYEVAL_SILENT_PERMIT;
                        sawSilent = true;
                    }
                }
                context.logPolicyEval(AUDIT_POLICY, logtype, target, policy, conditionDecision, null, null, 5);

                if (policy.isSilent()) { 
                    // Perform conflict resolution Phase 1 - for ignoreSilent case
                    if (decisionIgnoringSilentFlag == null)
                        decisionIgnoringSilentFlag = (Decision) decision.deepcopy();
                    context.getMetaData().getConflictResolver().resolve(policy, decisionIgnoringSilentFlag, conditionDecision);
                    if (effect == Effect.PERMIT) {
                        conditionDecision.setType(Decision.RULE_MATCH);
                    } else {
                        conditionDecision.setType(Decision.RULE_NOMATCH);
                    }
                }
                
                // Perform conflict resolution Phase 1
                context.getMetaData().getConflictResolver().resolve(policy, decision, conditionDecision);
                
                Debug.message("PolicyEval", "Found matching policy:"+policy+" effect="+effect);
            } catch(Exception ex) {
                // TODO simply log error and continue for now..
                Debug.error("PolicyEval", "getPolicyDecision:Exception=",ex);
                continue;
            }
        }
        // Perform conflict resolution : phase 2 here
        // If PDP is configured to permit if no targets match at all 
        if (targetMatches == 0 && "true".equals(m_props.getProperty(PERMIT_IF_NO_TARGETS_MATCH_PARAM))) {
            Debug.message("PolicyEval", "getPolicyDecision:NO targets matched & permit if no match is true");
            decision.setType(Decision.EFFECT_PERMIT);
        } else {
            if (decisionIgnoringSilentFlag != null) 
                context.getMetaData().getConflictResolver().resolveFinal(decisionIgnoringSilentFlag);
            context.getMetaData().getConflictResolver().resolveFinal(decision);
        }
        // Create a Audit log for the final result
        String finallogtype = (decision.getType() == Decision.EFFECT_DENY) ? AUDIT_FINALDECISION_DENY : AUDIT_FINALDECISION_PERMIT ;
        Obligation logobligation = null;
        if (context.getLogObligationFlag()) {
            logobligation = new Obligation("LOG");
            decision.addObligation(logobligation);
        }
        context.logPolicyEval(AUDIT_POLICY_FINAL, finallogtype, target, null, decision, decisionIgnoringSilentFlag, "&sawSilent="+sawSilent, 2);
    
        // Construct final Decision
        // Optimization : Add global Obligations LOGRECORD attribute *after* audit log is writen out
        if (logobligation != null) {
             logobligation.setAttribute("LOGRECORD", context.getCompleteLogRecord());
        }
        return decision;
    }

    private boolean targetMatches(Target reqtarget, List<Target> ptargets, Context context)
    {
        boolean match = false;
        Debug.message("PolicyEval", "targetMatches: start: "+reqtarget);
        if (ptargets == null || ptargets.size() == 0)
            match =  true; // Empty is same as ANY target
        if (!match) {
            // Get Eval class..
            //TargetEvaluator reval = context.getMetaData().getTargetEvaluator(reqtarget.getType());
            for (Target pres : ptargets) {
                TargetEvaluator reval = context.getMetaData().getTargetEvaluator(pres.getType());
                reval.initialize(context);
                Debug.message("PolicyEval", "targetMatches: pres=: "+pres);
                try {
                    if (reval.evaluate(reqtarget, pres, context)) {
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
    private Decision ruleMatches(Expression<Rule> prules, Context context, boolean collectadvices)
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
                RuleEvaluator seval = context.getMetaData().getRuleEvaluator(rule.getCategory());
                if (seval != null) {
                    try {
                        d = seval.evaluate(rule, context);
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
                        AssertionEvaluator aeval = context.getMetaData().getAssertionEvaluator(assertion.getCExpr().id_);
                        aeval.initialize(context);
                        if (aeval != null) {
                            try {
                                da = aeval.evaluate(assertion, context);
                                Debug.message("PolicyEval", "ruleMatches: asserteval: "+rule+ " amatch="+amatch);
                            } catch(Exception ex) {
                                Debug.error("PolicyEval", "getPolicyDecision:ruleMatches=",ex);
                                da = new Decision(Decision.RULE_NOMATCH);
                            }
                            // Check combiner rules..
                            amatch = (da.getType() == Decision.RULE_MATCH);
                            afinalmatch = processDecision( da, amatch, acombiner, afinaldecision, afinalmatch); 
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
        Debug.message("EmbeddedPolicyDecision", "processDecision:start:"+match);
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

        // Setup Debug  
        Debug.initialize(props);
        // Create initial context - to initialize MetaData, Logging
        Context ctx = new Context(props, null);
        // setup PolicyStore
        String pcl = m_props.getProperty(POLICYSTORE_CLASS_PROP);
        if (pcl == null)
            throw new PolicyException("POLICY_STORE_NOTFOUND",
                                  "PolicyStore not found");
        m_ps  = (PolicyStore) Class.forName(pcl).newInstance();
        m_ps.initialize(props);
        // Cache all policies in memory
        m_policyCache = m_ps.getAllPolicies();
    }
}
