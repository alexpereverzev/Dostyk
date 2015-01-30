package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by prog on 09.10.2014.
 */
public class ButtonModule {
    public static Button createButton(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");
        Button button = new Button(context);
        button.setTag(data.optJSONObject(0).optLong("id"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        button.setLayoutParams(params);
        String fieldName = body.optString("fieldname");
        button.setText(data.optJSONObject(0).optString(fieldName));
        StyleModule.setStyle(context, button, styles, styleId, actionId);
        return button;
    }
}
