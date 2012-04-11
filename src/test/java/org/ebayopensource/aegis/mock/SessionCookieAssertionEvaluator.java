package org.ebayopensource.aegis.mock;

import org.ebayopensource.aegis.*;
import org.ebayopensource.aegis.debug.Debug;
import org.ebayopensource.aegis.impl.BaseAssertionEvaluator;
import java.net.*; 
import java.text.*; 
import java.util.*; 

/**
  * Session cookie format :<br>
  * <Valid>:<authNtime>:<userid>:<userconfirmed>:<IDP>:<tokentype>:<browsersession>
  */
public class SessionCookieAssertionEvaluator extends BaseAssertionEvaluator
{
    final public static String SHARED_STATE_SESSION_DATA_KEY = "SHARED_STATE_SESSION_DATA_KEY";

    public void initialize(Context context)
    {
        // Check of we have already processed the session cookie as part of this evaluation
        Object sessiondata = context.getSharedState(SHARED_STATE_SESSION_DATA_KEY);
        Debug.message("SessionCookieAssertionEvaluator", "initialize:sessiondata="+sessiondata);
        if (sessiondata != null) {
            return;
        }
        // Process USERSESSION cookie
        HashMap<String,Object> data = new HashMap<String,Object>();
        Object stuff = context.getEnvValue("HTTPCOOKIES");
        ArrayList<HttpCookie> cookies = (ArrayList<HttpCookie>) stuff;
        
        // Start with assuming user is not authenticated
        data.put(SessionPolicyConstants.AUTHENTICATED, new Boolean(false));
        
        for (HttpCookie cookie : cookies) {
            if ("USERSESSION".equals(cookie.getName())) {
                String val = cookie.getValue();
                Debug.message("SessionCookieAssertionEvaluator", "initialize :cookie="+val);
                StringTokenizer tok = new StringTokenizer(val, ":");
                // VALID|INVALID
                if (tok.hasMoreTokens()) {
                    String s = tok.nextToken(); 
                    Debug.message("SessionCookieAssertionEvaluator", "initialize :valid="+s);
                    if ("VALID".equals(s)) 
                        data.put(SessionPolicyConstants.AUTHENTICATED, new Boolean(true));
                }
                // AuthN DateTime
                if (tok.hasMoreTokens()) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssZ");
                        String s = tok.nextToken(); 
                        Debug.message("SessionCookieAssertionEvaluator", "initialize :authndt="+s);
                        Date authnDate = df.parse(s); 
                        long authnStart = authnDate.getTime();
                        int i = (int) ((System.currentTimeMillis() - authnStart)/6000); 
                        long xx = (System.currentTimeMillis() - authnStart)/6000;
                        Debug.message("SessionCookieAssertionEvaluator", "curr="+System.currentTimeMillis()+" :authnStart="+authnStart+ " i="+i+" l="+xx);
                        Integer dursincelogin = new Integer(i);
                        data.put(SessionPolicyConstants.TIME_AUTHENTICATED, val);
                        data.put(SessionPolicyConstants.FRESHNESS_FROM_START, dursincelogin);
                    } catch (Exception ex) { 
                        /* Handle error if data cannot be parsed */
                    }
                }
                
                // Userid
                if (tok.hasMoreTokens()) {
                    String s = tok.nextToken(); 
                    Debug.message("SessionCookieAssertionEvaluator", "initialize :userid="+s);
                    data.put(SessionPolicyConstants.AUTHN_USERID, s);
                }

                // User confirmed
                if (tok.hasMoreTokens()) {
                    String s = tok.nextToken(); 
                    Debug.message("SessionCookieAssertionEvaluator", "initialize :confirmed="+s);
                    if ("CONFIRMED".equals(s))
                        data.put(SessionPolicyConstants.CONFIRMED_USER, new Boolean(true));
                    else
                        data.put(SessionPolicyConstants.CONFIRMED_USER, new Boolean(false));
                }
 
                // IDP
                if (tok.hasMoreTokens()) {
                    String s = tok.nextToken(); 
                    Debug.message("SessionCookieAssertionEvaluator", "initialize :idp="+s);
                    data.put(SessionPolicyConstants.IDENTITY_PROVIDER, s);
                }
                // Token Type
                if (tok.hasMoreTokens()) {
                    String s = tok.nextToken(); 
                    Debug.message("SessionCookieAssertionEvaluator", "initialize :toktype="+s);
                    data.put(SessionPolicyConstants.TOKEN_TYPE, s);
                }

                // Browser session
                if (tok.hasMoreTokens()) {
                    String s = tok.nextToken(); 
		    Debug.message("SessionCookieAssertionEvaluator", "initialize :browsersess="+s);
                    if ("BROWSER_SESSION".equals(s))
                        data.put(SessionPolicyConstants.BROWSER_SESSION, new Boolean(true));
                    else
                        data.put(SessionPolicyConstants.BROWSER_SESSION, new Boolean(false));
                }
                break;
            }
        }
        // Store processes data in shaed state 
        context.setSharedState(SHARED_STATE_SESSION_DATA_KEY, data);
    }
    public Object getValue(String id, Context context)
    {
        HashMap<String,Object> sessiondata = 
            (HashMap<String,Object> ) context.getSharedState(SHARED_STATE_SESSION_DATA_KEY);
        return sessiondata.get(id);
    }
}
