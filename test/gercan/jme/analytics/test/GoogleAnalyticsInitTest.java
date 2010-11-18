/*************************************************************************
* Copyright 2010 Gorkem Ercan
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
**************************************************************************/
package gercan.jme.analytics.test;

import gercan.jme.analytics.GoogleAnalytics;
import jmunit.framework.cldc11.TestCase;

public class GoogleAnalyticsInitTest extends TestCase {

	/**
	 * The default constructor. It just transmits the necessary informations to
	 * the superclass.
	 * 
	 * @param totalOfTests the total of test methods present in the class.
	 * @param name this testcase's name.
	 */
	public GoogleAnalyticsInitTest() {
		super(3, "GoogleAnalyticsInitTest");
	}

	public void nullMidletCheck(){
		boolean nullCheck=false;
		try{
			GoogleAnalytics.getInstance("TEST_ACCOUNT", null);
		}catch (Exception e) {
			assertTrue("Exception type is incorrect",(e instanceof IllegalArgumentException) );
			nullCheck=true;
		}
		assertTrue("null midlet check failed",nullCheck);
		
	}
	
	public void nullAccountCheck() {
		boolean nullCheck=false;
		try{
			GoogleAnalytics.getInstance(null,this);
		}catch (Exception e) {
			assertTrue("Exception type is incorrect",(e instanceof IllegalArgumentException) );
			nullCheck=true;
		}
		assertTrue("null account id check failed",nullCheck);
	}

	public void testMultipleInit() {
		GoogleAnalytics i1= GoogleAnalytics.getInstance("TEST_ACCOUNT",this);
		GoogleAnalytics i2= GoogleAnalytics.getInstance("TEST_ACCOUNT2",this);
		assertSame("Subsequent calls to getInstance should return same object",i1, i2);
	
	}

	/**
	 * This method stores all the test methods invocation. The developer must
	 * implement this method with a switch-case. The cases must start from 0 and
	 * increase in steps of one until the number declared as the total of tests
	 * in the constructor, exclusive. For example, if the total is 3, the cases
	 * must be 0, 1 and 2. In each case, there must be a test method invocation.
	 * 
	 * @param testNumber the test to be executed.
	 * @throws Throwable anything that the executed test can throw.
	 */
	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: 
			nullMidletCheck();
			break;
		case 1:
			nullAccountCheck();
			break;
		case 2:
			testMultipleInit();
			break;
		}
	}

}
