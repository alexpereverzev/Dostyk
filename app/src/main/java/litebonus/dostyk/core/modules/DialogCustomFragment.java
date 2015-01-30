package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.app.Dialog;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.core.Core;
import litebonus.dostyk.core.DialogApi;

/**
 * Created by Alexander on 29.10.2014.
 */
public class DialogCustomFragment {

    public static void createDialog(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
      /*  LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        long styleId = body.optLong("styleid");
        rootParams.setMargins(5, 5, 5, 5);


        JSONObject dialog_header = findChild(body, "dialog_header");
        JSONObject dialog_image = findChild(dialog_header, "dialog_image");

        long headStyleId = dialog_header.optLong("styleid");
        JSONObject dialog_content = findChild(body, "dialog_content");
        JSONObject dialog_bottom = findChild(body, "dialog_bottom");
        Bundle b=new Bundle();
        b.putString("dialog_header",dialog_header.toString());
        b.putString("dialog_content",dialog_content.toString());
        b.putString("dialog_bottom",dialog_content.toString());
        b.putString("dialog_image",dialog_image.toString());
        b.putLong("styleid",styleId);
        b.putLong("action",actionId);
        b.putString("data",data.toString());
        b.putString("styles",styles.toString());
         DialogView.getInsance(b);
        */
        Dialog dialog=new Dialog(context);
        ScrollView scrollView=new ScrollView(context);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(context);
        //linearLayout.setGravity(16);
        linearLayout.setLayoutParams(rootParams);
        linearLayout.setTag(data.optJSONObject(0).optLong("id"));
        JSONArray childs = body.optJSONArray("childs");

            linearLayout.setOrientation(LinearLayout.VERTICAL);

        if(childs != null) {
            if (childs.length() > 0) {
                for (int i = 0; i < childs.length(); i++) {
                    linearLayout.addView(Core.createView(context, childs.optJSONObject(i), data, styles));
                }
            }
        }
        //StyleModule.setStyle(context, linearLayout, styles, styleId, actionId);
        dialog.setContentView(linearLayout);

        DialogApi.setDialog(dialog);


        //return new Dialog(context);

    }

    private static JSONObject findChild(JSONObject body, String type){
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
