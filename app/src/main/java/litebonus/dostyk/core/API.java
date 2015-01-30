package litebonus.dostyk.core;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import litebonus.dostyk.interfaces.ServerMethods;

/**
 * Created by prog on 20.10.14.
 */
public class API {
    private static API instance;
    private ServerMethods methods;
    private API(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ServerMethods.API)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(new StringConverter()).build();
        methods = restAdapter.create(ServerMethods.class);
    }

    public static API getInstance(){
        if(instance == null){
            instance = new API();
        }
        return instance;
    }

    public ServerMethods getMethods() {
        return methods;
    }

    static class StringConverter implements Converter {

        @Override
        public Object fromBody(TypedInput typedInput, Type type) throws ConversionException {
            String text = null;
            JSONObject result = null;
            try {
                text = fromStream(typedInput.in());
                result = new JSONObject(text);
            } catch (IOException ignored) {/*NOP*/ } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Andrey", result.toString());
            return result;
        }

        @Override
        public TypedOutput toBody(Object o) {
            return null;
        }
        public static String fromStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            return out.toString();
        }
    }
}
