package litebonus.dostyk.interfaces;

import org.json.JSONObject;

/**
 * Created by prog on 07.08.2014.
 */
public interface ApiCallback {
    void statusReceive(JSONObject status, int code);
    //void statusReceive(JSONObject status, int code, IConferenceMonitoringResult beacon);
}
