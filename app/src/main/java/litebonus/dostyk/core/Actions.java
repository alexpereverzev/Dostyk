package litebonus.dostyk.core;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by prog on 17.10.14.
 */
public class Actions {
    private static Actions instance;
    private JSONArray actions;
    private Actions(){
        actions = new JSONArray();
    }

    public static Actions getInstance() {
        if(instance == null){
            instance = new Actions();
        }
        return instance;
    }

    public JSONArray getActions() {
        return actions;
    }

    public void put(JSONArray actions){
        for(int i = 0; i < actions.length(); i++){
            JSONObject item = actions.optJSONObject(i);
            if(find(item)) {
                this.actions.put(item);
            }
        }
    }

    private boolean find(JSONObject action){
        for (int i = 0; i < this.actions.length(); i++){
            JSONObject item = this.actions.optJSONObject(i);
            if(action.optInt("id") == item.optInt("id")){
                return false;
            }
        }
        return true;
    }

    public void setActions(JSONArray actions) {
        this.actions = actions;
    }
}
