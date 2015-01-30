package litebonus.dostyk.core;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by prog on 23.10.14.
 */
public class NavigationScreen {
    private static NavigationScreen instance;
    private JSONObject screen;
    private JSONArray templates;
    private NavigationScreen(){
        screen = new JSONObject();
        templates = new JSONArray();
    }

    public static NavigationScreen getInstance() {
        if(instance == null){
            instance = new NavigationScreen();
        }
        return instance;
    }

    public void setScreen(JSONObject screen) {
        this.screen = screen;
        templates = screen.optJSONArray("templates");
    }

    public View createView(Activity context){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout layout = new LinearLayout(context);
        //layout.setBackgroundColor(Color.parseColor("#000000"));
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        for(int i = 0; i < templates.length(); i++){
            JSONObject template = templates.optJSONObject(i).optJSONObject("template");
            JSONArray data = template.optJSONArray("data");
            JSONObject body = template.optJSONObject("body");
            JSONArray styles = template.optJSONArray("styles");
            Actions.getInstance().put(template.optJSONArray("actions"));
            layout.addView(Core.createView(context, body, data, styles), params);
        }
        return layout;
    }
}
