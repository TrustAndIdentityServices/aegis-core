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

import java.util.List;
import java.util.ArrayList;

/**
  * Encpasulates a single Policy.
  * Comprised of
  *
  * <code>Targets Rules and optional Effect(Permit|Deny)</code>
  */
public class Policy
{
    private String           m_name;
    private String           m_description;
    private List<Target>     m_targets;
    private Effect           m_effect;
    private Expression<Rule> m_rules;

    public Policy(String name, String desc, List<Target> targets,
                  Expression<Rule> rules, Effect effect)
    {
        m_name = name;
        m_description = desc;
        m_targets = targets;
        m_effect = effect;
        m_rules = rules;
    }

    public String getName()
    {
        return m_name;
    }
    public String getDescription()
    {
        return m_description;
    }
    public List<Target> getTargets()
    {
        return m_targets;
    }

    public Effect getEffect()
    {
        return m_effect;
    }
    public Expression<Rule> getRules()
    {
        return m_rules;
    }

    public String toString()
    {
        StringBuilder sbld = new StringBuilder();
        sbld.append( "{ \"Policy\" : { ");
        sbld.append(     "\"name\" : \"").append(getName()).append("\"");
        sbld.append(     ", \"description\" : \"").append(getDescription()).append("\"");
        sbld.append(     ", ").append(getEffect());

        sbld.append(     ", \"target\" : [");
        boolean first = true;
        for (Target t : getTargets() ) {
            if (first)
                first = false;
            else
                sbld.append(", ");
            sbld.append(t.toString());
        }
        sbld.append(     " ]");

        sbld.append(     ", \"rules\" : ");

        sbld.append(getRules().toString(true));

        sbld.append(     " } }");
     
        return sbld.toString();
    }
    public static void  main(String[] args) 
    {
         
         ArrayList<Target> targets = new ArrayList<Target>();
         Target target1 = new Target("web", "http://www.ebay.com/xxxx");
         Target target2 = new Target("cmd", "CMD:addItem");
         targets.add(target1);
         targets.add(target2);
         Expression<Rule> rules = new Expression<Rule>();
         rules.setType(Expression.ALL_OF);
         

         Expression<Assertion> exp1 = new Expression<Assertion>();
         Assertion a1 = new Assertion("cat1", "assertion1");
         a1.setCExpr( "id1", 0, "val1");

         Assertion a2 = new Assertion("cat1", "assertion2");
         a2.setCExpr("id2", 0, "val2");

         exp1.add(a1);
         exp1.add(a2);
  

         Rule rule1 = new Rule("category1", "rule1", exp1);
         rules.add(rule1);
       
         rules.add(rule1);
       
         Effect effect = new Effect(Effect.PERMIT);
         Policy pol = new Policy("testPolicy", "TEST Policy", targets, rules, effect);
         System.out.println("Policy = "+pol);
    }

}
