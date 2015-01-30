package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.support.v4.view.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Alexander on 10.10.2014.
 */
public class ViewPagerModule {
    public static ViewPager createViewPager(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){

        ViewPager viewPager=new ViewPager(context);

        return viewPager;
    }
}
