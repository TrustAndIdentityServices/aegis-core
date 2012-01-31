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
import org.ebayopensource.aegis.Condition;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.plugin.ConditionEvaluator;

/**
  * This class evaluates a condition based on the data in the context.
  * Restrictions:
  * All data it needs must be available in the input <code>context</code>
  * Only primitive types that respond correctly to <code>java.lang.Comparable</code> are acceptable
  * TODO : add collection support ( if (x instanceof Collection<?>){})
  */
public class GenericConditionEvaluator implements ConditionEvaluator
{
    public void initialize(HashMap props) 
    {
    }
    public Decision evaluate(Condition condition, List<Environment> context) throws Exception
    {
        ArrayList<CExpr> expr = condition.getAllAttrs();
        Decision d = new Decision(Decision.CONDITION_MATCH);
        Advice adv = null;
        for (CExpr e : expr) {
            Object cval = null;
            for (Environment env : context) {
                cval = env.getAttribute(e.id_);
                if (cval != null)
                    break;
            }
            Object eval = e.val_;
            boolean match = false;
            // TODO check for collection - implementing primitive types for now
            if (cval != null) {
                int comp = ((Comparable) cval).compareTo(eval);
                switch (e.op_) {
                    case Condition.OP_EQ :
                        if (comp == 0)
                            match = true;
                        break;
                         
                    case Condition.OP_NE :
                        if (comp != 0)
                            match = true;
                        break;
                    case Condition.OP_LT :
                        if (comp < 0)
                            match = true;
                        break;
                    case Condition.OP_GT :
                        if (comp > 0)
                            match = true;
                        break;
                }
            }
            if (match)
                continue;
            // if no match, change decision state and add to Advice, 
            d.setType(Decision.CONDITION_NOMATCH);
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
