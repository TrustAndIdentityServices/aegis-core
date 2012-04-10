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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.HttpCookie;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.debug.Debug;
import org.junit.Before;
import org.junit.Test;

public class SessionPoliciesTest
{
    private long L0MAX = 525600;
    private long L1MAX = 1440;
    private long L2MAX = 20;
    private PolicyDecisionPoint pdp = null;
    @Before
    public void setUp()
    {
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDPSessionPolicy.properties");
            props.load(url.openStream());
            pdp = PolicyEnforcementPoint.getPDP(props);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to get PDP:"+ex);
        }
    }

    @Test
    public void testL0TimeExceeded() {
        Target resource = new Target("webcmd", "L0CMD");
        List<Environment> env = new ArrayList<Environment>();
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
        cookies.add(new HttpCookie("COOKIE_USERID", "usertest"));
        cookies.add(new HttpCookie("COOKIE_SESSION", "11111111"));
        cookies.add(new HttpCookie("COOKIE_AUTHNDATE", getDateBefore(L0MAX+1)));
        Environment env1 = new Environment("Session", "env1");
        env1.setAttribute("HTTPCOOKIES", cookies);
        env.add(env1);
        Decision decision = 
            pdp.getPolicyDecision( resource, env);
        Debug.message("L0Test", decision.toString());
        // Decision should not be null
        assertEquals(true, decision != null);
        // Decision should be DENY
        assertEquals(Decision.EFFECT_DENY, decision.getType());
        List<Advice> advs = decision.getAdvices();
        assertEquals(true, advs != null);
    }

    private String getDateBefore(long l)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssZ");
        Date now = new Date();
        long nowl = now.getTime()/6000;
        long pl = (nowl - l) * 6000;
        Date dt = new Date(pl);
        return(df.format(dt));
    }
}
