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
package gercan.jme.analytics;

import java.util.Random;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import net.sourceforge.floggy.persistence.ObjectSet;

/**
 * <p>
 * The interface for tracking activity within JavaME applications and reporting
 * that activity to Google Analytics. You can use this interface to track both
 * pageviews and events.
 * </p>
 * <p>
 * Before you can start using this interface you need a web property id (also
 * known as UA number). For details on creating an account getting the web
 * property ID visit <a href="http://www.google.com/analytics">Google analytics
 * website </a>. This interface uses the mobile version of the web property id
 * (UA replaced with MO).
 * </p><p>
 * This interface operates in two modes. When it is on the immediate mode
 *  {@link GoogleAnalytics#setImmediate(boolean)}. 
 * </p>
 * 
 * @author Gorkem Ercan
 * 
 */
public class GoogleAnalytics {
	private static final String digits = "0123456789ABCDEF";
	/*
	 * internal instance
	 */
	private static GoogleAnalytics INSTANCE;

	private String accountId;
	private int screenWidth;
	private int screenHeight;
	private int colorDepth;

	private String rootPath;
	private String userAgent;
	private String locale;
	private String hostname;
	private SessionData session;
	private boolean immediate;
	private ObjectSet currentEventSet;
	private int processedEventIndex;
	private Object dataLock = new Object();
	
	private GoogleAnalytics(String accountId, int screenWidth,
			int screenHeight, int colorDepth) {
		this.immediate = false;
		this.accountId = accountId;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.colorDepth = colorDepth;
		initFromSytemProperties();
		initSessionData();
	}

	/**
	 * Factory method that creates the GoogleAnalytics instance.
	 * 
	 * @param webPropertyID
	 *            Google analytics web id
	 * @param width
	 *            screen width of the device
	 * @param height
	 *            screen height of the device
	 * 
	 * @throws IllegalArgumentException
	 *             if accountId or midlet is null
	 */
	public static GoogleAnalytics getInstance(String webPropertyID,
			MIDlet midlet) {

		synchronized (GoogleAnalytics.class) {
			if (INSTANCE == null) {
				if (webPropertyID == null) {
					throw new IllegalArgumentException(
							"Account id is mandatory can not be null");
				}
				if (midlet == null) {
					throw new IllegalArgumentException(
							"Midlet is mandatory can not be null");
				}

				// a temporary canvas
				Canvas c = new Canvas() {
					protected void paint(Graphics arg0) {
					}
				};
				Display display = Display.getDisplay(midlet);
				int colors = display.numColors();
				int colorDepth = 1;
				do {
					colorDepth++;
				} while (colors >> colorDepth != 1);
				INSTANCE = new GoogleAnalytics(webPropertyID, c.getWidth(),
						c.getHeight(), colorDepth);
			}
		}
		return INSTANCE;
	}

	/**
	 * This is the standard unit of measure for a traditional web site, It is
	 * recommended that you trigger at least one pageview at application load to
	 * track unique visitors. You must decide when to trigger a pageview
	 * requests as they will not happen automatically as in a web site. Choose
	 * the url strings to be descriptive. Url strings will be populated to your
	 * reports as page paths.
	 * 
	 * @param url
	 *            descriptive name for the current view
	 */
	public void trackPageView(String url) {
		if (url == null)
			return;

		if (url.charAt(0) != '/') {
			url = "/" + url;
		}
		StringBuffer path = new StringBuffer(getRootPath());
		// document Path
		path.append("&utmp=");
		path.append(encode(url));

		if (immediate) {
			try {
				makeRequest(path.toString());
			} catch (Exception e) {	}
		} else {
			
			EventData data = new EventData();
			data.setUrl(path.toString());
			synchronized (dataLock) {
				AnalyticsDataPersistenceUtils.storeData(data);
				loadEventSet();
			}
			
		}

	}

	/**
	 * For tracking additional events to be reported in the Event Tracking
	 * section of Google Analytics. See the <a href=
	 * "http://code.google.com/apis/analytics/docs/tracking/eventTrackerGuide.html"
	 * >Event Tracking Guide</a> for detailed uses.
	 * 
	 * @param category
	 *            The name you supply for the group of objects you want to track
	 * @param action
	 *            A string that is uniquely paired with each category, and
	 *            commonly used to define the type of user interaction
	 * @param opt_label
	 *            An optional string to provide additional dimensions to the
	 *            event data.
	 * @param opt_value
	 *            An integer that you can use to provide numerical data about
	 *            the user event.
	 */
	public void trackEvent(String category, String action, String opt_label,
			int opt_value) {
		StringBuffer path = new StringBuffer(getRootPath());

		path.append("&utmt=event");
		// Event data
		path.append("&utme=5(");
		path.append(encode(category));
		path.append("*");
		path.append(encode(action));
		if (opt_label != null) {
			path.append("*");
			path.append(encode(opt_label));
		}
		path.append(")");
		if (opt_value > -1) {
			path.append("(");
			path.append(opt_value);
			path.append(")");
		}

		if (immediate) {
			try {
				makeRequest(path.toString());
			} catch (Exception e) {/*ignored*/}
		} else {
			EventData data = new EventData();
			data.setUrl(path.toString());
			synchronized (dataLock) {
				AnalyticsDataPersistenceUtils.storeData(data);
				loadEventSet();
			}
		}

	}
	/**
	 * Returns if it is on the immediate mode
	 * @return immediate status
	 */
	public boolean isImmediate() {
		return immediate;
	}
	/**
	 * Sets the immediate mode. In immediate mode the events are send 
	 * right away.
	 * 
	 * @param immediate
	 */
	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}
	/**
	 * Reads and dispatches the events that have been stored. 
	 * Dispatches events not only from this session but 
	 * also the events that were stored but not dispatched on previous sessions as well.
	 * <p>
	 * If the GoogleAnalytics is on immediate mode no events are 
	 * stored and calling this method has no use.
	 *  
	 * @return if there are more events to dispatch
 	 */
	public boolean readAndDispatch() {
		synchronized (dataLock) {

			if (currentEventSet == null) {
				loadEventSet();
			}

			if (currentEventSet == null || currentEventSet.size() < 1) {
				return false;
			}
			try {
				EventData data = (EventData) currentEventSet
						.get(processedEventIndex);
				makeRequest(data.getUrl());
				processedEventIndex++;
				AnalyticsDataPersistenceUtils.deleteData(data);
				return currentEventSet.size() > processedEventIndex;

			} catch (Exception e) {
				return false;
			}
		}
	}
	private void loadEventSet(){
		currentEventSet = AnalyticsDataPersistenceUtils.getEventDataSet();
		processedEventIndex=0;
	}
	
	
	private void initSessionData() {
		SessionData data = AnalyticsDataPersistenceUtils.readSessionData();
		long now = System.currentTimeMillis() / 1000L; // Analytics uses times
														// divided by 1000
		if (data == null) {
			data = new SessionData();
			data.setCurrTimestamp(now);
			data.setPrevTimestamp(now);
			data.setFirstTimestamp(now);
			data.setVisits(1);
			Random r = new Random();
			data.setUserId(r.nextInt(9999999));
		} else {
			data.setPrevTimestamp(data.getCurrTimestamp());
			data.setCurrTimestamp(now);
			data.setVisits(data.getVisits() + 1);
		}
		AnalyticsDataPersistenceUtils.storeData(data);
		this.session = data;
	}

	private void initFromSytemProperties() {
		locale = System.getProperty("microedition.locale");
		hostname = System.getProperty("microedition.hostname");

		String platform = System.getProperty("microedition.platform");
		String configuration = System.getProperty("microedition.configuration");
		String profiles = System.getProperty("microedition.profiles");
		StringBuffer userAgentBuffer = new StringBuffer();
		userAgentBuffer.append(platform);
		userAgentBuffer.append(" (Profile/").append(profiles);
		userAgentBuffer.append(" Configuration/").append(configuration);
		userAgentBuffer.append("; ").append(locale);
		userAgentBuffer.append(")");
		userAgent = userAgentBuffer.toString();
	}

	private void makeRequest(String path) throws Exception {
		System.out.println("URL:" + path);
		System.out.println("UserAgent:" + userAgent);
		HttpConnection c = (HttpConnection) Connector.open(path);
		c.setRequestProperty("Host", "www.google-analytics.com");
		c.setRequestProperty("User-Agent", userAgent);
		int rc = c.getResponseCode();
		if (rc != HttpConnection.HTTP_OK) {
			throw new Exception("Connection failed with response code " + rc);
		}
	}

	private String getCookie() {
		StringBuffer cookieString = new StringBuffer();
		cookieString.append("__utma=");
		cookieString.append("999").append(".");
		cookieString.append(session.getUserId()).append(".");
		cookieString.append(session.getFirstTimestamp()).append(".");// first
		cookieString.append(session.getPrevTimestamp()).append("."); // previous
		cookieString.append(session.getCurrTimestamp()).append("."); // current
		cookieString.append(session.getVisits());// visits
		return encode(cookieString.toString());
	}

	/**
	 * Creates the beginning of the tracking .gif url. This part of the url is
	 * valid for this session and created once. Refer to <a href=
	 * "http://code.google.com/apis/analytics/docs/tracking/gaTrackingTroubleshooting.html#gifParameters"
	 * > analytics document</a> for details of the parameters
	 * 
	 * @return beginning part of the tracking gif url
	 */
	private String getRootPath() {
		synchronized (INSTANCE) {
			if (rootPath == null) {
				String encoding = System.getProperty("microedition.encoding");
				StringBuffer path = new StringBuffer();
				path.append("http://www.google-analytics.com/__utm.gif");
				path.append("?utmwv=4.4ma"); // Tracking code version
				path.append("&utmn=-1"); // ID to prevent caching
				path.append("&utmcs=").append(encoding); // Language encoding
				// Screen size
				path.append("&utmsr=");
				path.append(String.valueOf(screenWidth));
				path.append("x");
				path.append(String.valueOf(screenHeight));
				// Screen color depth
				path.append("&utmsc=");
				path.append(String.valueOf(colorDepth)).append("-bit");
				// hostname
				path.append("&utmhn=").append(hostname);
				// language
				path.append("&utmul=");
				path.append(locale);
				path.append("&utmac=").append(accountId);
				path.append("&utmcc=").append(getCookie());
				rootPath = path.toString();
			}
		}
		return rootPath;
	}

	private static String encode(String s) {
		StringBuffer buf = new StringBuffer(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
					|| (ch >= '0' && ch <= '9') || ".-*_".indexOf(ch) > -1) { //$NON-NLS-1$
				buf.append(ch);
			} else if (ch == ' ') {
				buf.append('+');
			} else {
				byte[] bytes = new String(new char[] { ch }).getBytes();
				for (int j = 0; j < bytes.length; j++) {
					buf.append('%');
					buf.append(digits.charAt((bytes[j] & 0xf0) >> 4));
					buf.append(digits.charAt(bytes[j] & 0xf));
				}
			}
		}
		return buf.toString();
	}
}
