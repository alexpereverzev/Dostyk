package litebonus.dostyk.core.modules;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import litebonus.dostyk.R;
import litebonus.dostyk.activity.MainActivity;
import litebonus.dostyk.api.API;
import litebonus.dostyk.core.Actions;
import litebonus.dostyk.core.Core;
import litebonus.dostyk.core.Screen;
import litebonus.dostyk.fragments.BaseFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alexander on 10.11.2014.
 */
public class SearchModule {
    public static Context con;
    public static MainActivity activity;
    public static EditText editText;
    public static JSONArray data;

    private static TextView status_filter;
    private static TextView status_search;
    private static boolean show_search=false;

    public static LinearLayout createSearch(final Activity context, final JSONObject body, final JSONArray data, JSONArray styles, long actionId) {
        con = context;
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(context);
        //linearLayout.setGravity(16);
        linearLayout.setLayoutParams(rootParams);
        linearLayout.setTag(data.optJSONObject(0).optLong("id"));
        int styleId = body.optInt("styleid");


        String orientation = body.optString("orientation");
        JSONArray childs = body.optJSONArray("childs");
        if (orientation.equals("vertical")) {
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        } else {
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        JSONObject input = findChild(body, "search_input");

        JSONObject search_button = findChild(body, "search_buttons");

        JSONObject clear = findChild(search_button, "search_btn_clear");

        JSONObject find = findChild(search_button, "search_btn_submit");

        JSONObject filter_status=findChild(body,"search_filter_status");

        JSONObject search_status=findChild(body,"search_result_status");


        try {
            editText = EditTextModule.createEditText(context, input, data, styles, input.getInt("id"));
            editText.setSingleLine();
            linearLayout.addView(editText);
            activity = (MainActivity) context;
            SharedPreferences sharedPreferences=activity.getSharedPreferences("template",activity.MODE_PRIVATE);
            int template=sharedPreferences.getInt("id",0);
            final SharedPreferences shared=activity.getSharedPreferences("template"+template,activity.MODE_PRIVATE);

            editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        InputMethodManager in = (InputMethodManager)    activity.getSystemService(Context.INPUT_METHOD_SERVICE);

                        in.hideSoftInputFromWindow(editText
                                        .getApplicationWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        if (!editText.getText().toString().equals("")) {
                            if(!(shared.getBoolean("all",false))){
                                SearchPosition();}
                            else {
                                status_search.setVisibility(View.VISIBLE);
                                show_search=true;

                            }
                        }
                        else {
                            show_search=true;
                            clearScreen();
                            status_search.setVisibility(View.VISIBLE);
                        }
                    }
                    return false;
                }
            });

            LinearLayout linear = new LinearLayout(context);
            linear.setOrientation(LinearLayout.HORIZONTAL);
            Button clear_button = ButtonModule.createButton(context, clear, data, styles, clear.getInt("id"));
            Button submit_button = ButtonModule.createButton(context, find, data, styles, find.getInt("id"));
            submit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    if (!editText.getText().toString().equals("")) {
                        if(!(shared.getBoolean("all",false)))
                            SearchPosition();
                        else  {
                            status_search.setVisibility(View.VISIBLE);
                            show_search=true;
                        }
                          }
                    else {
                        show_search=true;
                        clearScreen();
                        status_search.setVisibility(View.VISIBLE);
                    }
                }
            });

            clear_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setText("");
                    show_search=false;
                    clearScreen();
                }
            });
            linear.addView(clear_button);
            linear.addView(submit_button);
            linearLayout.addView(linear);

            LinearLayout content_status=new LinearLayout(context);
            LinearLayout.LayoutParams root = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            content_status.setLayoutParams(root);
            content_status.setPadding(12,0,0,0);
            content_status.setBackgroundColor(context.getResources().getColor(R.color.white));
            content_status.setOrientation(LinearLayout.VERTICAL);

            status_filter=TextModule.createTextView(context,filter_status,data,styles,filter_status.getInt("id"));
            status_search=TextModule.createTextView(context,search_status,data,styles,search_status.getInt("id"));


            status_filter.setVisibility(View.VISIBLE);
            if(show_search){
                status_search.setVisibility(View.VISIBLE);
                show_search=false;
            }
            else status_search.setVisibility(View.GONE);

            if(checkFilter()) status_filter.setVisibility(View.VISIBLE);
            else status_filter.setVisibility(View.GONE);

            content_status.addView(status_search);
            content_status.addView(status_filter);
            linearLayout.addView(content_status);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        StyleModule.setStyle(context, linearLayout, styles, styleId, actionId);

        return linearLayout;
    }

    public static JSONObject findChild(JSONObject body, String type) {
        JSONObject result = new JSONObject();
        JSONArray childs = body.optJSONArray("childs");
        for (int i = 0; i < childs.length(); i++) {
            JSONObject item = childs.optJSONObject(i);
            if (item.optString("type").equals(type)) {
                result = item;
                return result;
            }
        }
        return result;
    }

    private static Callback<JSONObject> templateCalback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            activity.getMapFragment().startApi();
            JSONObject json = jsonObject.optJSONObject("template");
            JSONObject template = json.optJSONObject("template");
            LinearLayout layout = new LinearLayout(con);
            data = template.optJSONArray("data");
            if(data.length()==0){
                show_search=true;
            }
            JSONObject jsonObject1=Screen.getInstance().getScreen();
            try {
                JSONObject array=   jsonObject1.optJSONArray("templates").getJSONObject(1).getJSONObject("template").put("data", data);

                Screen.getInstance().setScreen(jsonObject1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject body = template.optJSONObject("body");
            JSONArray styles = template.optJSONArray("styles");
            Actions.getInstance().put(template.optJSONArray("actions"));
            layout.addView(Core.createView((MainActivity) con, body, data, styles));
            MainActivity activity = (MainActivity) con;
            FragmentTransaction fragTransaction = activity.getFragmentManager().beginTransaction();
            BaseFragment baseFragment = new BaseFragment();
            baseFragment.setLayout(layout);
            Bundle bundle = new Bundle();
            bundle.putInt("tag", -100);
            bundle.putString("search", editText.getText().toString());
            bundle.putInt("tag_next", ListViewModule.stt);
            baseFragment.setArguments(bundle);
            fragTransaction.replace(R.id.content_frame, baseFragment).commit();
        }

        @Override
        public void failure(RetrofitError retrofitError) {

        }
    };

    public static void clearScreen() {
        if (ListViewModule.stt != 0) {
            FragmentTransaction fragTransaction = activity.getFragmentManager().beginTransaction();
            BaseFragment baseFragment = new BaseFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("tag", ListViewModule.stt);
            baseFragment.setArguments(bundle);
            fragTransaction.replace(R.id.content_frame, baseFragment).commit();
        }
    }

    public static void SearchPosition(){
        activity.getMapFragment().stopApi();
        SharedPreferences sharedPreferences=activity.getSharedPreferences("template",activity.MODE_PRIVATE);
        int template=sharedPreferences.getInt("id",0);
        SharedPreferences shared=activity.getSharedPreferences("template"+template,activity.MODE_PRIVATE);

        JSONArray item=null;
        Map<String, String> options=new LinkedHashMap<>();
        options.put("1[pfield]","name");
        options.put("1[matching]","like");
        options.put("1[vfield]",editText.getText().toString());
        int i=2;
        if(!shared.getString("filter","").isEmpty()){
            try {
                item=new JSONArray(shared.getString("filter",""));
                for(int k=0; k<item.length(); k++){
                    if(item.optJSONObject(k).optBoolean("active")) {
                        options.put("" + i + "[pfield]", item.optJSONObject(k).optString("category"));
                        options.put("" + i + "[matching]", item.optJSONObject(k).optString("matching"));
                        options.put("" + i + "[vfield]", item.optJSONObject(k).optString("name"));
                        i++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        API.getInstance().getMethods().getStaticTemplateSearchDynamic(42, options, templateCalback);
    }



    public static boolean checkFilter(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("template",activity.MODE_PRIVATE);
        int template=sharedPreferences.getInt("id",0);
        if(template==0){
            return false;
        }
        else {
            boolean result=false;
            SharedPreferences shared = activity.getSharedPreferences("template" + template, activity.MODE_PRIVATE);
            try {
                JSONArray item=new JSONArray(shared.getString("filter",""));
                for(int i=0; i<item.length(); i++){
                    if(!item.optJSONObject(i).optBoolean("active")){
                        result=true;
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}
