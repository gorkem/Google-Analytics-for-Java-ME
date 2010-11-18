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

import net.sourceforge.floggy.persistence.FloggyException;
import net.sourceforge.floggy.persistence.ObjectSet;
import net.sourceforge.floggy.persistence.Persistable;
import net.sourceforge.floggy.persistence.PersistableManager;

/**
 * Utilities for storing and retrieving data from data store. 
 * Uses <a href="http://floggy.sourceforge.net/">floggy</a> for persistence management.
 * 
 * @author Gorkem Ercan
 *
 */
final /*package*/ class AnalyticsDataPersistenceUtils {
	
	public static final SessionData readSessionData(){
		try{
			PersistableManager pm = PersistableManager.getInstance();
			ObjectSet set = pm.find(SessionData.class, null, null);
			if (set.size()>0){
				return (SessionData)set.get(0);
			}
			return null;
		}catch (FloggyException e) {
			e.printStackTrace();
			return null;
		}	
	}

	public static ObjectSet getEventDataSet() {
		PersistableManager pm = PersistableManager.getInstance();
		try {
			return pm.find(EventData.class, null, null, true);
		} catch (FloggyException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void deleteData(Persistable data){
		try {
			PersistableManager pm = PersistableManager.getInstance();
			pm.delete(data);
		} catch (FloggyException e) {
			//ignored
		}
	}
	
	public static void storeData(Persistable data) {
		try {
			PersistableManager pm = PersistableManager.getInstance();
			pm.save(data);
		} catch (FloggyException e) {
			//ignored
		}
	}
	


}
