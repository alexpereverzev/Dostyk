package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.R;

/**
 * Created by prog on 08.10.2014.
 */
public class ImageModule {
    public static ImageView createPicture(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        int styleId = body.optInt("styleid");
        float d = context.getResources().getDisplayMetrics().density;
        final ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageParams.setMargins(5,5,5,5);
        imageView.setLayoutParams(imageParams);
        String fieldName = body.optString("fieldname");
      //  String url = data.optJSONObject(0).optString(fieldName);
        String url ="";
        for(int i=0; i<data.length(); i++){
            url = data.optJSONObject(i).optString(fieldName);
            if(!url.equals("")){
                break;
            }
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnFail(R.drawable.ic_logo)
                .cacheOnDisk(true)
                .considerExifParams(true).build();
        ImageLoader.getInstance().displayImage(url, imageView, options);
        StyleModule.setStyle(context, imageView, styles, styleId, actionId);
        return imageView;
    }
}
