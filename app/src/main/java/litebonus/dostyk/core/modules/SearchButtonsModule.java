package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alexander on 10.11.2014.
 */
public class SearchButtonsModule {
    public static LinearLayout createButton(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");

        LinearLayout layout=new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        layout.setLayoutParams(params);


        layout.setTag(data.optJSONObject(0).optLong("id"));

        JSONArray childs = body.optJSONArray("childs");
        layout.setOrientation(LinearLayout.HORIZONTAL);
        if(childs != null) {
            if (childs.length() > 0) {
                for (int i = 0; i < childs.length(); i++) {
                    try {
                        if(childs.optJSONObject(i).getString("type").equals("seacrh_btn_clear")){

                        }



                      //  linearLayout.addView(Core.createView(context, childs.optJSONObject(i), data, styles));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


       // StyleModule.setStyle(context, button, styles, styleId, actionId);
        return layout;
    }
}
