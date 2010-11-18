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

public class PageViewTrackEventTest extends TestCase {

	
	/**
	 * The default constructor. It just transmits the necessary informations to
	 * the superclass.
	 * 
	 * @param totalOfTests the total of test methods present in the class.
	 * @param name this testcase's name.
	 */
	public PageViewTrackEventTest() {
		super(3, "PageViewTest");	
	}
	
	public void doStart() {
		GoogleAnalytics.getInstance("MO-1834873-5", this);
		super.doStart();
	}
	
	public void trackPageViewTest() {
		GoogleAnalytics.getInstance(null, null).setImmediate(true);
		GoogleAnalytics.getInstance(null,null).trackPageView("/someview");
	}
	
	public void trackEventTest(){
		GoogleAnalytics.getInstance(null, null).setImmediate(true);
		GoogleAnalytics.getInstance(null,null).trackEvent("category", "action", "label", 1001);
	}
	
	public void testReadAndDispatch(){
		GoogleAnalytics.getInstance(null, null).setImmediate(false);
		
		GoogleAnalytics.getInstance(null,null).trackPageView("/rndview"); //1
		GoogleAnalytics.getInstance(null,null).trackEvent("dispatch", "action", "label",-1);//2
		GoogleAnalytics.getInstance(null,null).trackPageView("/rndview2");//3
		GoogleAnalytics.getInstance(null,null).trackEvent("dispatch", "action2", "label",-1);//4
		GoogleAnalytics.getInstance(null,null).trackEvent("dispatch", "action3", "label",-1);//5
		int numberOfDispatches=0;
		while (true) {
			numberOfDispatches++;
			if(!GoogleAnalytics.getInstance(null, null).readAndDispatch()){
				break;
			}
			if(numberOfDispatches==3){
				GoogleAnalytics.getInstance(null,null).trackPageView("/rndview3"); //6
			}
		}
		assertEquals("Not dispatched expected amount of events", 6, numberOfDispatches);
	
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
			trackPageViewTest();
			break;
		case 1:
			trackEventTest();
			break;
		case 2:
			testReadAndDispatch();
			break;
		}
	}

}
