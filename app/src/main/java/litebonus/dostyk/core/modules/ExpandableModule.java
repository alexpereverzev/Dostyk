package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.views.ExpandableTextView;

/**
 * Created by prog on 21.10.14.
 */
public class ExpandableModule {
    public static ExpandableTextView createExpandable(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ExpandableTextView expandableTextView = new ExpandableTextView(context, null);
        long styleId = body.optLong("styleid");
        rootParams.setMargins(5, 5, 5, 5);
        expandableTextView.setLayoutParams(rootParams);
        JSONObject expandable_header = findChild(body, "expandable_header");
        long headStyleId = expandable_header.optLong("styleid");
        JSONObject expandable_content = findChild(body, "expandable_content");
        JSONObject expandable_collapse = findChild(expandable_header, "expandable_collapse");
        JSONObject expandable_expand = findChild(expandable_header, "expandable_expand");
        JSONObject expandable_title_delimiter = findChild(expandable_header, "expandable_title_delimiter");
        JSONObject expandable_title = findChild(expandable_header, "expandable_title");

        expandableTextView.setPassive(LayoutModule.createLayout(context, expandable_collapse, data, styles, -1));
        expandableTextView.setActive(LayoutModule.createLayout(context, expandable_expand, data, styles, -1));
        expandableTextView.setDelimitorContainer(LayoutModule.createLayout(context, expandable_title_delimiter, data, styles, -1));
        expandableTextView.setTitleTextContainer(LayoutModule.createLayout(context, expandable_title, data, styles, -1));
        LinearLayout layout = LayoutModule.createLayout(context, expandable_content, data, styles, -1);
        expandableTextView.setExpandContent(layout);
        StyleModule.setStyle(context, expandableTextView.getTitleContainer(), styles, headStyleId, -1);
        StyleModule.setStyle(context, expandableTextView, styles, styleId, actionId);
        return expandableTextView;
    }

    public static JSONObject findChild(JSONObject body, String type){
        JSONObject result = new JSONObject();
        JSONArray childs = body.optJSONArray("childs");
        for (int i = 0; i < childs.length(); i++){
            JSONObject item = childs.optJSONObject(i);
            if(item.optString("type").equals(type)){
                result = item;
                return result;
            }
        }
        return result;
    }
}
