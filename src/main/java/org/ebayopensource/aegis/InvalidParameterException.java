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

public class InvalidParameterException extends PolicyException
{
    private static final String s_errorid = "INVALID_PARAM";
    private static final String s_msg = "One or more input params is invalid";
    public InvalidParameterException() {
        super (s_errorid, s_msg);
    }
}
