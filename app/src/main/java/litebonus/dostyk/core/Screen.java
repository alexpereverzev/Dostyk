package litebonus.dostyk.core;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.activity.MainActivity;


/**
 * Created by prog on 23.10.14.
 */
public class Screen {
    private static Screen instance;
    private JSONObject screen;
    private JSONArray templates;

    private Screen() {
        screen = new JSONObject();
        templates = new JSONArray();
    }

    public static Screen getInstance() {
        if (instance == null) {
            instance = new Screen();
        }
        return instance;
    }

    public void setScreen(JSONObject screen) {
        this.screen = screen;
        templates = screen.optJSONArray("templates");
    }

    public View createView(Activity context) {
        String type = screen.optString("type");
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setTitle(screen.optString("name"));
        Views.getInstance().resetInstance();
        switch (type) {
            case "pager": {
                String screenId = screen.optString("id");
                String id = screen.optString("modelId");
                String[] ids = getIds(screen.optJSONArray("dsids"));
                ViewPager pager = mainActivity.getViewPager();
                pager.setVisibility(View.VISIBLE);
                mainActivity.getFrameLayout().setVisibility(View.GONE);
                //      DynamicPagerAdapter pagerAdapter = new DynamicPagerAdapter(ids, context, screenId);
                //   pager.setAdapter(pagerAdapter);
                //   pager.setCurrentItem(findPosition(id, ids));
                break;
            }
            case "plain": {

                mainActivity.getFrameLayout().setVisibility(View.VISIBLE);
                mainActivity.getViewPager().setVisibility(View.GONE);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                for (int i = 0; i < templates.length(); i++) {
                    JSONObject template = templates.optJSONObject(i).optJSONObject("template");
                    JSONArray data = template.optJSONArray("data");
                    JSONObject body = template.optJSONObject("body");
                    JSONArray styles = template.optJSONArray("styles");
                    Actions.getInstance().put(template.optJSONArray("actions"));
                    layout.addView(Core.createView(context, body, data, styles));
                }
                return layout;
            }
        }
        return new View(context);
    }

    public JSONObject getScreen() {
        return screen;
    }

    private String[] getIds(JSONArray ids) {
        String[] result = new String[ids.length()];
        for (int i = 0; i < ids.length(); i++) {
            result[i] = ids.optString(i);
        }
        return result;
    }

    private int findPosition(String id, String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(id)) {
                return i;
            }
        }
        return 0;
    }
}
