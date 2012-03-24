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
package org.ebayopensource.aegis.plugin;

import java.io.*;
import java.util.*;

public interface AuditLogger
{
    final static String AUDIT_LOG_FILE_PARAM = "AUDIT_LOG_FILE";
    /**
     * Initialize logger
     * @param props
     */
    public void initialize(Properties props);

    /**
     * Ceate a Audit Log record
     * @param type
     * @param msgid
     * @param message
     */
    public void log(String type, String msgid, String message);
}
