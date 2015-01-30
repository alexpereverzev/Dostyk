package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.core.Core;
import litebonus.dostyk.views.Pie;

/**
 * Created by prog on 27.10.14.
 */
public class PieModule {
    public static Pie createPie(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Pie pie = new Pie(context);
        pie.setLayoutParams(params);
        for (int i = 0; i < data.length(); i++){
            JSONObject item = data.optJSONObject(i);
            JSONArray tmpData = new JSONArray();
            tmpData.put(item);
            View v = Core.createView(context, body.optJSONArray("childs").optJSONObject(0), tmpData, styles);
            v.setTag(data.optJSONObject(i).optString("id"));
            pie.putView(v);

        }
        pie.setTag(body.optString("id"));
        StyleModule.setStyle(context, pie, styles, styleId, actionId);

        return pie;
    }
}
