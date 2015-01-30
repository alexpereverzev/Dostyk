package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Alexander on 17.12.2014.
 */
public class AppVersion {
    public static TextView createAppVersion(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        TextView textView = new TextView(context);
        textView.setLayoutParams(params);
        DateFormat format = new DateFormat();
       // format.format("dd.MM.yy.hh.mm", new Date());
       // textView.setText("Версия приложения 01.67.02.5bf04e420." + format);
        textView.setText("Версия приложения 01.71.02.c9c0dbf42." + format.format("dd.MM.yy.H.mm", new Date(1422020972649L)));
        StyleModule.setStyle(context, textView, styles, styleId,actionId);
        return textView;
    }
}
