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
    private String           m_id;
    private String           m_version;
    private String           m_name;
    private String           m_description;
    private List<Target>     m_targets;
    private Effect           m_effect;
    private Expression<Rule> m_rules;
    private boolean          m_silent = false;
    private boolean          m_active = true;

    public Policy(String name, String desc, List<Target> targets,
                  Expression<Rule> rules, Effect effect)
    {
        m_name = name;
        m_description = desc;
        m_targets = targets;
        m_effect = effect;
        m_rules = rules;
    }

    public String getId()
    {
        return m_id;
    }
    public void setId(String id)
    {
        m_id = id;
    }
    public String getName()
    {
        return m_name;
    }
    public String getVersion()
    {
        return m_version;
    }
    public void setVersion(String version)
    {
        m_version = version;
    }
    public String getDescription()
    {
        return m_description;
    }
    public void setDescription(String desc)
    {
        m_description = desc;
    }
    public boolean isSilent()
    {
        return m_silent;
    }
    public void setSilent(boolean silent)
    {
        m_silent = silent;
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

    public boolean isActive()
    {
        return m_active;
    }

    public void setActive(boolean state)
    {
        m_active = state;
    }

    public String toString()
    {
        StringBuilder sbld = new StringBuilder();
        sbld.append( "{ \"Policy\" : { ");
        if (m_id != null) {
            sbld.append(     "\"id\" : \"").append(m_id).append("\", ");
        }
        sbld.append(     "\"active\" : ").append(isActive());
        sbld.append(     ", \"silent\" : ").append(isSilent());
        sbld.append(     ", \"name\" : \"").append(getName()).append("\"");
        sbld.append(     ", \"description\" : \"").append(getDescription()).append("\"");

        if (m_version != null) {
            sbld.append(     ", \"version\" : \"").append(m_version).append("\"");
        }

        sbld.append(     ", ").append(getEffect());

        sbld.append(     ", \"target\" : [");
        boolean first = true;
 
        List<Target> targetList = getTargets();
        if (targetList != null) {
            for (Target t : getTargets() ) {
                if (first)
                    first = false;
                else
                    sbld.append(", ");
                sbld.append(t.toString());
            }
        } 
        sbld.append(     " ]");
        sbld.append(     ", \"rules\" : ");
        Expression<Rule> rulesExp = getRules();

        if (rulesExp != null) {
            sbld.append(getRules().toString(true));
        } else {
            sbld.append( "[ ]");
        }

        sbld.append(     " } }");
     
        return sbld.toString();
    }
}
