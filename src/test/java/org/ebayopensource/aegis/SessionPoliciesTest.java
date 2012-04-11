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
/**
  * This test is driven by PDPSessionPolicy.properties, SessionMetaData.properties and SessionPolicies.json.
  * And a custom AssertionEvaluator : <code>SessionCookieAssertionEvaluator</code>
  * The policies are copied below for easy reference.
  * <br>
  * <code>
  * { "Policy" : 
  *   { "silent" : false, "name" : "L0Personalization", "description" : "", "effect" : "PERMIT",
  *     "target" : [ [  "webcmd", "L0CMD"  ] ],
  *     "rules" : [ 
  *       {  "category" : "Session" , "ALL_OF" : [ 
  *         [ "Authenticated", "=", true ] ,
  *         [ "FreshnessFromStartTime", "<", 525600 ] ,
  *         [ "ConfirmedUser", "=", true ] ,
  *         [ "IdentityProvider", "=", "EBAY" ] ,
  *         [ "TokenType", "=", "EBAY_COOKIE" ] ]
  *       } ]
  *   }
  *  }
  * { "Policy" :
  *   { "silent" : false, "name" : "L1SessionPolicy", "description" : "", "effect" : "PERMIT",
  *     "target" : [ [  "webcmd", "L1CMD"  ] ],
  *     "rules" : [
  *       {  "category" : "Session" , "ALL_OF" : [ 
  *          [ "Authenticated", "=", true ] ,
  *          [ "FreshnessFromStartTime", "<", 1440 ] ,
  *          [ "ConfirmedUser", "=", true ] ,
  *          [ "IdentityProvider", "=", "EBAY" ] ,
  *          [ "TokenType", "=", "EBAY_COOKIE" ] ]
  *       } ]
  *    }
  * }
  * { "Policy" :
  *   { "silent" : false, "name" : "L2SessionPolicy", "description" : "", "effect" : "PERMIT",
  *     "target" : [ [  "webcmd", "L2CMD"  ] ],
  *     "rules" : [ 
  *       {  "category" : "Session" , "ALL_OF" : [
  *          [ "Authenticated", "=", true ] ,
  *          [ "FreshnessFromStartTime", "<", 20 ] ,
  *          [ "ConfirmedUser", "=", true ] ,
  *          [ "IdentityProvider", "=", "EBAY" ] ,
  *          [ "TokenType", "=", "EBAY_COOKIE" ] ,
  *          [ "BrowserSession", "=", true ] ]
  *       } ]
  *     }
  * }
  * </code>
  * <br>
  * USERSESSION Cookie format :<br>
  * <code>
  * <Valid>:<authNtime>:<userid>:<userconfirmed>:<IDP>:<tokentype>:<browsersession>
  * </code>
  * <br>Examples:
  * <code>
  * VALID:20120401130000-700:testuser1:CONFIRMED:Facebook:EBAY_COOKIE:BROWSERSESSION
  * INVALID:20120401130000-700:testuser1:UNCONFIRMED:::
  * </code>
  *
  */

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

        StringBuilder cookieval = new StringBuilder();
        // Construct the cookie value with all valid attrs except Freshness
        cookieval.append("VALID").append(":")
                 .append(getDateBefore(L0MAX+1)).append(":")
                 .append("usertest1xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx").append(":")
                 .append("CONFIRMED").append(":")
                 .append("EBAY").append(":")
                 .append("EBAY_COOKIE").append(":")
                 .append("BROWSERSESSION").append(":");

        List<Environment> env = new ArrayList<Environment>();
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
        cookies.add(new HttpCookie("USERSESSION", cookieval.toString()));
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
        Advice adv = advs.get(0);
        assertEquals(adv.toString().contains("FreshnessFromStartTime"), true);
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
