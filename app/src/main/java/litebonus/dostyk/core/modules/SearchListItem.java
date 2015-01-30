package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import litebonus.dostyk.core.Core;

/**
 * Created by Alexander on 11.11.2014.
 */
public class SearchListItem {
    public static View createLayout(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(context);
        //linearLayout.setGravity(16);

        View v = null;
        JSONObject content = findChild(body, "listitem_content");


        JSONObject listitem_action_group = findChild(body, "listitem_action_group");

        JSONObject listitem_type = findChild(body, "listitem_type");

        linearLayout.setLayoutParams(rootParams);

        String orientation = body.optString("orientation");

        if (orientation.equals("vertical")) {
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        } else {
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        }

        try {
            String type_action = data.getJSONObject(0).optString("action_type");
            JSONArray childs_action = listitem_action_group.optJSONArray("childs");
            if(childs_action!=null)
            for (int i = 0; i < childs_action.length(); i++) {
                JSONObject action = childs_action.getJSONObject(i);
                String act = action.getString("action_type");
                if (type_action.equals(act)) {
                    actionId = action.getLong("actionid");


                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(content.toString());
            jsonObject.put("actionid", actionId);
            jsonObject.put("type", "layout");
            jsonObject.put("orientation", "vertical");
            jsonObject.put("styleid", 31);
            JSONArray childs = content.optJSONArray("childs");
            v = Core.createView(context, jsonObject, data, styles);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!content.isNull("orientation")) {
            LinearLayout li = (LinearLayout) v;
            if (content.optString("orientation").equals("vertical")) {
                li.setOrientation(LinearLayout.VERTICAL);
            } else li.setOrientation(LinearLayout.HORIZONTAL);

        }

        //  linearLayout.addView(Core.createView(context, content, data, styles));

      /*  int styleId = body.optInt("styleid");



        if(childs != null) {
            if (childs.length() > 0) {
                for (int i = 0; i < childs.length(); i++) {
                    try {
                        linearLayout.addView(Core.createView(context, childs.optJSONObject(i).put("actionid",actionId), data, styles));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }



        StyleModule.setStyle(context, linearLayout, styles, styleId, actionId);*/

        return v;
    }


    public static JSONObject findChild(JSONObject body, String type) {
        JSONObject result = new JSONObject();
        JSONArray childs = body.optJSONArray("childs");
        for (int i = 0; i < childs.length(); i++) {
            JSONObject item = childs.optJSONObject(i);
            if (item.optString("type").equals(type)) {
                result = item;
                return result;
            }
        }
        return result;
    }
}
