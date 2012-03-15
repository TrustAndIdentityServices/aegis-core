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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AdviceTest.class,
	EffectTest.class,
	EnvironmentTest.class,
	ObligationTest.class,
	PolicyEvaluationTest.class,
        JSONDataStoreTest.class
})
public class AllTests {

}
