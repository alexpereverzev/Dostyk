package litebonus.dostyk.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import litebonus.dostyk.R;
import litebonus.dostyk.api.API;
import litebonus.dostyk.api.IntervalsProvider;
import litebonus.dostyk.core.CacheManager;
import litebonus.dostyk.core.NavigationScreen;
import litebonus.dostyk.helper.Distance;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.ibecom.api.IBApi;
import ru.ibecom.api.exceptions.IBError;

public class StartActivity extends DistanceActivity {

    private ProgressBar progressBar2;
    private TextView timer;
    private boolean flag;
    private Context context;
    private Timer timer_task;
    private boolean bluetooth_enable;
    private Intent start;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   getActionBar().hide();
        setContentView(R.layout.activity_start);
        context = this;
        preferences=getSharedPreferences("api",MODE_PRIVATE);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        timer = (TextView) findViewById(R.id.timer);

        CacheManager.getInstance().init(this);
       // exportDB();
      //  getDB("db/bkcashe.db");
        //importDB();
        bluetooth_enable = checkBluetooth();
        start = new Intent(StartActivity.this, MainActivity.class);
        if (!bluetooth_enable) start.putExtra("enable", true);

            if (!isConnected()) {
                Toast.makeText(context, "Отсутствует соединение с интернетом. Проверьте соединение и перезапустите приложение", Toast.LENGTH_LONG).show();
            }
        // if (isInternet(context)) {
        if (CacheManager.getInstance().isScreenCache("32")) {
            CacheManager.getInstance().getScreen("32", new Callback<JSONObject>() {
                @Override
                public void success(JSONObject jsonObject, Response response) {
                    NavigationScreen.getInstance().setScreen(jsonObject);
                    if(preferences.getString("json","").isEmpty()){
                        if(isInternet(context)){
                            API.getInstance().getMethods().getJsonZoneFlacon(templateJsonCalback);
                        }
                       else startActivity(start);

                    }
                    else {
                        initAp(preferences.getString("json",""));
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
                    CacheManager.getInstance().saveScreen(response.getUrl(), jsonObject);
                    NavigationScreen.getInstance().setScreen(jsonObject);
                    flag = true;
                    if(preferences.getString("json","").isEmpty()){
                        API.getInstance().getMethods().getJsonZoneFlacon(templateJsonCalback);
                    }
                    else {
                        initAp(preferences.getString("json",""));
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Toast.makeText(context,"Отсутствует соединение с интернетом",Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private int count = 0;

    public void initAp(String json){

        IntervalsProvider provider = new IntervalsProvider(context);
        provider.setExitImmediate(false);
        provider.setEnterImmediate(false);
        provider.setExitCounter(15);
        provider.setEnterCounter(3);
        try {
            IBApi.initAPI(json);


            Log.d("version",IBApi.versionString());
        } catch (IBError ib2Error) {
            ib2Error.printStackTrace();
        }
        startActivity(start);
        finish();

    }

    public boolean isInternet(Context context) {
        ConnectivityManager IM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = IM.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void startTimer(final int minuti) {
        CountDownTimer countDownTimer = new CountDownTimer(10 * minuti * 1000, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                count++;
                int barMax = 20;
                int barVal = (barMax) - ((int) (seconds / 7 * 100) + (int) (seconds % 7));
                progressBar2.setProgress(barVal);
                double sd = (barVal / barMax) * 100;
                //  timer.setText(String.format("%02d", seconds%20)+" %");
                timer.setText((count / 2) * 10 + " %");
                //  timer.setText(String.format("%02d", seconds/7) + ":" + String.format("%02d", seconds%7));
                // format the textview to show the easily readable format

                API.getInstance().getMethods().getStaticScreen(32, new Callback<JSONObject>() {
                    @Override
                    public void success(JSONObject jsonObject, Response response) {

                        //  NavigationScreen.getInstance().setScreen(jsonObject);
                        //   startActivity(new Intent(StartActivity.this, MainActivity.class));
                        //   flag=true;
                        // startActivity(new Intent(StartActivity.this, MainActivity.class));
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                        System.out.print("");
                    }
                });

            }

            @Override
            public void onFinish() {
                finish();
                if (flag) {
                    //    startActivity(new Intent(StartActivity.this, MainActivity.class));
                }
              /*  if(textTimer.getText().equals("00:00")){
                    textTimer.setText("STOP");
                }
                else{
                    textTimer.setText("2:00");
                }*/
            }
        }.start();

    }


    private Callback<JSONObject> templateJsonCalback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            preferences.edit().putString("json",jsonObject.toString()).commit();

            initAp(jsonObject.toString());

        }

        @Override
        public void failure(RetrofitError retrofitError) {

        }
    };

    private Callback<JSONObject> menuCalback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            NavigationScreen.getInstance().setScreen(jsonObject);
            flag = true;
            API.getInstance().getMethods().getJsonZoneFlacon(templateJsonCalback);
        }

        @Override
        public void failure(RetrofitError retrofitError) {

        }
    };




    class UpdateTimeTask extends TimerTask {
        public void run() {

            if (isInternet(context)) {

                API.getInstance().getMethods().getStaticScreen(32, menuCalback);

                timer_task.cancel();
            }

        }
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

    private void importDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();


            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "litebonus.dostyk"
                        + "//databases//" + "data";
                String backupDBPath  = "/BackupFolder/data";
                String ss=getAssets().toString();
              System.out.print(ss);
                File  backupDB= new File(getAssets().toString()+"/db", "bkcashe.db");
                File currentDB  = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "DCIM"
                        + File.separator, "cashe");

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

    private void getDB(String path) {

        InputStream json = null;
        OutputStream outputStream = null;
        try {
           outputStream= new FileOutputStream(new File(Environment.getExternalStorageDirectory()
                   + File.separator + "DCIM"
                   + File.separator + "cashe1.db"));
            json = getAssets().open(path);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = json.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (json != null) {
                try {
                    json.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    //exporting database
    private void exportDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data =  new File( Environment.getExternalStorageDirectory()
                    + File.separator + "DCIM"
                   , "cashe");

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "litebonus.dostyk"
                        + "//databases//" + "cashe";
                String backupDBPath  = "/DCIM/bkcashe";
                File currentDB = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "DCIM"
                        , "cashe");
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

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
