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

import java.util.ResourceBundle;
/**
  * Base Exception class for PEF.
  * Handles i18n messages via PEFMessages.properties resource bundle
  */
public class PolicyException extends RuntimeException
{
   
    public final String RESOURCE_BUNDLE = "PEFMessages";
    public PolicyException(String errorid, String message) 
    {
        super(errorid+":"+message );
    }
    public String getLocalizedMessage()
    {
        ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE);
        return  rb.getString(errorid_);
    }
    private String errorid_ = null;
}
