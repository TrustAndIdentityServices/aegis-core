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

public class CExpr {
    public int op_;
    public String id_;
    public Object val_;
    public CExpr(String id, int op, Object val) {
        id_ = id;
        op_ = op;
        val_ = val;
    }
}
