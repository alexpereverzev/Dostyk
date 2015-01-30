package litebonus.dostyk.interfaces;

import org.json.JSONObject;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;


/**
 * Created by prog on 15.10.2014.
 */
public interface ServerMethods {
       static final String API = "http://contentsrv.ibecom.ru/api/v1/3";
       static final String Rabbit = "amqp://device:device@contentsrv.ibecom.ru:5672";
    //  static final String API = "http://192.168.9.197:3000/api/v1/3";
    // static final String Rabbit="amqp://device:device@192.168.9.197:5672";
      static final int Application = 3;

    @GET("/template/1")
    public void getMenu(Callback<JSONObject> callback);

    @GET("/template/2")
    public void getTemplate(Callback<JSONObject> callback);

    @GET("/beacons")
    public void getGeo(Callback<JSONObject> callback);

    @GET("/template/{id}")
    public void getStaticTemplate(@Path("id") String templateId, Callback<JSONObject> callback);

    @GET("/template/{id}")
    public void getStaticTemplate(@Path("id") long templateId, Callback<JSONObject> callback);

    @GET("/template/{id}/{dataId}")
    public void getDynamicTemplate(@Path("id") long templateId, @Path("dataId") long dataId, Callback<JSONObject> callback);

    @GET("/screen/{id}")
    public void getStaticScreen(@Path("id") long screenId, Callback<JSONObject> callback);

    @GET("/screen/{id}/{dataId}")
    public void getDynamicScreen(@Path("id") long templateId, @Path("dataId") long dataId, Callback<JSONObject> callback);

    @GET("/zones/{id}")
    public void getJsonZone(@Path("id") long templateId, Callback<JSONObject> callback);

    @GET("/template/{id}")
    public void getStaticTemplateSearch(@Path("id") long templateId, @Query("1[pfield]") String pfield1, @Query("1[matching]") String pmatchin1, @Query("1[vfield]") String vfield1, Callback<JSONObject> callback);

    @GET("/template/{id}")
    public void getStaticTemplateSearchDynamic(@Path("id") long templateId, @QueryMap Map<String, String> options, Callback<JSONObject> callback);

    @GET("/screen/{id}/")
    public void getDynamicScreenDeviceId(@Path("id") long templateId, @QueryMap Map<String, String> options, Callback<JSONObject> callback);

    @GET("/zones/")
    public void getJsonZoneFlacon(Callback<JSONObject> callback);

}
