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
package org.ebayopensource.aegis;

import java.util.ArrayList;
import java.util.List;

public class Decision
{
    static public final int EFFECT_DENY = 0;
    static public final int EFFECT_PERMIT = 1;
    static public final int EFFECT_UNKNOWN = 2;
    static public final int CONDITION_MATCH = 3;
    static public final int CONDITION_NOMATCH = 4;
    static String[] _strs = 
              { "DENY", "PERMIT", "UNKNOWN", "COND_MATCH", "COND_NOMATCH"};
    /**
     * Constructs a Decision 
     * @param type decision type (eg Allow, Deny, Unknown)
     */
    public Decision(int type)
    {
        _type = type;
    }
    /**
     * Sets decision type
     * @param type
     */
    public void setType(int type)
    {
        _type = type;
    }

    /**
     * Gets string version of decision type
     */
    public String getTypeStr()
    {
        return _strs[_type];
    }

    /**
     * Adds an Obligation to the decision
     * @param o Obligation
     */
    public void addObligation(Obligation o)
    {
        if (_obligations == null) {
            _obligations = new ArrayList<Obligation>();
        }
        _obligations.add(o);
    }

    /**
     * Adds an Advice to the decision
     * @param a Advice
     */
    public void addAdvice(Advice a)
    {
        if (_advices == null) {
            _advices = new ArrayList<Advice>();
        }
        _advices.add(a);
    }
    /**
     * Retrieve decison type
     * @return type
     */
    public int getType()
    {
        return _type;
    }

    /**
     * Retrieve Obligations
     * @return obligations
     */
    public List<Obligation> getObligations()
    {
        return _obligations;
    }

    /**
     * Retrieve Advices
     * @return advices
     */
    public List<Advice> getAdvices()
    {
        return _advices;
    }

    /**
     * Reset Obligations
     */
    public void resetObligations()
    {
        _obligations = null;
    }

    /**
     * Reset  Advices
     */
    public void resetAdvices()
    {
        _advices = null;
    }

    public String toString()
    {
        StringBuilder sbld = new StringBuilder();
        sbld.append("Decision : { effect : \"").append(getType()).append("\", ") ;
        sbld.append("Obligations: ").append(getObligations()).append(", ");
        sbld.append("Advices: ");
           if (_advices != null) {
               for (Advice adv : getAdvices()) {
                  sbld.append(adv);
               }
           }
        sbld.append("}");
        return sbld.toString();
    }

    private int _type = 0;
    private ArrayList<Obligation> _obligations = null;
    private ArrayList<Advice> _advices = null;
}
