package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import litebonus.dostyk.R;
import litebonus.dostyk.core.CacheManager;
import litebonus.dostyk.fragments.MapFragment;
import litebonus.dostyk.interfaces.ServerMethods;
import litebonus.dostyk.views.Picker;


import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import litebonus.dostyk.activity.MainActivity;
import litebonus.dostyk.api.API;
import litebonus.dostyk.core.Screen;

/**
 * Created by prog on 08.10.2014.
 */
public class ActionModule {



    static boolean update=false;

    public static Picker getPicker() {
        return picker;
    }

    static Picker picker;

    public static void setUpdate(boolean update) {
        ActionModule.update = update;
    }

    public static boolean isUpdate() {
        return update;
    }

    public static void setAction(final Activity context, final View view, final JSONObject actionParams) {
        final String action = actionParams.optString("type");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof Picker) {
                    ((Picker) v).click();
                    picker=(Picker) v;
                }
                update=false;
                switch (action) {

                    case "navigate": {
                        MainActivity activity=(MainActivity) context;
                        MainActivity.exit=false;
                        String destination = actionParams.optString("destination");
                        if (destination.equals("btsettings")) {
                            BTSettings(context);
                        } else if (destination.equals("screen_by_id")) {
                            long destinationId = actionParams.optLong("destination_screen_id");
                            long dataId = Long.parseLong(v.getTag().toString());
                            if(Screen.getInstance().getScreen().length()!=0)
                            ((MainActivity) context).getBackStack().push(Screen.getInstance().getScreen());
                            if(CacheManager.getInstance().isScreenCache(""+destinationId,""+dataId)){
                                CacheManager.getInstance().getScreen(""+destinationId,""+dataId,((MainActivity) context).getDynamicTemplateCallback());
                            }else {
                                activity.getMapFragment().stopApi();
                                API.getInstance().getMethods().getDynamicScreen(destinationId, dataId, ((MainActivity) context).getDynamicTemplateCallback());
                            }
                        }
                        else if (destination.equals("screen_by_device_id")) {
                            update=true;
                            activity.getMapFragment().stopApi();
                            long destinationId = actionParams.optLong("destination_screen_id");
                            long dataId = Long.parseLong(v.getTag().toString());
                           // if(Screen.getInstance().getScreen().length()!=0)
                          //  ((MainActivity) context).getBackStack().push(Screen.getInstance().getScreen());
                            Map<String, String> options = new LinkedHashMap<>();
                            options.put("1[pfield]", "device_id");
                            options.put("1[matching]", "=");
                            options.put("1[vfield]", "" + activity.getDevice_id());
                            API.getInstance().getMethods().getDynamicScreenDeviceId(destinationId, options, ((MainActivity) context).getTemplateCalback());
                        } else if (destination.equals("screen_by_params")) {
                            if(picker!=null) picker.reset();
                            long destinationId = actionParams.optLong("destination_screen_id");
                            long dataId = Long.parseLong(v.getTag().toString());
                            if(Screen.getInstance().getScreen().length()!=0)
                            ((MainActivity) context).getBackStack().push(Screen.getInstance().getScreen());
                            Map<String, String> options = new LinkedHashMap<>();
                            JSONObject object = Screen.getInstance().getScreen();
                            JSONArray array = object.optJSONArray("templates").optJSONObject(0).optJSONObject("template").optJSONArray("data");
                            options.put("1[pfield]", actionParams.optJSONArray("params").optJSONObject(0).optString("pfield"));
                            options.put("1[matching]", actionParams.optJSONArray("params").optJSONObject(0).optString("matching"));
                            options.put("1[vfield]", array.optJSONObject(0).optString(actionParams.optJSONArray("params").optJSONObject(0).optString("vfield")));
                            API.getInstance().getMethods().getDynamicScreenDeviceId(destinationId, options, ((MainActivity) context).getDynamicTemplateCallback());
                        }
                        else {
                            long destinationId = actionParams.optLong("destination_screen_id");
                            //  ((MainActivity) context).getBackStack().clear();

                            if(CacheManager.getInstance().isScreenCache(""+destinationId)){
                                CacheManager.getInstance().getScreen(""+destinationId,((MainActivity) context).getTemplateCalback());
                            }else {
                                activity.getMapFragment().stopApi();
                                API.getInstance().getMethods().getStaticScreen(destinationId, ((MainActivity) context).getTemplateCalback());
                            }

                        }
                        break;
                    }
                    case "save":

                    {
                        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences("template", context.MODE_PRIVATE).edit();
                        sharedPreferences.putBoolean("test", true).commit();
                        int template_id = actionParams.optInt("destination_template_id");
                        JSONObject object = Screen.getInstance().getScreen();
                        JSONArray jsonArray = object.optJSONArray("templates");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int res = jsonArray.optJSONObject(i).optInt("id");
                            if (res == template_id) {
                                JSONArray array = jsonArray.optJSONObject(i).optJSONObject("template").optJSONArray("data");
                                int order_picker = (Integer) view.getTag();
                                for (int y = 0; y < array.length(); y++) {
                                    if (order_picker == (array.optJSONObject(y).optInt("id"))) {
                                        SharedPreferences pref1 = context.getSharedPreferences("template", context.MODE_PRIVATE);
                                        SharedPreferences.Editor prefEditor1 = pref1.edit();
                                        prefEditor1.putInt("id", template_id).commit();
                                        SharedPreferences pref = context.getSharedPreferences("template" + template_id, context.MODE_PRIVATE);
                                        SharedPreferences.Editor prefEditor = pref.edit();
                                        boolean flag=false;
                                        JSONArray item=null;
                                        if(pref.getString("filter","").isEmpty()){
                                            item=new JSONArray();
                                            JSONObject filter=new JSONObject();
                                            try {
                                                filter.put("name",array.optJSONObject(y).optString("name"));
                                                filter.put("matching","=");
                                                filter.put("category","category");
                                                filter.put("active",false);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else {
                                            try {
                                                item=new JSONArray(pref.getString("filter",""));
                                                for(int k=0; k<item.length(); k++){
                                                    JSONObject filter=  item.optJSONObject(k);
                                                    if(array.optJSONObject(y).optString("name").equals(filter.optString("name"))){
                                                        flag=!item.optJSONObject(k).optBoolean("active");
                                                        item.optJSONObject(k).put("active",flag);

                                                        break;
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        if(flag)prefEditor.putBoolean("all",false);
                                        prefEditor.putBoolean("selected" + order_picker, flag);
                                        prefEditor.putString("filter",item.toString());
                                        prefEditor.commit();

                                    }
                                }

                            }
                        }

                        break;
                    }

                    case "traceroute" : {
                        if (picker != null) picker.reset();
                        MainActivity.exit = true;
                        MainActivity activity = (MainActivity) context;
                        activity.getBackStack().clear();
                        activity.getMapFragment().getClear().setVisibility(View.VISIBLE);
                        activity.setTitle("План");
                        activity.getBackButton().setVisibility(View.GONE);
                        FragmentManager manager = activity.getFragmentManager();
                        MapFragment map = (MapFragment) manager.findFragmentById(R.id.headlines_fragment);
                        JSONArray array = Screen.getInstance().getScreen().optJSONArray("templates").optJSONObject(0).optJSONObject("template").optJSONArray("data");
                        double x = 0, y = 0;
                        int l = 0;
                        for (int i = 0; i < array.length(); i++) {
                            if (!array.optJSONObject(i).isNull("x")) {
                                x = array.optJSONObject(i).optDouble("x");
                            }
                            if (!array.optJSONObject(i).isNull("y")) {
                                y = array.optJSONObject(i).optDouble("y");
                            }
                            if (!array.optJSONObject(i).isNull("l")) {
                                l = array.optJSONObject(i).optInt("l");
                            }
                        }
                        String name = array.optJSONObject(0).optString("name");
                        map.onFindRoute(x, y, l, name);
                        activity.getTest().setVisibility(View.VISIBLE);
                        activity.getFrameLayout().setVisibility(View.GONE);
                        break;
                    }
                    case "switchpickers" : {
                        String destination = actionParams.optString("destination");
                        int template_id = actionParams.optInt("destination_template_id");
                        if (template_id == 0) template_id = 32;
                        String value = "";
                        String pfield = "";
                        JSONArray opt = actionParams.optJSONArray("params");
                        for (int i = 0; i < opt.length(); i++) {
                            value = opt.optJSONObject(i).optString("value");
                            pfield = opt.optJSONObject(i).optString("pfield");
                        }
                        if (destination.equals("check_off")) {
                            PickerModule.Bunch.getInstance().resetBunch();
                            saveFilter(context, template_id, false);

                        } else if (destination.equals("check_on")) {
                            PickerModule.Bunch.getInstance().checkOnBunch();
                            saveFilter(context, template_id, true);
                        }


                        break;
                    }

                    case "reset":{
                        MainActivity activity = (MainActivity) context;
                        JSONObject input = new JSONObject();
                        try {
                            input.put("application_id", ServerMethods.Application);
                            input.put("device_type", "android");
                            input.put("device_id",activity.getDevice_id());
                            input.put("device_token", activity.getRegid());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        activity.getmConsumer().sendClear_message(input);
                        break;}
                }
                DrawerLayout drawerLayout = ((MainActivity) context).getmDrawerLayout();
                LinearLayout scrollView = ((MainActivity) context).getDrawerView();
                View menuButton = ((MainActivity) context).getMenuButton();
                menuButton.setFocusableInTouchMode(false);
                menuButton.clearFocus();
                if (drawerLayout.isDrawerOpen(scrollView)) {
                    drawerLayout.closeDrawer(scrollView);
                }
            }
        });
    }

    private static void BTSettings(Activity context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void saveFilter(Context context, int template_id, boolean flag_input){
        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences("template", context.MODE_PRIVATE).edit();
        sharedPreferences.putBoolean("test", true).commit();

        JSONObject object = Screen.getInstance().getScreen();
        JSONArray jsonArray = object.optJSONArray("templates");
        for (int i = 0; i < jsonArray.length(); i++) {
            int res = jsonArray.optJSONObject(i).optInt("id");
            if (res == template_id) {

                SharedPreferences pref1 = context.getSharedPreferences("template", context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor1 = pref1.edit();
                prefEditor1.putInt("id", template_id).commit();
                SharedPreferences pref = context.getSharedPreferences("template" + template_id, context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = pref.edit();
                JSONArray array = jsonArray.optJSONObject(i).optJSONObject("template").optJSONArray("data");
                JSONArray item=new JSONArray();
                for (int y = 0; y < array.length(); y++) {
                    JSONObject filter=new JSONObject();
                    try {
                        filter.put("name",array.optJSONObject(y).optString("name"));
                        filter.put("active",flag_input);
                        filter.put("matching","=");
                        filter.put("category","category");
                        item.put(filter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    prefEditor.putBoolean("all",!flag_input);
                }
                prefEditor.putString("filter", item.toString());
                prefEditor.commit();

            }
        }
    }

}
