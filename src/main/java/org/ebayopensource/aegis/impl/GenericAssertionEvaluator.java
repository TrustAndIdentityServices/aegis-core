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
import java.util.HashMap;
import java.util.List;

import org.ebayopensource.aegis.Advice;
import org.ebayopensource.aegis.CExpr;
import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.PolicyException;
import org.ebayopensource.aegis.plugin.AssertionEvaluator;
import org.ebayopensource.aegis.debug.Debug;

/**
  * This class evaluates a Assertion based on the data in the context.
  * Restrictions:
  * All data it needs must be available in the input <code>context</code>
  * Only primitive types that respond correctly to <code>java.lang.Comparable</code> are acceptable
  * TODO : add collection support ( if (x instanceof Collection<?>){})
  */
public class GenericAssertionEvaluator implements AssertionEvaluator
{
    final static String WILD_CHAR = "*";
    public void initialize(HashMap props) 
    {
    }
    public Decision evaluate(Assertion assertion, List<Environment> context) throws Exception
    {
        CExpr expr = assertion.getCExpr();
        CExpr e = expr;
        Decision d = new Decision(Decision.RULE_MATCH);
        Advice adv = null;
        Object cval = null;
        for (Environment env : context) {
            cval = env.getAttribute(e.id_);
            if (cval != null)
                break;
        }
        Object eval = e.val_;
        Debug.message("GenericAssertionEvaluator", "evaluale:c="+cval+
                        " op="+ e.op_+ " e="+eval);
        boolean match = false;

        // WILD_CHAR
        try {
            if (eval.equals(WILD_CHAR) && e.op_ == Assertion.OP_EQ) 
                match = true;
        } catch (Exception ex) { /* OK to ignore - fallthru to processing below */ }
        // This class implements primitive types only - write a custom plugin for other datatypes & operators
        if (match == false && cval != null) {
            int comp = ((Comparable) cval).compareTo(eval);
            switch (e.op_) {
                case Assertion.OP_EQ :
                    if (comp == 0)
                        match = true;
                    break;
                     
                case Assertion.OP_NE :
                    if (comp != 0)
                        match = true;
                    break;
                case Assertion.OP_LT :
                    if (comp < 0)
                        match = true;
                    break;
                case Assertion.OP_GT :
                    if (comp > 0)
                        match = true;
                    break;
                default :
                    throw new PolicyException("INVALID_OPERATOR", e.op_);
            }
        }
        Debug.message("GenericAssertionEvaluator", "evaluate:match="+match);
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
}
