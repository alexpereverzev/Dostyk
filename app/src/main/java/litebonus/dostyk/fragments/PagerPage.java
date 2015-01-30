package litebonus.dostyk.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.api.API;
import litebonus.dostyk.core.Actions;
import litebonus.dostyk.core.Core;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by prog on 24.10.14.
 */
public class PagerPage extends LinearLayout {
    Activity context;
    int position;
    ScrollView container;
    public PagerPage(Context context) {
        super(context);
        //LayoutInflater.from(context).inflate(R.layout.pager_page, this);

    }

    public PagerPage(final Activity context, int position){
        super(context);
        this.context = context;
        this.position = position;
        container = new ScrollView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.setLayoutParams(params);
        container.setVerticalScrollBarEnabled(false);
        this.addView(container);
        API.getInstance().getMethods().getDynamicScreen(3, position, new Callback<JSONObject>() {
            @Override
            public void success(JSONObject jsonObject, Response response) {
                JSONArray templates = jsonObject.optJSONArray("templates");
                for(int i = 0; i < templates.length(); i++){
                    JSONObject template = templates.optJSONObject(i).optJSONObject("template");
                    JSONArray data = template.optJSONArray("data");
                    JSONObject body = template.optJSONObject("body");
                    JSONArray styles = template.optJSONArray("styles");
                    Actions.getInstance().put(template.optJSONArray("actions"));
                    container.addView(Core.createView(context, body, data, styles));
                }
            }


            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
