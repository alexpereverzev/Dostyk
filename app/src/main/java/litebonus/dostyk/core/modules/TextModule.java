package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by prog on 08.10.2014.
 */
public class TextModule {
    public static TextView createTextView(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        TextView textView = new TextView(context);
        textView.setLayoutParams(params);
        String fieldName = body.optString("fieldname");
        textView.setText(Html.fromHtml(data.optJSONObject(0).optString(fieldName)));
        StyleModule.setStyle(context, textView, styles, styleId,actionId);
        return textView;
    }
}
