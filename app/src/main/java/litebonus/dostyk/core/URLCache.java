package litebonus.dostyk.core;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prog on 28.10.14.
 */
public class URLCache {
    private static URLCache instance;
    private Map<String, JSONObject> cacheMap;
    private URLCache(){
        cacheMap = new HashMap<>();
    }

    public Map<String, JSONObject> getCacheMap() {
        return cacheMap;
    }


    public static URLCache getInstance() {
        if(instance == null){
            instance = new URLCache();
        }
        return instance;
    }

}
