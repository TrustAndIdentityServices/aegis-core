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
    static public final int RULE_INIT = 3;
    static public final int RULE_MATCH = 4;
    static public final int RULE_NOMATCH = 5;
    static String[] s_strs = 
              { "DENY", "PERMIT", "UNKNOWN","RULE_INIT", "RULE_MATCH", "RULE_NOMATCH"};
    private int m_type = 0;
    private ArrayList<Obligation> m_obligations = null;
    private ArrayList<Advice> m_advices = null;

    /**
     * Constructs a Decision 
     * @param type decision type (eg Allow, Deny, Unknown)
     */
    public Decision(int type)
    {
        m_type = type;
    }
    /**
     * Sets decision type
     * @param type
     */
    public void setType(int type)
    {
        m_type = type;
    }

    /**
     * Gets string version of decision type
     */
    public String getTypeStr()
    {
        return s_strs[m_type];
    }

    /**
     * Adds an Obligation to the decision
     * @param o Obligation
     */
    public void addObligation(Obligation o)
    {
        if (m_obligations == null) {
            m_obligations = new ArrayList<Obligation>();
        }
        m_obligations.add(o);
    }

    /**
     * Adds an Advice to the decision
     * @param a Advice
     */
    public void addAdvice(Advice a)
    {
        if (m_advices == null) {
            m_advices = new ArrayList<Advice>();
        }
        m_advices.add(a);
    }
    /**
     * Retrieve decison type
     * @return type
     */
    public int getType()
    {
        return m_type;
    }

    /**
     * Retrieve Obligations
     * @return obligations
     */
    public List<Obligation> getObligations()
    {
        return m_obligations;
    }

    /**
     * Retrieve Advices
     * @return advices
     */
    public List<Advice> getAdvices()
    {
        return m_advices;
    }

    /**
     * Reset  Advices
     */
    public void resetAdvices()
    {
        m_advices = null;
    }

    /**
     * Reset  Obligations
     */
    public void resetObligations()
    {
        m_obligations = null;
    }

    public String toString()
    {
        StringBuilder sbld = new StringBuilder();
        sbld.append("Decision : { effect : \"").append(getTypeStr()).append("\", ") ;
        sbld.append("Obligations: ").append(getObligations()).append(", ");
        sbld.append("Advices: ");
           if (m_advices != null) {
               for (Advice adv : getAdvices()) {
                  sbld.append(adv);
               }
           }
        sbld.append("}");
        return sbld.toString();
    }
    public Decision deepcopy() throws Exception
    {
        Decision d = new Decision(m_type);
        if (m_obligations != null)
            d.m_obligations = (ArrayList<Obligation> ) m_obligations.clone();
        if (m_advices != null)
            d.m_advices     = (ArrayList<Advice>) m_advices.clone();
        return d;
    }
}
