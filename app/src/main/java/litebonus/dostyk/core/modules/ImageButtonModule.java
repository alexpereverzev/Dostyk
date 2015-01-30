package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by prog on 09.10.2014.
 */
public class ImageButtonModule {
    public static ImageButton createButton(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");
        final ImageButton button = new ImageButton(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        button.setLayoutParams(params);
        button.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String fieldName = body.optString("fieldname");
        String url = data.optJSONObject(0).optString(fieldName);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();
        ImageLoader.getInstance().displayImage(url, button, options);
        StyleModule.setStyle(context, button, styles, styleId, actionId);
        return button;
    }
}
