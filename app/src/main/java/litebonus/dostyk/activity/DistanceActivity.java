package litebonus.dostyk.activity;

import android.app.Activity;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import litebonus.dostyk.helper.Distance;

/**
 * Created by Alexander on 15.01.2015.
 */
public class DistanceActivity extends Activity {

    private List<Distance> firstStage;

    private List<Distance> secondStage;

    public List<Distance> getFirstStage() {
        return firstStage;
    }

    public void setFirstStage(List<Distance> firstStage) {
        this.firstStage = firstStage;
    }

    public List<Distance> getSecondStage() {
        return secondStage;
    }

    public void setSecondStage(List<Distance> secondStage) {
        this.secondStage = secondStage;
    }

    public void parse(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.optJSONArray("features");
            List<Distance> item_first = new ArrayList<>();
            List<Distance> item_second = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                Distance distance = new Distance();

                JSONArray coordinates = array.optJSONObject(i).optJSONObject("geometry").optJSONArray("coordinates");
                distance.setX(coordinates.optDouble(0));
                distance.setY(coordinates.optDouble(1));
                distance.setL(array.optJSONObject(i).optJSONObject("properties").optJSONArray("relations").optJSONObject(1).optJSONObject("reltags").optInt("value"));
                distance.setZone(array.optJSONObject(i).optJSONObject("properties").optJSONArray("relations").optJSONObject(0).optJSONObject("reltags").optInt("value"));
                distance.setName(array.optJSONObject(i).optJSONObject("properties").optJSONArray("relations").optJSONObject(0).optJSONObject("reltags").optString("name"));
                if (distance.getL() == 0)
                    item_first.add(distance);
                else {
                    item_second.add(distance);
                }
            }
            firstStage = item_first;
            secondStage = item_second;
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
