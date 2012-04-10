package org.ebayopensource.aegis.mock;

import org.ebayopensource.aegis.*;
import org.ebayopensource.aegis.impl.BaseAssertionEvaluator;
import java.net.*; 
import java.text.*; 
import java.util.*; 

public class SessionCookieAssertionEvaluator extends BaseAssertionEvaluator
{
    private HashMap<String,Object> data = new HashMap<String,Object>();
    public void initialize(Context context)
    {
        // Process "Test*" cookies
        Object stuff = context.getEnvValue("HTTPCOOKIES");
        ArrayList<HttpCookie> cookies = (ArrayList<HttpCookie>) stuff;
        
        // Start with assuming user is not authenticated
        data.put(SessionPolicyConstants.AUTHENTICATED, new Boolean(false));

        for (HttpCookie cookie : cookies) {
            String nm = cookie.getName();
            String val = cookie.getValue();
            if ("COOKIE_USERID".equals(nm))
                data.put(SessionPolicyConstants.AUTHN_USERID, val);
            else if ("COOKIE_SESSION".equals(nm))
                data.put(SessionPolicyConstants.AUTHENTICATED, new Boolean(true));
            else if ("COOKIE_AUTHNDATE".equals(nm)) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssZ");
                    Date authnDate = df.parse(val); 
                    long authnStart = authnDate.getTime();
                    Long dursincelogin = 
                      new Long((System.currentTimeMillis() - authnStart)/6000); 
                    data.put(SessionPolicyConstants.TIME_AUTHENTICATED, val);
                    data.put(SessionPolicyConstants.FRESHNESS_FROM_START, dursincelogin);
                } catch (Exception ex) { 
                    /* Handle error if data cannot be parsed */
                }
            }

        }
    }
    public Object getValue(String id, Context context)
    {
        return data.get(id);
    }
}
