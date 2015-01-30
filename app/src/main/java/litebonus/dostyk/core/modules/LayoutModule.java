package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.core.Core;

/**
 * Created by prog on 03.10.2014.
 */
public class LayoutModule {
    public static LinearLayout createLayout(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        ScrollView scrollView=new ScrollView(context);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(context);
        //linearLayout.setGravity(16);
        linearLayout.setLayoutParams(rootParams);
        linearLayout.setTag(data.optJSONObject(0).optLong("id"));
        int styleId = body.optInt("styleid");
        String orientation = body.optString("orientation");
        JSONArray childs = body.optJSONArray("childs");
        if(orientation.equals("vertical")){
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        }else{
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        if(childs != null) {
            if (childs.length() > 0) {
                for (int i = 0; i < childs.length(); i++) {
                    linearLayout.addView(Core.createView(context, childs.optJSONObject(i), data, styles));
                }
            }
        }
        StyleModule.setStyle(context, linearLayout, styles, styleId, actionId);

        return linearLayout;
    }
}
