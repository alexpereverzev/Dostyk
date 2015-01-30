package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.R;
import litebonus.dostyk.core.Core;

/**
 * Created by prog on 08.10.2014.
 */
public class ListViewModule {
    public static LinearLayout createListView(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId) {
        int styleId = body.optInt("styleid");
        // ScrollView scrollView = new ScrollView(context);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //  scrollView.setLayoutParams(rootParams);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(rootParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        if (data.length() == 1) {
            JSONObject res = data.optJSONObject(0);
            if (!res.isNull("share_in_social")) {
                String re = res.optString("share_in_social");
            if (re.length() != 0) {
                    linearLayout.setVisibility(View.GONE);
                }
           }
        }
    //linearLayout.addView(getSeporator(context, 0, 1, 0, 15));
        for (int i = 0; i < data.length(); i++) {
            JSONArray tmpData = new JSONArray();
            tmpData.put(data.optJSONObject(i));
            View v = Core.createView(context, body.optJSONArray("childs").optJSONObject(0), tmpData, styles);
            v.setTag(data.optJSONObject(i).optLong("id"));
            linearLayout.addView(v);

        }
        StyleModule.setStyle(context, linearLayout, styles, styleId, actionId);
        //    scrollView.addView(linearLayout);
        linearLayout.setTag(body.optInt("id"));
        stt = body.optInt("id");
        return linearLayout;
    }

    public static View getSeporator(Activity context, int left, int top, int right, int bottom) {
        View v = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        params.setMargins(left, top, right, bottom);
        v.setBackgroundColor(context.getResources().getColor(R.color.seporator));
        v.setLayoutParams(params);
        return v;
    }

    public static int stt = 0;
}
