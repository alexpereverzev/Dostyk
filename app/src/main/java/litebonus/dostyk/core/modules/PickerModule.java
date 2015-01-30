package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import litebonus.dostyk.core.Screen;
import litebonus.dostyk.core.Views;
import litebonus.dostyk.views.Picker;

/**
 * Created by prog on 29.10.14.
 */
public class PickerModule {


    public static Picker createPicker(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId) {
        String id = body.optString("id");
        int order = body.optInt("");
        int groupId = body.optInt("group_number", 0);
        int bunch_number=body.optInt("bunch_number",0);
        long styleId = body.optLong("styleid");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Picker picker = new Picker(context, groupId);
        picker.setTag(data.optJSONObject(0).optInt("id"));
        picker.setLayoutParams(params);
        JSONObject picker_active = ExpandableModule.findChild(body, "picker_active");
        JSONObject picker_passive = ExpandableModule.findChild(body, "picker_passive");
        JSONObject picker_default = ExpandableModule.findChild(body, "picker_default");
        String field_name = picker_default.optString("fieldname");
        boolean isActive = data.optJSONObject(0).optBoolean(field_name);
        isActive=checkSave(context, isActive, data.optJSONObject(0).optInt("id"));
        picker.setActive(LayoutModule.createLayout(context, picker_active, data, styles, -1));
        picker.setPassive(LayoutModule.createLayout(context, picker_passive, data, styles, -1));
        StyleModule.setStyle(context, picker, styles, styleId, actionId);
        if (groupId > 0) {
            Grouping.getInstance().getPickerList().add(picker);
        }
        if (isActive) {
            Views.getInstance().getAfterLoadList().add(picker);
        }
        if(bunch_number>0){
            Bunch.getInstance().getPickerList().add(picker);
        }
        return picker;
    }

    public static class Grouping {
        private List<Picker> pickerList;
        public static Grouping instance;

        private Grouping() {
            pickerList = new ArrayList<>();
        }

        public static Grouping getInstance() {
            if (instance == null) {
                instance = new Grouping();
            }
            return instance;
        }

        public void resetGroup(long groupId) {
            for (Picker picker : pickerList) {
                if (picker.getGroupId() == groupId) {
                    picker.reset();
                }
            }
        }

        public List<Picker> getPickerList() {
            return pickerList;
        }
    }



    public static boolean checkSave(Context context, boolean active, int id) {
       // int template_id = actionParams.optInt("destination_template_id");
        int template_id = 32;

        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("template", context.MODE_PRIVATE);
        JSONObject object = Screen.getInstance().getScreen();
        if (!sharedPreferences.getBoolean("test", false)) {
            if (object.length() != 0) {
                JSONArray jsonArray = object.optJSONArray("templates");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int res = jsonArray.optJSONObject(i).optInt("id");
                        if (res == template_id) {
                            JSONArray array = jsonArray.optJSONObject(i).optJSONObject("template").optJSONArray("data");
                            int order_picker = id;
                            for (int y = 0; y < array.length(); y++) {
                                if (order_picker == (array.optJSONObject(y).optInt("id"))) {
                                    SharedPreferences pref1 = context.getSharedPreferences("template", context.MODE_PRIVATE);
                                    SharedPreferences.Editor prefEditor1 = pref1.edit();
                                    prefEditor1.putInt("id", template_id).commit();
                                    SharedPreferences pref = context.getSharedPreferences("template" + res, context.MODE_PRIVATE);
                                    SharedPreferences.Editor prefEditor = pref.edit();
                                    JSONArray item = null;
                                    if (pref.getString("filter", "").isEmpty()) {
                                        item = new JSONArray();
                                        JSONObject filter = new JSONObject();
                                        try {
                                            filter.put("name", array.optJSONObject(y).optString("name"));
                                            filter.put("active", active);
                                            filter.put("matching", "=");
                                            filter.put("category", "category");
                                            item.put(filter);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            item = new JSONArray(pref.getString("filter", ""));
                                            JSONObject filter = new JSONObject();
                                            filter.put("name", array.optJSONObject(y).optString("name"));
                                            filter.put("active", active);
                                            filter.put("matching", "=");
                                            filter.put("category", "category");
                                            item.put(filter);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    prefEditor.putString("filter", item.toString());
                                    prefEditor.commit();
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        } else {
            if (object.length() != 0) {
                JSONArray jsonArray = object.optJSONArray("templates");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int temp = jsonArray.optJSONObject(i).optInt("id");
                        if (temp == template_id) {
                            JSONArray array = jsonArray.optJSONObject(i).optJSONObject("template").optJSONArray("data");
                            sharedPreferences = context.getSharedPreferences("template" + temp, context.MODE_PRIVATE);
                            try {
                                JSONArray item = new JSONArray(sharedPreferences.getString("filter", ""));
                                for (int y = 0; y < item.length(); y++) {
                                    if (id == (array.optJSONObject(y).optInt("id"))) {
                                        active = item.optJSONObject(y).optBoolean("active");
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return active;
    }


    public static class Bunch {
        private List<Picker> pickerList;
        public static Bunch instance;

        private Bunch() {
            pickerList = new ArrayList<>();
        }

        public static Bunch getInstance() {
            if (instance == null) {
                instance = new Bunch();
            }
            return instance;
        }

        public void resetBunch() {
            if(!pickerList.isEmpty())
            for (Picker picker : pickerList) {
                    picker.click();
                    picker.reset();

            }
        }

        public void checkOnBunch() {
            if(!pickerList.isEmpty())
            for (Picker picker : pickerList) {
                    picker.click();
                    picker.activate();

            }
        }

        public List<Picker> getPickerList() {
            return pickerList;
        }
    }

}
