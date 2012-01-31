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

public class Effect
{
    final public static int DENY = 0;
    final public static int PERMIT = 1;
    final public static int UNKNOWN = 2;
    static final public int MAX = 2;

    public Effect(int e)
    {
        if (e > MAX )
            throw new InvalidParameterException();
        _effect = e;
    }
    public int get()
    {
        return _effect;
    }
    public void set( int e)
    {
        if (e > MAX )
            throw new InvalidParameterException();
        _effect = e;
    }
    private int _effect;
}
