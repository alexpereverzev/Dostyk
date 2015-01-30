package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Alexander on 10.11.2014.
 */
public class EditTextModule {
    public static EditText createEditText(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        EditText edit = new EditText(context);
        edit.setLayoutParams(params);
        String fieldName = body.optString("fieldname");
        String dat1a=data.optJSONObject(0).optString(fieldName);
        edit.setHintTextColor(Color.BLACK);
        edit.setHint("Поиск");
       // edit.setText("Поиск");
     //   StyleModule.setStyle(context, edit, styles, styleId,actionId);
        return edit;
    }
}
