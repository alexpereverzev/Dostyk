package litebonus.dostyk.interfaces;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import litebonus.dostyk.R;
import litebonus.dostyk.activity.MainActivity;
import litebonus.dostyk.api.API;
import litebonus.dostyk.core.CacheManager;
import litebonus.dostyk.core.Screen;
import litebonus.dostyk.fragments.BaseFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class JavaScriptInterface {


    private Activity activity;
    public JavaScriptInterface(Activity activiy) {
        this.activity = activiy;
    }


    @JavascriptInterface
    public void startDialog(String videoAddress) {
        try {
            JSONObject jsonObject = new JSONObject(videoAddress);
            int screen = jsonObject.getInt("screen");
            int model = jsonObject.getInt("model");
          final   MainActivity act = (MainActivity) activity;
            MainActivity.exit = false;
            act.getCheck().sendMessage(new Message());
            //  act.getBackButton().setVisibility(View.VISIBLE);
            if(CacheManager.getInstance().isScreenCache(""+screen,""+model)){
                CacheManager.getInstance().getScreen(""+screen,""+model,templateCalback);
            }else {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.getMapFragment().stopApi();
                    }
                });
                API.getInstance().getMethods().getDynamicScreen(screen, model, templateCalback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @JavascriptInterface
    public void mapLongTap(final String resorce) {
        try {
         JSONObject jsonObject = new JSONObject(resorce);
           // if(!jsonObject.optBoolean("isEmpty"))
         final String name = jsonObject.optString("name");
         final double x=jsonObject.optJSONObject("point").optDouble("x");
         final double y=jsonObject.optJSONObject("point").optDouble("y");
         final int l=jsonObject.optJSONObject("point").optInt("l");
         final int screen=jsonObject.optInt("screen");
         final int model=jsonObject.optInt("model");
         final String category=jsonObject.optString("category");
         final MainActivity act = (MainActivity) activity;
         final JSONObject send=new JSONObject(act.getMapFragment().EndJson("" + x, "" + y, "" + l));
         send.put("feature",jsonObject);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    act.getMapFragment().showDialog(name,category,send.toString(),screen,model,l+1);//(x,y,l));
                }
            });
           } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void setFloor(final String resorce) {

            int number=Integer.valueOf(resorce)+1;
            final String title="План (Этаж "+number+")";
            final MainActivity act = (MainActivity) activity;



            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    act.changeTitleMap(title);//(x,y,l));
                }
            });


    }

    private Callback<JSONObject> templateCalback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            if(response!=null){
                CacheManager.getInstance().saveScreen(response.getUrl(),jsonObject);
            }
            final MainActivity act = (MainActivity) activity;
            Screen.getInstance().setScreen(jsonObject);
            ((MainActivity) activity).getFragmentManager().beginTransaction().replace(R.id.content_frame, new BaseFragment()).commit();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    act.getMapFragment().startApi();
                    act.getTest().setVisibility(View.GONE);
                    act.getFrameLayout().setVisibility(View.VISIBLE);
                }
            });


        }

        @Override
        public void failure(RetrofitError retrofitError) {
            System.out.print("");
        }
    };

}
