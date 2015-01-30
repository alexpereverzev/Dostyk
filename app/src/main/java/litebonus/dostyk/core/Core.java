package litebonus.dostyk.core;

import android.app.Activity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.core.modules.ActionModule;
import litebonus.dostyk.core.modules.AppVersion;
import litebonus.dostyk.core.modules.ButtonModule;
import litebonus.dostyk.core.modules.DummyModule;
import litebonus.dostyk.core.modules.EditTextModule;
import litebonus.dostyk.core.modules.ExpandableModule;
import litebonus.dostyk.core.modules.ImageButtonModule;
import litebonus.dostyk.core.modules.ImageModule;
import litebonus.dostyk.core.modules.LayoutModule;
import litebonus.dostyk.core.modules.ListViewModule;
import litebonus.dostyk.core.modules.PickerModule;
import litebonus.dostyk.core.modules.PieModule;
import litebonus.dostyk.core.modules.SearchButtonsModule;
import litebonus.dostyk.core.modules.SearchListItem;
import litebonus.dostyk.core.modules.SearchModule;
import litebonus.dostyk.core.modules.ShareButtonModule;
import litebonus.dostyk.core.modules.TextModule;
import litebonus.dostyk.core.modules.ViewPagerModule;
import litebonus.dostyk.core.modules.WebViewModule;

/**
 * Created by prog on 02.10.2014.
 */
public class Core {


    public static View createView(Activity context, JSONObject body, JSONArray data, JSONArray styles) {

        String type = body.optString("type");
        long actionId = body.optLong("actionid", -1);
        final View view;


        switch (type) {
            case "layout": {
                view = LayoutModule.createLayout(context, body, data, styles, actionId);
                break;
            }
            case "picture": {
                view = ImageModule.createPicture(context, body, data, styles, actionId);
                break;
            }
            case "text": {
                view = TextModule.createTextView(context, body, data, styles, actionId);
                break;
            }
            case "listview": {
                view = ListViewModule.createListView(context, body, data, styles, actionId);
                break;
            }
            case "button": {
                view = ButtonModule.createButton(context, body, data, styles, actionId);
                break;
            }
            case "imagebutton": {
                view = ImageButtonModule.createButton(context, body, data, styles, actionId);
                break;
            }
            case "expandable": {
                view = ExpandableModule.createExpandable(context, body, data, styles, actionId);
                break;
            }
            case "horizontal_slider": {
                view = null;
                break;
            }
            case "pager": {
                view = ViewPagerModule.createViewPager(context, body, data, styles, actionId);
                break;
            }
            case "webview": {
                view = WebViewModule.createWebView(context, body, data, styles, actionId);
                break;
            }
            case "dummy": {
                view = DummyModule.createDummy(context, body, data, styles, actionId);
                break;
            }

            case "pie": {
                view = PieModule.createPie(context, body, data, styles, actionId);
                break;
            }

            case "picker": {
                view = PickerModule.createPicker(context, body, data, styles, actionId);
                break;
            }
            case "sharebutton": {
                view = ShareButtonModule.createShareButton(context, body, data, styles, actionId);
                break;
            }
            case "search_bar": {
                view = SearchModule.createSearch(context, body, data, styles, actionId);
                break;
            }
            case "search_input": {
                view = EditTextModule.createEditText(context, body, data, styles, actionId);
                break;
            }

            case "search_buttons": {
                view = SearchButtonsModule.createButton(context, body, data, styles, actionId);
                break;
            }
            case "listitem": {
                view = SearchListItem.createLayout(context, body, data, styles, actionId);
                break;
            }
            case "app_version": {
                view = AppVersion.createAppVersion(context, body, data, styles, actionId);
                break;
            }

            default: {
                view = new View(context);
            }
        }


        if (!body.isNull("visibilty")) {
            view.setVisibility(View.GONE);
        }
        if (actionId != -1) {
            JSONObject action = findAction(actionId);
            ActionModule.setAction(context, view, action);
        }
        Views.getInstance().getViewList().add(view);
        return view;
    }

    public static JSONObject findAction(long actionId) {
        JSONObject action = new JSONObject();
        for (int i = 0; i < Actions.getInstance().getActions().length(); i++) {
            JSONObject item = Actions.getInstance().getActions().optJSONObject(i);
            if (item.optLong("id") == actionId) {
                return item;
            }
        }
        return action;
    }

}
