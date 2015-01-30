package litebonus.dostyk.activity;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import litebonus.dostyk.R;
import litebonus.dostyk.api.API;
import litebonus.dostyk.api.IntervalsProvider;
import litebonus.dostyk.core.CacheManager;
import litebonus.dostyk.core.NavigationScreen;
import litebonus.dostyk.core.Screen;
import litebonus.dostyk.core.modules.ActionModule;
import litebonus.dostyk.core.modules.PickerModule;
import litebonus.dostyk.fragments.BaseFragment;
import litebonus.dostyk.fragments.MapFragment;
import litebonus.dostyk.interfaces.ServerMethods;
import litebonus.dostyk.push.GcmIntentService;
import litebonus.dostyk.push.MessageConsumer;
import litebonus.dostyk.views.Picker;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.ibecom.api.IBApi;
import ru.ibecom.api.exceptions.IBError;

public class MainActivity extends DistanceActivity {

    private LinearLayout drawerView;
    private DrawerLayout mDrawerLayout;
    private LinearLayout backButton;

    private TextView title;
    private boolean flag_register=false;
    private View menuButton;
    private Stack<JSONObject> backStack;
    private FrameLayout frameLayout;
    private MessageConsumer mConsumer;

    public int device_id;
    boolean mBound = false;
    boolean internet=false;
    private String title_map="План (Этаж 1)";

    private static boolean register = false;
    public int getDevice_id() {
        return device_id;
    }
    private SharedPreferences preferences;
    public MessageConsumer getmConsumer() {
        return mConsumer;
    }
    private View test;
    private ViewPager viewPager;
    private ImageView tab_menu_map;
    private ImageView tab_menu_search;
    private ImageView tab_menu_favorite;
    private MapFragment mapFragment;
    public MapFragment getMapFragment() {
        return mapFragment;
    }
    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public Fragment getBase() {

        return base;
    }

    public void changeTitleMap(String s){
        title_map=s;
        setTitle(title_map);
    }

    public boolean checkBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {

            return false;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                return true;
            } else return false;
        }

    }

    private Handler check;
    public Handler getCheck() {
        return check;
    }
    String regid;

    public String getRegid() {
        return regid;
    }

    public Fragment base;
    public String SENDER_ID = "884811890037";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backStack = new Stack<>();
        preferences = getSharedPreferences("api", MODE_PRIVATE);
        if(!preferences.getString("json","").isEmpty()){
           parse(preferences.getString("json",""));
        }
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.test);
        context = this;
        setActionBar();
        test = findViewById(R.id.headlines_fragment);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().replace(R.id.headlines_fragment, mapFragment).commit();
       // initialization();
        viewPager = (ViewPager) findViewById(R.id.pager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (LinearLayout) findViewById(R.id.left_drawer);
        if (getIntent().getStringExtra("action_new") != null) {

            CacheManager.getInstance().init(context);
            if (CacheManager.getInstance().isScreenCache("32")) {
                CacheManager.getInstance().getScreen("32", new Callback<JSONObject>() {
                    @Override
                    public void success(JSONObject jsonObject, Response response) {
                        NavigationScreen.getInstance().setScreen(jsonObject);
                        createMenu();
                        try {
                            JSONObject json = new JSONObject(getIntent().getExtras().getString("action_new"));
                            int screenid = json.optInt("screenId");
                            int model = json.optInt("modelId");
                            String type = json.optString("actionType");
                            if (type.equals("navigate")) {
                                API.getInstance().getMethods().getDynamicScreen(screenid, model, templateCalback);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            } else {
                API.getInstance().getMethods().getStaticScreen(32, new Callback<JSONObject>() {
                    @Override
                    public void success(JSONObject jsonObject, Response response) {
                        NavigationScreen.getInstance().setScreen(jsonObject);
                        createMenu();
                        try {
                            JSONObject json = new JSONObject(getIntent().getExtras().getString("action_new"));
                            int screenid = json.optInt("screenId");
                            int model = json.optInt("modelId");
                            String type = json.optString("actionType");
                            if (type.equals("navigate")) {
                                API.getInstance().getMethods().getDynamicScreen(screenid, model, templateCalback);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                    }
                });

            }


            if(!IBApi.isInitialized()){
                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(mReceiver, filter);
                flag_register=true;
            }

        } else {
            createMenu();
        }



        check = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                // Gets the task from the incoming Message object.
                backButton.setVisibility(View.VISIBLE);
            }

        };
        context = this;
        tab_menu_map = (ImageView) findViewById(R.id.map);
        tab_menu_search = (ImageView) findViewById(R.id.search);
        tab_menu_favorite = (ImageView) findViewById(R.id.favorite);
        tab_menu_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit = true;
                mapFragment.hideDialog();
                mapFragment.startApi();
                ActionModule.setUpdate(false);
                if (ActionModule.isUpdate()) ActionModule.setUpdate(false);
                test.setVisibility(View.VISIBLE);
                changeTitleMap(title_map);
                backButton.setVisibility(View.INVISIBLE);
                frameLayout.setVisibility(View.GONE);
                for (Picker item : PickerModule.Grouping.getInstance().getPickerList()) {
                    item.reset();
                }
            }
        });

        tab_menu_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                exit = false;
                mapFragment.hideDialog();
                getMapFragment().stopApi();
                if (ActionModule.isUpdate()) ActionModule.setUpdate(false);
                for (Picker item : PickerModule.Grouping.getInstance().getPickerList()) {
                    item.reset();
                }
                ActionModule.setUpdate(false);
                // if(CacheManager.getInstance().isScreenCache("40")){
                //     CacheManager.getInstance().getScreen("40", templateCalbackFavorite);
                // }else {
                API.getInstance().getMethods().getStaticScreen(40, templateCalback);
                // }
            }
        });

        tab_menu_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit = false;
                mapFragment.hideDialog();
                if (ActionModule.isUpdate()) ActionModule.setUpdate(false);
                for (Picker item : PickerModule.Grouping.getInstance().getPickerList()) {
                    item.reset();
                }
                ActionModule.setUpdate(false);
                if (CacheManager.getInstance().isScreenCache("41")) {
                    CacheManager.getInstance().getScreen("41", templateCalbackFavorite);
                } else {
                    API.getInstance().getMethods().getStaticScreen(41, templateCalbackFavorite);
                }
            }
        });
     /*   mConsumer = new MessageConsumer("192.168.9.197:5672",
                "device",
                "fanout");*/
        mConsumer = new MessageConsumer("contentsrv.ibecom.ru:5672",
                "device",
                "fanout");
        //  mConsumer.startConnect();
        mConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {

            public void onReceiveMessage(byte[] message) {
                String text = "";
                try {
                    text = new String(message, "UTF8");
                    try {
                        JSONObject object = new JSONObject(text);
                        if(device_id==0)
                        device_id = object.optInt("device_id");
                        SharedPreferences.Editor pref = getSharedPreferences("device_id", MODE_PRIVATE).edit();
                        pref.putInt("device_id", device_id).commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            System.out.print(device_id);
            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                JSONObject input = new JSONObject();
                try {
                    input.put("application_id", ServerMethods.Application);
                    input.put("device_type", "android");
                    input.put("device_token", regid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mConsumer.sendMessage(input);
            }


        } else {

            Log.i("Error", "No valid Google Play Services APK found.");
        }

        changeTitleMap(title_map);

        if (getIntent().getBooleanExtra("enable", false)) {
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
            flag_register=true;
        }

        if(!isConnected()){

            IntentFilter inet_filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(InternetReceiver,inet_filter);
            internet=true;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, GcmIntentService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(myConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        if(IBApi.isInitialized())
        IBApi.releaseAPI();
        mConsumer.dispose();
        if(flag_register){
            unregisterReceiver(mReceiver);
        }
        if(internet) unregisterReceiver(InternetReceiver);

        super.onDestroy();
        //     mConsumer.dispose();
    }

    public void createMenu() {
        drawerView.addView(NavigationScreen.getInstance().createView(this));
    }

    public LinearLayout getBackButton() {
        return backButton;
    }

    private void setActionBar() {
        Toolbar actionbar = (Toolbar) findViewById(R.id.actionbar);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View customView = mInflater.inflate(R.layout.menu, actionbar);
        backButton = (LinearLayout) customView.findViewById(R.id.back_button);
        title = (TextView) customView.findViewById(R.id.title);
        menuButton = customView.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDrawerLayout.isDrawerOpen(drawerView)) {
                    mDrawerLayout.openDrawer(drawerView);
                    mapFragment.hideDialog();
                    menuButton.setFocusableInTouchMode(true);
                    menuButton.requestFocus();
                } else {
                    mDrawerLayout.closeDrawer(drawerView);
                    menuButton.setFocusableInTouchMode(false);
                    menuButton.clearFocus();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!backStack.isEmpty()) {
                    /* Screen.getInstance().setScreen(backStack.pop());
                    if(backStack.isEmpty()) {
                        backButton.setVisibility(View.INVISIBLE);

                        base = new BaseFragment();
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, base).commit();    //commitAllowingStateLoss();
                        getTest().setVisibility(View.GONE);
                    }*/
                    if(ActionModule.getPicker()!=null) ActionModule.getPicker().reset();
                    Screen.getInstance().setScreen(backStack.pop());
                    base = new BaseFragment();
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, base).commit(); ////commitAllowingStateLoss();

                } else if (backStack.isEmpty()) {
                    if(ActionModule.getPicker()!=null) ActionModule.getPicker().reset();
                    backButton.setVisibility(View.INVISIBLE);
                    getTest().setVisibility(View.VISIBLE);
                    changeTitleMap(title_map);
                    getFrameLayout().setVisibility(View.GONE);
                    exit = true;
                }
            }
        });

    }


    GcmIntentService mService;

    public ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("ServiceConnection", "connected");
            GcmIntentService.LocalBinder binder = (GcmIntentService.LocalBinder) service;
            mService = binder.getService();
            mService.setDynamicTemplateCallback(templateCalback);
            mBound = true;
        }
        //binder comes from server to communicate with method's of
        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            mService = null;
        }
    };

    public void setTitle(String title) {

        this.title.setText(title);
    }

    private Callback<JSONObject> templateCalbackFavorite = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            if (response != null) {
                CacheManager.getInstance().saveScreen(response.getUrl(), jsonObject);
            }
            Screen.getInstance().setScreen(jsonObject);
            Bundle bundle = new Bundle();
            bundle.putString("gravity", "center");
            BaseFragment baseFragment = new BaseFragment();
            baseFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, baseFragment).commit();//commitAllowingStateLoss
            backButton.setVisibility(View.INVISIBLE);
            getTest().setVisibility(View.GONE);
            getFrameLayout().setVisibility(View.VISIBLE);
        }


        @Override
        public void failure(RetrofitError retrofitError) {
            Toast.makeText(context, "Отсутствует соединение с интернетом", Toast.LENGTH_SHORT).show();
        }
    };

    private Callback<JSONObject> templateCalback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            if (response != null) {
                CacheManager.getInstance().saveScreen(response.getUrl(), jsonObject);
            }
            JSONArray array= jsonObject.optJSONArray("templates");//.optJSONObject(1).optJSONObject("template");
            JSONObject obj=array.optJSONObject(0).optJSONObject("template").optJSONArray("data").optJSONObject(0);
            if(obj!=null) {
                if (obj.length() != 1) {

                    Screen.getInstance().setScreen(jsonObject);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, new BaseFragment()).commit();////commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();
                    backButton.setVisibility(View.VISIBLE);
                    getTest().setVisibility(View.GONE);
                    getFrameLayout().setVisibility(View.VISIBLE);
                    getMapFragment().startApi();
                }
            }
            else {
                Screen.getInstance().setScreen(jsonObject);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new BaseFragment()).commit();////commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();
                backButton.setVisibility(View.VISIBLE);
                getTest().setVisibility(View.GONE);
                getFrameLayout().setVisibility(View.VISIBLE);
                getMapFragment().startApi();

            }
        }


        @Override
        public void failure(RetrofitError retrofitError) {
            Toast.makeText(context, "Отсутствует соединение с интернетом", Toast.LENGTH_SHORT).show();
        }
    };

    private Callback<JSONObject> dynamicTemplateCallback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            if (response != null) {
                CacheManager.getInstance().saveScreen(response.getUrl(), jsonObject);
            }
            Screen.getInstance().setScreen(jsonObject);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new BaseFragment()).commit();//commitAllowingStateLoss
            mDrawerLayout.closeDrawer(drawerView);
            backButton.setVisibility(View.VISIBLE);

        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Toast.makeText(context, "Отсутствует соединение с интернетом", Toast.LENGTH_SHORT).show();
        }
    };

    public Stack<JSONObject> getBackStack() {
        return backStack;
    }

    public Callback<JSONObject> getDynamicTemplateCallback() {
        return dynamicTemplateCallback;
    }

    public Callback<JSONObject> getTemplateCalback() {
        return templateCalback;
    }

    public DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }

    public LinearLayout getDrawerView() {
        return drawerView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    public View getMenuButton() {
        return menuButton;
    }


    public static boolean exit = true;

    public static boolean isExit() {
        return exit;
    }

    public static void setExit(boolean exit) {
        MainActivity.exit = exit;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(drawerView)) {
            mDrawerLayout.closeDrawer(drawerView);
            menuButton.setFocusableInTouchMode(false);
            menuButton.clearFocus();
        } else {
            if (!backStack.isEmpty()) {
                if(ActionModule.getPicker()!=null) ActionModule.getPicker().reset();
                Screen.getInstance().setScreen(backStack.pop());
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new BaseFragment()).commit();//commitAllowingStateLoss
            } else {
                backButton.setVisibility(View.INVISIBLE);
                if(ActionModule.getPicker()!=null) ActionModule.getPicker().reset();
                // if (!flag) {
                getTest().setVisibility(View.VISIBLE);
                getFrameLayout().setVisibility(View.GONE);
                setTitle(title_map);
                if (exit) {
                   super.onBackPressed();
                }
                exit = true;
            }
        }
    }


    public ViewPager getViewPager() {
        return viewPager;
    }

    public FrameLayout getFrameLayout() {
        return frameLayout;
    }

    public View getTest() {
        return test;
    }


    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);

        SharedPreferences pref = getSharedPreferences("device_id", MODE_PRIVATE);
       // device_id = pref.getInt("device_id", 0);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            // Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            // Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }



    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        //   Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                //  try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                try {
                    String regid1 = gcm.register(SENDER_ID);
                    // sendRegistrationIdToBackend();
                    regid = regid1;
                    storeRegistrationId(context, regid1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return msg;
            }


            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                JSONObject input = new JSONObject();
                try {
                    input.put("application_id", ServerMethods.Application);
                    input.put("device_type", "android");
                    input.put("device_token", regid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mConsumer.sendMessage(input);

            }
        }.execute(null, null, null);

    }

    public String generateDeviceId() {
        final String macAddr, androidId;
        WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        macAddr = wifiInf.getMacAddress();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());
        System.out.print(deviceUuid);
        return androidId;     // Maybe save this: deviceUuid.toString()); to the preferences.
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Error", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        System.out.print("");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        System.out.print("");
                        break;
                    case BluetoothAdapter.STATE_ON:
                       // if (!register) {
                            if (preferences.getString("json", "").isEmpty()) {
                                API.getInstance().getMethods().getJsonZoneFlacon(templateJsonCalback);
                            } else {
                                if (!IBApi.isInitialized()) {
                                    IntervalsProvider provider = new IntervalsProvider(context);
                                    provider.setExitImmediate(false);
                                    provider.setEnterImmediate(false);
                                    provider.setExitCounter(15);
                                    provider.setEnterCounter(3);
                                    try {
                                        parse(preferences.getString("json", ""));
                                        IBApi.initAPI(preferences.getString("json", ""));
                                    } catch (IBError ib2Error) {
                                        ib2Error.printStackTrace();
                                    }
                                    mapFragment.initializeConsumersApi();
                                }
                            }
                            register = true;
                     //   }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        System.out.print("");
                        break;
                }
            }
        }
    };


    private Callback<JSONObject> templateJsonCalback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            preferences.edit().putString("json", jsonObject.toString()).commit();
            IntervalsProvider provider = new IntervalsProvider(context);
            provider.setExitImmediate(false);
            provider.setEnterImmediate(false);
            provider.setExitCounter(15);
            provider.setEnterCounter(3);
            parse(jsonObject.toString());
            try {
                IBApi.initAPI(jsonObject.toString());
            } catch (IBError ib2Error) {
                ib2Error.printStackTrace();
            }
            mapFragment.initializeConsumersApi();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
        }
    };


    private final BroadcastReceiver InternetReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context cont, Intent intent) {

            if (isConnected())
            {
                JSONObject input = new JSONObject();
                try {
                    input.put("application_id", ServerMethods.Application);
                    input.put("device_type", "android");
                    input.put("device_token", regid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mConsumer.sendMessage(input);
              MainActivity activity=(MainActivity)context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        unregisterReceiver(InternetReceiver);
                        internet=false;
                    }
                });

          }

        }
    };

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE);
        NetworkInfo.State mobile = NetworkInfo.State.DISCONNECTED;
        if ( mobileInfo != null) {
            mobile = mobileInfo.getState();
        }
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI);
        NetworkInfo.State wifi = NetworkInfo.State.DISCONNECTED;
        if ( wifiInfo != null) {
            wifi = wifiInfo.getState();
        }
        boolean dataOnWifiOnly = (Boolean) PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(
                        "data_wifi_only", true);
        if ((!dataOnWifiOnly && (mobile.equals(NetworkInfo.State.CONNECTED) || wifi
                .equals(NetworkInfo.State.CONNECTED)))
                || (dataOnWifiOnly && wifi.equals(NetworkInfo.State.CONNECTED))) {
            return true;
        } else {
            return false;
        }
    }

}
