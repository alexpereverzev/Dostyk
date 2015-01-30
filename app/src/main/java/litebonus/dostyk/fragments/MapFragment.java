package litebonus.dostyk.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import litebonus.dostyk.R;
import litebonus.dostyk.activity.MainActivity;
import litebonus.dostyk.api.API;
import litebonus.dostyk.core.CacheManager;
import litebonus.dostyk.core.Screen;
import litebonus.dostyk.helper.Distance;
import litebonus.dostyk.helper.Helper;
import litebonus.dostyk.interfaces.JavaScriptInterface;
import litebonus.dostyk.interfaces.ServerMethods;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.ibecom.api.IBApi;
import ru.ibecom.api.exceptions.IBError;
import ru.ibecom.api.interfaces.IBLocation;
import ru.ibecom.api.interfaces.IBLocationManager;
import ru.ibecom.api.interfaces.IBTimedLocation;
import ru.ibecom.api.interfaces.IBZone;
import ru.ibecom.api.interfaces.IBZoneManager;


public class MapFragment extends Fragment implements IBZoneManager.SingleZoneMonitoringConsumer, IBLocationManager.Consumer {
    private Handler handler;
    private Handler lost_hander;
    private WebView webView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Client client;
    private IBZoneManager zoneManager;
    private IBZone zone;
    private double x = -100;
    private double y = -100;
    private double n;
    private List<String> zones;
    private MainActivity act;
    private long time_last = 1415175273;
    private ImageView clear;
    private Button clear_route;
    private static boolean position = false;
    private IBLocationManager locationManager;
    private String current = "";
    private long time;
    private Dialog dia;
    private String jsonFrom;
    private String jsonTo;
    private JSONObject style_route;
    private int l;
    private double raw_y = 0.0;
    private double raw_x = 0.0;
    private JSONObject fo;
    private JSONObject to;
    private MainActivity activity;
    private double route_x_end;
    private double route_y_end;
    private boolean show_end_dialog=false;

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public ImageView getClear() {
        return clear;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            style_route = new JSONObject();
            style_route.put("color", "red");
            style_route.put("dashArray", "15,15");
            style_route.put("className","blink-path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        activity = (MainActivity) getActivity();
        client = new Client();
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        webView = (WebView) view.findViewById(R.id.web_map);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                StringBuilder builder = new StringBuilder();
                builder.append("X = ");
                builder.append(event.getX());
                builder.append(" Y = ");
                builder.append(event.getY());
                builder.append(" RawX = ");
                builder.append(event.getRawX());
                builder.append(" RawY = ");
                builder.append(event.getRawY());
                raw_x = event.getX();
                raw_y = event.getY();
                Log.d("Coord", builder.toString());
                return false;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        JavaScriptInterface jsInterface = new JavaScriptInterface(getActivity());
        webView.addJavascriptInterface(jsInterface, "JSInterface");
        webView.loadUrl("file:///android_asset/map/index.html");
        act = (MainActivity) getActivity();
        webView.setWebViewClient(client);
        //  webView.setWebChromeClient(new Client1());
        clear = (ImageView) view.findViewById(R.id.clear);
        clear_route = (Button) view.findViewById(R.id.clear_button);
        clear_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.onLoadResource(webView, "javascript:clearRouteLayer()");
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
            }
        });

        parent = (LinearLayout) view.findViewById(R.id.parent_menu);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("");
            }
        });
        minimize = (Button) view.findViewById(R.id.minimize);
        reset_route = (Button) view.findViewById(R.id.reset_route);
        route_position = (ImageView) view.findViewById(R.id.current_position);
        clear_here = (ImageView) view.findViewById(R.id.clear_here);
        address_from = (TextView) view.findViewById(R.id.address_from);
        address_here = (TextView) view.findViewById(R.id.address_here);
        route_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((jsonTo != null) && (x != (-100))) {
                    jsonFrom = null;
                    client.onLoadResource(webView, "javascript:clearRouteLayer()");
                    address_from.setText("Текущее местоположение");
                    client.onLoadResource(webView, "javascript:findRouteFromCurrentPosition(" + jsonTo + "," + style_route + ")");

                }

            }
        });
        clear_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address_here.setText("");
                jsonTo = null;
            }
        });
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setVisibility(View.GONE);
                clear.setVisibility(View.VISIBLE);
            }
        });
        reset_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRoute();

            }
        });
        //  initializeConsumersApi();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
               /*if(!flag_clear) {
                    flag_clear=true;
                    client.onLoadResource(webView, "javascript:removeTerminal()");
                    client.onLoadResource(webView, "javascript:clearRouteLayer()");
                }*/
                client.onLoadResource(webView, "javascript:onTerminalPositionChange(" + x + "," + y + "," + l + ")");
                if ((jsonFrom == null) || (address_from.getText().equals("Текущее местоположение")))
                    if (jsonTo != null)
                        client.onLoadResource(webView, "javascript:findRouteFromCurrentPosition(" + jsonTo + "," + style_route + ")");
            }

        };

        lost_hander = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                // Gets the task from the incoming Message object.
                if (position) {
                    //  client.onLoadResource(webView, "javascript:removeTerminal()");
                    //  client.onLoadResource(webView, "javascript:clearRouteLayer()");

                    //  x = -100;
                    time = 0;
                    //  y = -10000;
                    position = false;
                }
            }

        };

        //  Timer timer = new Timer();
        //   timer.schedule(new UpdateTimeTask(), 1000, 4000); //тикаем каждую секунду без задержки
        return view;
    }


    public void initializeConsumersApi() {
        zoneManager = IBApi.getZoneManager();
        if (zoneManager != null) {
            zones = new ArrayList<>();
            for (IBZone zone : zoneManager.getAllZones()) {
                zones.add(zone.getIdentifier());
            }
        }
        //if (zoneManager != null)
        //  zoneManager.subscribeConsumer(this);
        locationManager = IBApi.getLocationManager();
        // IBLocationManager locationManager=null;
        if (locationManager != null)
            locationManager.subscribeConsumer(this);


    }

    public void onFindRoute(double x1, double y1, int l1, String name) {
        String endjson = convertXY(x1, y1, l1);
        jsonTo = endjson;
        address_here.setText(name);
        // String endjson = EndJson(""+x1, ""+y1, "0");
        if (address_from.getText().toString().isEmpty()) {
            if (x != (-100)) {
                String current_position = convertXY(x, y, 0);
                String cu = EndJson("" + x, "" + y, "" + 0);
                //  client.onLoadResource(webView, "javascript:findRoute(" + cu + "," + endjson + ")");
                client.onLoadResource(webView, "javascript:findRouteFromCurrentPosition(" + endjson + "," + style_route + ")");
            } else {
                String def = EndJson("191.164", "110.141", "0");
                address_from.setText("Вход");
                jsonFrom = def;
                client.onLoadResource(webView, "javascript:findRoute(" + def + "," + endjson + "," + style_route + ")");
            }
        } else {
            client.onLoadResource(webView, "javascript:findRoute(" + jsonFrom + "," + endjson + "," + style_route + ")");
        }
    }


    public String convertXY(double x, double y, int l) {
        int R = 6378137;
        double R_MINOR = 6356752.314245179;
        double d = Math.PI / 180;
        double ny = y * d;
        double temp = R_MINOR / R;
        double e = Math.sqrt(1 - temp * temp);
        double con = e * Math.sin(ny);
        double ts = Math.tan(Math.PI / 4 - ny / 2) / Math.pow((1 - con) / (1 + con), e / 2);
        double res_y = -R * Math.log(Math.max(ts, 1E-10));
        double res_x = x * R * d;
        String def = EndJson("" + res_x, "" + res_y, "" + l);
        return def;
    }

    @Override
    public void onCurrentZoneChange(final IBZone ib2Zone) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject style = new JSONObject();
                if (ib2Zone != null) {
                    Toast.makeText(getActivity(), "New zone = " + ib2Zone.getIdentifier(), Toast.LENGTH_SHORT).show();
                    MainActivity activity = (MainActivity) getActivity();
                    JSONObject input = new JSONObject();
                    try {
                        style.put("fillColor", "#1ACFD7");
                        style.put("weight", 2);
                        style.put("color", "#93E8EC");
                        style.put("fillOpacity", "1.0");
                        style.put("opacity", "0.5");
                        current = ib2Zone.getIdentifier();
                        input.put("application_id", ServerMethods.Application);
                        input.put("device_id", activity.getDevice_id());
                        input.put("zone_id", ib2Zone.getIdentifier());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (activity.getDevice_id() != 0) {
                        activity.getmConsumer().sendMessageZone(input);
                    }
                } else {
                    Toast.makeText(getActivity(), "New zone = lost", Toast.LENGTH_SHORT).show();
                    try {
                        style.put("fillColor", "#FFF8EB");
                        style.put("color", "#EADECE");
                        style.put("fillOpacity", "1.0");
                        style.put("opacity", "1.0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                client.onLoadResource(webView, "javascript:higlightPOI(" + current + "," + style.toString() + "," + "true" + ")");
            }
            //   }

        });
    }


    @Override
    public String getConsumerId() {
        return "ApiTest";
    }

    @Override
    public void onNewLocation(final IBLocation ibLocation) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                x = ibLocation.getX();
                y = ibLocation.getY();
                int stage=0;
                Distance z;
                l = (int) ibLocation.getN();
                if (l == 0) {
                    stage=1;
                    z = calculateDistance(activity.getFirstStage(), x, y);
                } else {
                    stage=2;
                    z = calculateDistance(activity.getSecondStage(), x, y);
                }



                if (z.getZone() != 0) {
                    if (z.getName().equals(address_here.getText().toString())) {
                        if(!show_end_dialog){
                            showDialogEnd();
                           show_end_dialog=true;
                        }
                    } else if ((route_x_end != 0)&&(address_here.getText().equals("Dostyk Plaza " + stage + " этаж"))) {
                        double res = calculateDistanceEndRoute(x, y);
                        if (res <= 6) {
                            if(!show_end_dialog){
                                showDialogEnd();
                                show_end_dialog=true;
                            }
                        }
                    }
                    JSONObject style = new JSONObject();
                    JSONObject input = new JSONObject();
                    try {
                        Toast.makeText(getActivity(), "New zone = " + z.getZone(), Toast.LENGTH_SHORT).show();
                        style.put("fillColor", "#1ACFD7");
                        style.put("weight", 2);
                        style.put("color", "#93E8EC");
                        style.put("fillOpacity", "1.0");
                        style.put("opacity", "0.5");
                        client.onLoadResource(webView, "javascript:higlightPOI(" + z.getZone() + "," + style.toString() + "," + "true" + ")");
                        input.put("application_id", ServerMethods.Application);
                        input.put("device_id", activity.getDevice_id());
                        input.put("zone_id", z);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (activity.getDevice_id() != 0) {
                        activity.getmConsumer().sendMessageZone(input);
                    }
                }
                IBLocation location = IBApi.getLocationManager().getLastLocation();
                time_last = ((IBTimedLocation) location).getTimestamp();
                position = true;
                time = System.currentTimeMillis();
                client.onLoadResource(webView, "javascript:onTerminalPositionChange(" + x + "," + y + "," + l + ")");
                if ((jsonFrom == null) || (address_from.getText().equals("Текущее местоположение")))
                    if (jsonTo != null)
                        client.onLoadResource(webView, "javascript:findRouteFromCurrentPosition(" + jsonTo + "," + style_route + ")");
                //  Message message = new Message();

                //  handler.sendMessage(message);


            }
        });

    }

    @Override
    public List<String> getZones() {
        return zones;
    }

    @Override
    public IBZoneManager.ZoneSortRule getZoneSortRule() {
        return IBZoneManager.ZoneSortRule.Power;
    }


    @Override
    public void onError(IBError ibError) {
        Log.d("errorApi", ibError.toString());
    }


    public Distance calculateDistance(List<Distance> item, double point_x, double point_y) {
        double min = 100000;
        Distance z = null;
        for (int i = 0; i < item.size(); i++) {
        double res = Math.sqrt(Math.pow(item.get(i).getX() - point_x, 2) + Math.pow(item.get(i).getY() - point_y, 2));
            if (res < min) {
                min = res;
                z = item.get(i);
            }
        }
        return z;
    }

    public double calculateDistanceEndRoute(double point_x, double point_y) {
        double res = Math.sqrt(Math.pow(route_x_end - point_x, 2) + Math.pow(route_y_end - point_y, 2));
        return res;
    }

    class UpdateTimeTask extends TimerTask {
        public void run() {
            if (time != 0) {
                double sec = (System.currentTimeMillis() - time) / 1000;
                if ((sec) > 10) {
                    lost_hander.sendMessage(new Message());
                }
            }


        }
    }

    public String EndJson(String x, String y, String l) {
        String json = "{\"x\":\"" + x + "\", \"y\":\"" + y + "\",\"l\":\"" + l + "\"}";

        return json;
    }

    class Client extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            load();
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            view.loadUrl(url);
        }
    }

    class Client1 extends WebChromeClient {


        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d("chrome", cm.message());
            Log.d("chromeLine", "" + cm.lineNumber());
            Log.d("chromeLine", "" + cm.toString());
            return true;
        }

    }


    private void load() {
        client.onLoadResource(webView, "javascript:loadMapCanvas(" + getJson("geo/dostyk_mapcanvas_1_level.json") + "," + getJson("geo/dostyk_poi_1_level.json") + ")");
        client.onLoadResource(webView, "javascript:addMapCanvas(" + getJson("geo/dostyk_mapcanvas_2_level.json") + ")");
        client.onLoadResource(webView, "javascript:loadMapPOI(" + getJson("geo/dostyk_poi_2_level.json") + ")");
        client.onLoadResource(webView, "javascript:loadRoutes(" + getJson("geo/dostyk_routegraph.json") + ")");
        String res = EndJson("163.461", "92.210", "0");
        try {
            JSONObject object = new JSONObject(res);
            if (Helper.isTabletDevice(getActivity()))
                object.put("zoom", "21");
            else object.put("zoom", "21");

            client.onLoadResource(webView, "javascript:setCenter(" + object.toString() + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeConsumersApi();


    }

    private String getJson(String path) {
        StringBuilder buf = new StringBuilder();
        InputStream json = null;
        try {
            json = getActivity().getAssets().open(path);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
            String result = buf.toString();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Callback<JSONObject> templateCalback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            if (response != null) {
                CacheManager.getInstance().saveScreen(response.getUrl(), jsonObject);
            }
            Screen.getInstance().setScreen(jsonObject);
            getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, new BaseFragment()).commit();
            act.getTest().setVisibility(View.GONE);
            act.getFrameLayout().setVisibility(View.VISIBLE);
            act.getMapFragment().startApi();
            // activity.backButton.setVisibility(View.INVISIBLE);
            if (Screen.getInstance().getScreen().length() != 0)
                act.getBackButton().setVisibility(View.VISIBLE);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
        }

    };


    public void hideDialog() {
        if (dia != null)
            dia.dismiss();
    }


    public void showDialog(final String name, final String category, final String json, final int screen, final int model, final int floor) {
        dia = new Dialog(getActivity());
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (jsonFrom == null && jsonTo == null) {
            client.onLoadResource(webView, "javascript:clearRouteLayer()");
        }
        dia.setCanceledOnTouchOutside(true);
        dia.setContentView(R.layout.register_device_dialog);
        TextView title_category = (TextView) dia.findViewById(R.id.category);
        title_category.setText(category);
        TextView title = (TextView) dia.findViewById(R.id.title);
        if (!name.isEmpty()) title.setText(name);
        else title.setText("Dostyk Plaza " + floor + " этаж");
        LinearLayout from = (LinearLayout) dia.findViewById(R.id.from);

        try {
            fo = new JSONObject(json);
            if (jsonTo != null)
                to = new JSONObject(jsonTo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setVisibility(View.VISIBLE);
                address_from.setText(name);
                if (!name.isEmpty()) address_from.setText(name);
                else address_from.setText("Dostyk Plaza " + floor + " этаж");
                jsonFrom = json;


                if (address_from.getText().equals(address_here.getText())) {
                    if (!address_from.getText().equals("Dostyk Plaza " + floor + " этаж")) {
                        if ((fo != null) && (to != null)) {
                            if (fo.optString("l").equals(to.optString("l"))) {
                                jsonTo = null;
                                address_here.setText("");
                                client.onLoadResource(webView, "javascript:clearRouteLayer()");
                            }
                        } else {
                            jsonTo = null;
                            address_here.setText("");
                            client.onLoadResource(webView, "javascript:clearRouteLayer()");

                        }
                    }
                }
                client.onLoadResource(webView, "javascript:setRouteStart(" + jsonFrom + ")");
                if (!address_here.getText().toString().isEmpty()) {
                    clear.setVisibility(View.VISIBLE);
                    client.onLoadResource(webView, "javascript:findRoute(" + jsonFrom + "," + jsonTo + "," + style_route + ")");
                }
                dia.dismiss();
            }
        });
        TextView title_route = (TextView) dia.findViewById(R.id.title_route);
        Button detail = (Button) dia.findViewById(R.id.detail);
        if (screen == 0) {
            detail.setVisibility(View.GONE);
            title_route.setVisibility(View.VISIBLE);
        }

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.exit = false;
                if (CacheManager.getInstance().isScreenCache("" + screen, "" + model)) {
                    CacheManager.getInstance().getScreen("" + screen, "" + model, templateCalback);
                } else {
                    act.getMapFragment().stopApi();
                    API.getInstance().getMethods().getDynamicScreen(screen, model, templateCalback);
                }
                dia.dismiss();
            }
        });

        LinearLayout here = (LinearLayout) dia.findViewById(R.id.here);
        here.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.setVisibility(View.VISIBLE);
                        if (!name.isEmpty()) {
                            address_here.setText(name);
                            route_x_end = 0;
                            route_y_end = 0;
                        } else {
                            address_here.setText("Dostyk Plaza " + floor + " этаж");
                            route_x_end = fo.optDouble("x");
                            route_y_end = fo.optDouble("y");
                        }
                        jsonTo = json;
                        if (address_from.getText().equals(address_here.getText())) {

                            if (!address_from.getText().equals("Dostyk Plaza " + floor + " этаж")) {
                                if ((fo != null) && (to != null)) {
                                    if (fo.optString("l").equals(to.optString("l"))) {
                                        jsonFrom = null;
                                        address_from.setText("");
                                        client.onLoadResource(webView, "javascript:clearRouteLayer()");
                                    }
                                } else {
                                    jsonFrom = null;
                                    address_from.setText("");
                                    client.onLoadResource(webView, "javascript:clearRouteLayer()");

                                }
                            }

                        }
                        client.onLoadResource(webView, "javascript:setRouteEnd(" + jsonTo + ")");
                        if (!address_from.getText().toString().isEmpty() && !address_from.getText().toString().equals("Текущее местоположение")) {
                            clear.setVisibility(View.VISIBLE);
                            client.onLoadResource(webView, "javascript:findRoute(" + jsonFrom + "," + jsonTo + "," + style_route + ")");
                        } else if (x != (-100)) {
                            address_from.setText("Текущее местоположение");
                            client.onLoadResource(webView, "javascript:findRouteFromCurrentPosition(" + jsonTo + "," + style_route + ")");
                        }

                        dia.dismiss();
                    }
                });
        ImageView close = (ImageView) dia.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });


        dia.show();


    }


    public void showDialogEnd() {
        final Dialog dialog_end = new Dialog(getActivity());
        dialog_end.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog_end.setCanceledOnTouchOutside(true);
        dialog_end.setContentView(R.layout.dialog_end_route);
        Button ok = (Button) dialog_end.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRoute();
                dialog_end.dismiss();
            }
        });


        ImageView close = (ImageView) dialog_end.findViewById(R.id.close_end_route);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRoute();
                dialog_end.dismiss();
            }
        });

        dialog_end.show();
    }

    public void startApi() {
        if (locationManager != null) {
            locationManager.subscribeConsumer(this);
         //   zoneManager.subscribeConsumer(this);
        }
    }

    public void stopApi() {
        if (locationManager != null) {
            locationManager.unsubscribeConsumer(this);
          //  zoneManager.unsubscribeConsumer(this);
        }
    }

    private LinearLayout parent;
    private Button minimize;
    private ImageView route_position;
    private ImageView clear_here;
    private Button reset_route;
    private TextView address_from;
    private TextView address_here;


    public void clearRoute() {
        route_x_end = 0;
        route_y_end = 0;
        show_end_dialog=false;
        address_from.setText("");
        address_here.setText("");
        jsonTo = null;
        jsonFrom = null;
        clear.setVisibility(View.GONE);
        parent.setVisibility(View.GONE);
        client.onLoadResource(webView, "javascript:clearRouteLayer()");
    }

}
