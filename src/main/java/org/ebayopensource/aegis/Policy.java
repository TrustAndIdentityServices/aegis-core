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
/**
  * Encpasulates a single Policy.
  * Comprised of
  * <code>Subjects Effect(Permit|Deny) Resources Actions Conditions </code>
  */
public class Policy
{
    public Policy(Expression<Subject> subjects, List<Resource> resources, List<Action> actions, Effect effect, Expression<Condition> conditions)
    {
        subjects_ = subjects;
        resources_ = resources;
        actions_ = actions;
        effect_ = effect;
        conditions_ = conditions;
    }
    public Expression<Subject> getSubjects() 
    {
        return subjects_;
    }
    public List<Resource> getResources()
    {
        return resources_;
    }
    public List<Action> getActions()
    {
        return  actions_;
    }
    public Effect getEffect()
    {
        return effect_;
    }
    public Expression<Condition> getConditions()
    {
        return conditions_;
    }

    public String toString()
    {
        StringBuilder sbld = new StringBuilder();
        sbld.append("Policy : {");
          sbld.append("Subjects : { ");
            sbld.append(subjects_);
          sbld.append("}");

        if (resources_ != null) {
          sbld.append(", Resources : { ");
            for (Resource r : resources_)
                sbld.append(r);
          sbld.append("}");
        }
        if (actions_ != null) {
          sbld.append(", Actions : { ");
            for (Action a : actions_)
                sbld.append(a);
          sbld.append("}");
        }

          sbld.append(", Conditions : { ");
            sbld.append(conditions_);
          sbld.append("}");
          
        sbld.append("}");
        return sbld.toString();
    }
    private Expression<Subject> subjects_;
    private List<Resource> resources_;
    private List<Action> actions_;
    private Effect effect_;
    private Expression<Condition> conditions_;

}
