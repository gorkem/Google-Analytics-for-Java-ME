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

import net.sourceforge.floggy.persistence.Persistable;
/**
 * Persistable object for storing the session data
 * 
 * @author Gorkem Ercan
 *
 */
public class SessionData implements Persistable {
	

	private long prevTimestamp;
    private long currTimestamp;
    private long firstTimestamp;
    private int visits;
	private int userId;
    
    
    public SessionData() {
    	
	}
    
    int getUserId() {
		return userId;
	}

	void setUserId(int userId) {
		this.userId = userId;
	}

	long getFirstTimestamp() {
		return firstTimestamp;
	}

	void setFirstTimestamp(long firstTimestamp) {
		this.firstTimestamp = firstTimestamp;
	}

	long getPrevTimestamp() {
		return prevTimestamp;
	}

	void setPrevTimestamp(long prevTimestamp) {
		this.prevTimestamp = prevTimestamp;
	}

	long getCurrTimestamp() {
		return currTimestamp;
	}

	void setCurrTimestamp(long currTimestamp) {
		this.currTimestamp = currTimestamp;
	}

	int getVisits() {
		return visits;
	}

	void setVisits(int visits) {
		this.visits = visits;
	}

	
    
    
}
