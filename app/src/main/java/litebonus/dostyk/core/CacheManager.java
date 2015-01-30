package litebonus.dostyk.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.squareup.okhttp.Cache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import litebonus.dostyk.helper.FileUtils;
import retrofit.Callback;
import litebonus.dostyk.interfaces.ServerMethods;
import ru.ibecom.api.util.FileUtil;

/**
 * Created by prog on 12.12.14.
 */
public class CacheManager {
    private static CacheManager instance;
    private Context appContext;
    private SQLiteDatabase db;

    private CacheManager(){

    }

    public static CacheManager getInstance() {
        if (instance == null){
            instance = new CacheManager();
        }
        return instance;
    }

    public  String DB_FILEPATH = Environment.getExternalStorageDirectory()
            + File.separator + "DCIM"
            + File.separator + "cashe1.db"; //"/data/data/{package_name}/databases/database.db";
    public void init(Context context){
        appContext = context.getApplicationContext();
       /* try {
            new DBHelper(appContext).importDatabase(DB_FILEPATH);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        db = new DBHelper(appContext).getWritableDatabase();
    }

    public boolean isScreenCache(String screenId, String dataId){
        String url = ServerMethods.API + "/screen/" + screenId + "/" + dataId;
        return checkUrl(url);
    }

    public boolean isScreenCache(String screenId){
        String url = ServerMethods.API + "/screen/" + screenId;
        return checkUrl(url);
    }

    private boolean checkUrl(String url){
        Cursor cursor = db.query("data", new String[]{"*"}, "data.url = ?", new String[]{url}, null, null ,null);
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void saveScreen(String url, JSONObject screen){
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("template", screen.toString());
        long id = db.insert("data", null, values);
        Log.d("DataBase", "new row id = " + id);
    }

    public void getScreen(String screenId, String dataId, Callback<JSONObject> callback){
        String url = ServerMethods.API + "/screen/" + screenId + "/" + dataId;
        getData(url, callback);
    }

    public void getScreen(String screenId, Callback<JSONObject> callback){
        String url = ServerMethods.API + "/screen/" + screenId;
        getData(url, callback);
    }

    private void getData(String url, Callback<JSONObject> callback){
        Cursor cursor = db.query("data", new String[]{"template"}, "data.url = ?", new String[]{url}, null, null ,null);
        if(cursor.moveToFirst()){
            int id = cursor.getColumnIndex("template");
            String js = cursor.getString(id);
            JSONObject result;

            try {
                result = new JSONObject(js);
            } catch (JSONException e) {
                e.printStackTrace();
                result = new JSONObject();
            }
            cursor.close();
            callback.success(result, null);
        }
        cursor.close();
        Log.d("DataBase", "get from cache");
    }

    public class DBHelper extends SQLiteOpenHelper{
        private static final String name = "cashe";
        private static final int version = 1;

        public DBHelper(Context context) {
            super(context, name, null, version);
            //super(context, Environment.getExternalStorageDirectory()
              //      + File.separator /*+ "DCIM"*/
                //    + File.separator + name, null, version);*/
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table data ("
                    + "_id integer primary key autoincrement,"
                    + "url text,"
                    + "template text"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }



        /**
         * Copies the database file at the specified location over the current
         * internal application database.
         * */
        public boolean importDatabase(String dbPath) throws IOException {

            // Close the SQLiteOpenHelper so it will commit the created empty
            // database to internal storage.
            close();
            File newDb = new File(dbPath);
            File oldDb = new File(DB_FILEPATH);
            if (newDb.exists()) {
                FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
                // Access the copied database so SQLiteHelper will cache it and mark
                // it as created.
                getWritableDatabase().close();
                return true;
            }
            return false;
        }
    }
}
