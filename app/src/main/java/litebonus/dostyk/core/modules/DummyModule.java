package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by prog on 16.10.14.
 */
public class DummyModule {
    public static View createDummy(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        View dummy = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 3, 0, 3);
        dummy.setLayoutParams(params);
        int styleId = body.optInt("styleid");
        StyleModule.setStyle(context, dummy, styles, styleId, actionId);
        return dummy;
    }
}
