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

import org.ebayopensource.aegis.Action;
import org.ebayopensource.aegis.Advice;
import org.ebayopensource.aegis.Condition;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.MetaData;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyDecisionPoint;
import org.ebayopensource.aegis.PolicyStore;
import org.ebayopensource.aegis.Resource;
import org.ebayopensource.aegis.Subject;
import org.ebayopensource.aegis.debug.Debug;
import org.ebayopensource.aegis.plugin.ActionEvaluator;
import org.ebayopensource.aegis.plugin.ConditionEvaluator;
import org.ebayopensource.aegis.plugin.ResourceEvaluator;
import org.ebayopensource.aegis.plugin.SubjectEvaluator;

/**
  * Embedded PDP implementation
  */
public class EmbeddedPolicyDecisionPoint implements PolicyDecisionPoint
{
    
    static private volatile List<Policy> policyCache = null;
    //static private volatile MedaData md = null;
    private static final String POLICYSTORE_CLASS_PROP = "PolicyStoreClass";
    private static final String DEFAULT_POLICYSTORE_CLASS = "org.ebayopensource.aegis.impl.JSONPolicyStore";

    /**
      * gets a policy <code>Decision</code> for a given input params :
      *  @param subjects <code>List</code> of identities attempting acccess
      *  @param resource being accessed
      *  @param action being performed
      *  @param env context of the request
      *  @return Decision
      */
    public Decision getPolicyDecision(List<Subject> subjects, Resource resource, Action action, List<Environment> env) 
    {
        // Get relevant policies
        // TODO :_ps query policies...
        // For now use in memory cache 
        Debug.message("PolicyEval", "Start getPolicyDecision");
   
        Decision decision = new Decision(Effect.UNKNOWN);
        // For each policy, execute the conditions - accumulate results from each.
        for (Policy policy : policyCache) {
            Debug.message("PolicyEval", "getPolicyDecision:check for policy : "+policy);
            
            try {
                // Check for subject match
                if (!subjectMatches(subjects, policy.getSubjects(), env))
                    continue;
                // Check for resource match
                if (!resourceMatches(resource, policy.getResources(), env))
                    continue;
                // Check for action match
                if (!actionMatches(action, policy.getActions(), env))
                    continue;
                // Check Conditions and collect applicable Obligations and Advices
                int effect = policy.getEffect().get();
                Decision conditionDecision = conditionMatches(policy.getConditions(), env, (effect == Effect.PERMIT));
                
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
    private boolean subjectMatches(List<Subject> reqsubjects, Expression<Subject> psubs, List<Environment> env)
    {
        boolean match = false;
        if (psubs == null || psubs.getMembers() == null || psubs.getMembers().size() == 0)
            match =  true; // Empty is same as ANY subject)

        int combiner = Expression.ANY_OF;
        if (! match) {
            ArrayList<Object>  policymembers = psubs.getMembers();
            combiner = psubs.getType(); //ANY_OF, ALL_OF, NOT 
        
            for  (Object ps : policymembers) {
                Debug.message("PolicyEval", "subjectMatches: pol member: "+ps);
                if (ps instanceof Subject) {
                    Subject polSubject = (Subject) ps;
                    // Get Eval class..
                    SubjectEvaluator seval = MetaData.getSubjectEvaluator(polSubject.getType());
                    for (Subject subject : reqsubjects) {
                        Debug.message("PolicyEval", "subjectMatches: trying req subject : "+subject);
                        String type = subject.getType();
                        //if (type.equals(polSubject.getType())) { // TODO move type checkling to evaluator
                            try {
                                match = seval.evaluate(subject, polSubject, env);
                            } catch(Exception ex) {
                                // TODO simply ignore, log error and continue for now..
                                Debug.error("PolicyEval", "getPolicyDecision:subjectMatches=",ex);
                                continue;
                            }
                        //}
                        if (match) // Single match is enough 
                            break;
                    }
                } else {
                    Expression<Subject> polsubjectExpr = (Expression<Subject>) ps;
                    match = subjectMatches(reqsubjects, polsubjectExpr, env);
                }
                // Check combiner rules..
                if (combiner == Expression.ALL_OF && match == false)
                    return false; // We are done here
                if (combiner == Expression.ANY_OF && match == true)
                    return true; // We are done here
            }
        }
        if (combiner == Expression.NOT)
            return !match;
        else
            return match;
    }
    private boolean resourceMatches(Resource reqresource, List<Resource> presources, List<Environment> env)
    {
        boolean match = false;
        Debug.message("PolicyEval", "resourceMatches: start: "+reqresource);
        if (presources == null || presources.size() == 0)
            match =  true; // Empty is same as ANY resource
        if (!match) {
            // Get Eval class..
            ResourceEvaluator reval = MetaData.getResourceEvaluator(reqresource.getType());
            for (Resource pres : presources) {
                Debug.message("PolicyEval", "resourceMatches: pres=: "+pres);
                try {
                    if (reval.evaluate(reqresource, pres, env)) {
                       match = true;
                       break;
                    }
                } catch(Exception ex) {
                    // TODO simply ignore, log error and continue for now..
                    Debug.error("PolicyEval", "getPolicyDecision:resourcetMatches=",ex);
                    continue;
                }
            }
        } 
        Debug.message("PolicyEval", "resourceMatches: returning: "+match);
        return match;
    }
    private boolean actionMatches(Action reqaction, List<Action> pactions, List<Environment> env)
    {
        boolean match = false;
        if (pactions == null || pactions.size() == 0)
            match =  true; // Empty is same as ANY action
        if (!match) {
            // Get Eval class..
            ActionEvaluator aeval = MetaData.getActionEvaluator(reqaction.getType());
            for (Action pact : pactions) {
                try {
                    if (aeval.evaluate(reqaction, pact, env)) {
                       match = true;
                       break;
                    }
                } catch(Exception ex) {
                    // TODO simply ignore, log error and continue for now..
                    Debug.error("PolicyEval", "getPolicyDecision:actiontMatches=",ex);
                    continue;
                }
            }

        } 
        return match;
    }

    // TODO : process collect advices - advice collection for PERMIT policies only
    private Decision conditionMatches(Expression<Condition> pconds, List<Environment> env, boolean collectadvices)
    {
     
        Debug.message("PolicyEval", "conditionMatches: start: "+pconds);
        boolean finalmatch = false;
        Decision finaldecision = new Decision(Decision.CONDITION_NOMATCH);
        boolean match = false;
        if (pconds == null || pconds.getMembers() == null || 
            pconds.getMembers().size() == 0) {
            finalmatch = match =  true; // Empty is same as no conditions
            finaldecision.setType(Decision.CONDITION_MATCH);
        }

        int combiner = Expression.ANY_OF;
        if (! match) {
            ArrayList<Object>  policymembers = pconds.getMembers();
            combiner = pconds.getType(); //ANY_OF, ALL_OF, NOT 
        
            for  (Object ps : policymembers) {
                Debug.message("PolicyEval", "conditionMatches: pol member: "+ps);
                Decision d = null;
                if (ps instanceof Condition) {
                    Condition polcondition = (Condition) ps;
                    // Get Eval class..
                    ConditionEvaluator seval = MetaData.getConditionEvaluator(polcondition.getType());
                    try {
                        d = seval.evaluate(polcondition, env);
                        match = (d.getType() == Decision.CONDITION_MATCH);
                        Debug.message("PolicyEval", "conditionMatches: condeval: "+polcondition+ " match="+match);
                    } catch(Exception ex) {
                        // TODO simply ignore, log error and continue for now..
                        Debug.error("PolicyEval", "getPolicyDecision:conditionMatches=",ex);
                        continue;
                    }
                } else {
                    Expression<Condition> polconditionExpr = (Expression<Condition>) ps;
                    d = conditionMatches(polconditionExpr, env, collectadvices);
                    match = (d.getType() == Decision.CONDITION_MATCH);
                    
                }

                // Check combiner rules..
                if (combiner == Expression.ALL_OF && match == false)
                    finalmatch= false; // We are done here
                if (combiner == Expression.ANY_OF && match == true) {
                    finalmatch = true; // We are done here
                    finaldecision.setType(Decision.CONDITION_MATCH);
                    finaldecision.resetAdvices();
                }
                // aggregate advices and obligations if we are currently heading towards a non-match so far
                if (match == false && finalmatch == false) {
                    finaldecision.setType(Decision.CONDITION_NOMATCH);
                    List<Advice> curradvices = d.getAdvices();
                    if (curradvices != null){
                        for (Advice cadvice : curradvices) {
                            finaldecision.addAdvice(cadvice);
                        }
                    }
                }

            }
        }
        // TODO : Propose Expression.NOT NOT BE SUPPPORTED for Conditions - it creates unnecessary complications.
        return finaldecision;
    }
    public void initialize(Properties props) throws Exception
    {
        _props = props;
        // setup PolicyStore
        String pcl = props.getProperty(POLICYSTORE_CLASS_PROP);
        if (pcl == null)
            pcl = DEFAULT_POLICYSTORE_CLASS;
        _ps  = (PolicyStore) Class.forName(pcl).newInstance();
        // Cache all policies in memory
        policyCache = _ps.getAllPolicies();
    }
    Properties _props = null;
    PolicyStore _ps = null;
}
