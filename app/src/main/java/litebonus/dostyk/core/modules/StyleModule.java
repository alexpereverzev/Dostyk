package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import litebonus.dostyk.R;

/**
 * Created by prog on 08.10.2014.
 */
public class StyleModule {

    public static void setStyle(Activity context, View view, JSONArray styles, long styleId, long actionId){
        JSONObject style = findStyle(styles, styleId);
        float d = context.getResources().getDisplayMetrics().density;
        if(style != null) {
            Iterator<String> iterator = style.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                switch (key) {
                    case "backgroundtype": {
                        setBackground(style, view, context, actionId, d);
                        break;
                    }
                    case "margin":{
                        setMargins(style, view, d);
                        break;
                    }
                    case "padding":{
                        setPaddings(style, view, d);
                        break;
                    }
                    case "height":{
                        setHeight(style, view, d);
                        break;
                    }
                    case "width":{
                        setWidth(style, view, d);
                        break;
                    }
                    case "weight":{
                        setWeight(style, view);
                        break;
                    }
                    case "gravity":{
                        setGravity(style, view);
                        break;
                    }
                }
            }
            if(view instanceof TextView){
                setTextViewStyle((TextView) view, style);
            }
        }
    }

    private static JSONObject findStyle(JSONArray styles, long styleId){
        JSONObject style = new JSONObject();
        for(int i = 0; i < styles.length(); i++){
            if(styleId == styles.optJSONObject(i).optLong("id")){
                style = styles.optJSONObject(i);
                break;
            }
        }
        return style;
    }

    private static void setGravity(JSONObject style, View view){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        int gravity = style.optInt("gravity");
        if(view instanceof LinearLayout){
            ((LinearLayout)view).setGravity(gravity);
        }else{
            params.gravity = gravity;
        }
    }

    private static void setWeight(JSONObject style, View view){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        double weiht = style.optDouble("weight");
        params.weight = (float) weiht;
    }

    private static void setWidth(JSONObject style, View view, float d){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        int width = style.optInt("width");


            if (width == -1 || width == -2) {
                params.width = width;
                return;
            }
            params.width = (int) (width * d);

    }

    private static void setHeight(JSONObject style, View view, float d){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        int height = style.optInt("height");
        if(height == -1 || height == -2){
            params.height = height;
            return;
        }
        params.height = (int) (height * d);
    }

    private static void setMargins(JSONObject style, View view, float d){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        String margins = style.optString("margin");
        String[] values = margins.split(",");
        int left = (int)(Integer.parseInt(values[0]) * d);
        int top = (int)(Integer.parseInt(values[1]) * d);
        int right = (int)(Integer.parseInt(values[2]) * d);
        int bottom = (int)(Integer.parseInt(values[3]) * d);
        params.setMargins(left, top, right, bottom);

    }

    private static void setBackground(JSONObject style, View view, Activity context, long actionId, float d){
        String backType = style.optString("backgroundtype");
        StateListDrawable stateListDrawable = new StateListDrawable();
        switch (backType){
            case "solid":{
                view.setBackgroundColor(Color.parseColor(style.optString("background")));
                if(actionId != -1){
                    stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, context.getResources().getDrawable(R.drawable.default_pressed));
                }
                stateListDrawable.addState(new int[]{}, view.getBackground());
                view.setBackground(stateListDrawable);
                break;
            }
            case "gradient":{
                if(actionId != -1){
                    stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, getGradientDrawable(style, d, false));
                }
                stateListDrawable.addState(new int[]{}, getGradientDrawable(style, d, true));
                view.setBackground(stateListDrawable);
                break;
            }
        }
    }

    private static void setPaddings(JSONObject style, View view, float d){
        String margins = style.optString("padding");
        String[] values = margins.split(",");
        int left = (int)(Integer.parseInt(values[0]) * d);
        int top = (int)(Integer.parseInt(values[1]) * d);
        int right = (int)(Integer.parseInt(values[2]) * d);
        int bottom = (int)(Integer.parseInt(values[3]) * d);
        view.setPadding(left, top, right, bottom);
    }

    private static GradientDrawable getGradientDrawable(JSONObject style, float d, boolean flag){
        String colors = style.optString("gradient_color");
        String[] colorsArray = colors.split(",");
        String orientation = style.optString("gradient_orientation");
        GradientDrawable gradientDrawable = new GradientDrawable();
        if(flag) {
            gradientDrawable.setOrientation(GradientDrawable.Orientation.valueOf(orientation));
        }else{
            gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        }
        gradientDrawable.setColors(stringToInt(colorsArray));
        String cornerType = style.optString("corner_type");
        switch (cornerType){
            case "all":{
                int cornerRadius = style.optInt("corner_radius");
                gradientDrawable.setCornerRadius(cornerRadius * d);
                break;
            }
            case "custom":{
                float[] corners = getCorners(style.optString("custom_corners").split(","), d);
                gradientDrawable.setCornerRadii(corners);
                break;
            }
        }
        if(style.has("border_size")){
            int strokeSize = style.optInt("border_size");
            String strokeColor = style.optString("border_color");
            gradientDrawable.setStroke(strokeSize, Color.parseColor(strokeColor));
        }
        return gradientDrawable;
    }

    private static int[] stringToInt(String[] strings){
        int[] colors = new int[strings.length];
        for(int i = 0; i < strings.length; i++){
            colors[i] = Color.parseColor(strings[i]);
        }
        return colors;
    }

    private static float[] getCorners(String[] corners, float d){
        float[] c = new float[8];
        int idx = 0;
        for(int i = 1; i <= c.length; i++){
            c[i - 1] = Float.parseFloat(corners[idx]) * d;
            if(i != 0 && i%2 == 0){
                idx++;
            }
        }
        return c;
    }

    private static void setTextViewStyle(TextView view, JSONObject style){
        Iterator<String> iterator = style.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            switch (key) {
                case "text_size": {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, style.optInt(key));
                    break;
                }
                case "text_color": {
                    view.setTextColor(Color.parseColor(style.optString(key)));
                    break;
                }
                case "text_style":{
                    String textStyle = style.optString(key);
                    switch (textStyle){
                        case "bold":{
                            view.setTypeface(null, Typeface.BOLD);
                            break;
                        }
                    }

                }
            }
        }
    }
}
