package litebonus.dostyk.core;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import litebonus.dostyk.interfaces.ApiCallback;

/**
 * Created by prog on 06.08.2014.
 */
public class ApiMethods {

    //api host
    private static final String API = "http://192.168.9.197:3000/";

    //api methods
    private static final String LIST = "api/v1/2/template/2";
    private static final String GET_MENU = "api/v1/2/template/1";
    private static final String GET_TEMPLATE = "api/v1/2/template/";
    private static final String GET_GEO = "api/v1/2/beacons";
    //////////////////////////////////

    public static void getTestData2(RequestModel model, int code){
        model.setMethod(LIST);
        new ApiAsyncTask(model, code).execute();
    }

    public static void getMenu(RequestModel model, int code){
        model.setMethod(GET_MENU);
        new ApiAsyncTask(model, code).execute();
    }

    public static void getTemplate(RequestModel model, int code){
        model.setMethod(GET_TEMPLATE);
        new ApiAsyncTask(model, code).execute();
    }

    public static void getGEOJson(RequestModel model, int code){
        model.setMethod(GET_GEO);
        new ApiAsyncTask(model, code).execute();
    }

    private static class ApiAsyncTask extends AsyncTask<Void, Void, JSONObject> {
        private RequestModel model;
        private int code;
        private ApiAsyncTask(RequestModel model, int code) {
            this.model = model;
            this.code = code;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 5000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 7000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            JSONObject jsonObject = new JSONObject();
            HttpClient httpclient = new DefaultHttpClient(httpParameters);

            String url = API + model.getMethod();
            if(model.getTemplateId() != -1){
                url = url + model.getTemplateId();
            }else if(model.getDataId() != -1){
                url = url + "/" + model.getDataId();
            }

            HttpGet httppost = new HttpGet(url);
            Log.d("API Andrey", httppost.getURI().toString());
            try {
                HttpResponse response = httpclient.execute(httppost);
                String resp = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.d("API Andrey", resp);
                return new JSONObject(resp);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    jsonObject.put("error_message", e.getMessage());
                    jsonObject.put("exception", true);
                    jsonObject.put("status", false);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }


            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            model.call(jsonObject, code);
        }
    }

    public static class RequestModel{
        private int templateId;
        private int dataId;
        private String method;
        private ApiCallback receiver;

        public RequestModel(ApiCallback receiver) {
            this.receiver = receiver;
            templateId = -1;
            dataId = -1;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public int getTemplateId() {
            return templateId;
        }

        public void setTemplateId(int templateId) {
            this.templateId = templateId;
        }

        public int getDataId() {
            return dataId;
        }

        public void setDataId(int dataId) {
            this.dataId = dataId;
        }

        public void call(JSONObject jsonObject, int code){
            receiver.statusReceive(jsonObject, code);
        }
    }
}
